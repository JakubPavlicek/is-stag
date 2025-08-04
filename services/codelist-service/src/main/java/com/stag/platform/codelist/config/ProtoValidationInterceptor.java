package com.stag.platform.codelist.config;

import build.buf.protovalidate.ValidationResult;
import build.buf.protovalidate.Validator;
import build.buf.protovalidate.Violation;
import build.buf.protovalidate.Violation.FieldValue;
import build.buf.protovalidate.exceptions.CompilationException;
import build.buf.protovalidate.exceptions.ExecutionException;
import build.buf.protovalidate.exceptions.ValidationException;
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto;
import com.google.protobuf.Message;
import io.grpc.ForwardingServerCallListener;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class ProtoValidationInterceptor implements ServerInterceptor {

    private final Validator validator;

    /// @see <a href="https://github.com/DanielLiu1123/grpc-starter/blob/main/grpc-extensions/grpc-validation/src/main/java/grpcstarter/extensions/validation/ProtoValidateServerInterceptor.java">ProtoValidateServerInterceptor</a>
    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
        ServerCall<ReqT, RespT> call,
        Metadata headers,
        ServerCallHandler<ReqT, RespT> next
    ) {
        return new ForwardingServerCallListener.SimpleForwardingServerCallListener<>(next.startCall(call, headers)) {
            private boolean aborted = false;

            @Override
            public void onMessage(ReqT message) {
                ValidationResult result;

                try {
                    result = validator.validate((Message) message);
                } catch (ValidationException e) {
                    aborted = true;
                    closeCallWithValidationException(call, e);
                    return;
                }

                if (!result.isSuccess()) {
                    aborted = true;
                    closeCallWithValidationResult(call, result);
                    return;
                }

                super.onMessage(message);
            }

            @Override
            public void onHalfClose() {
                // Prevent IllegalStateException: call already closed
                if (!aborted) {
                    super.onHalfClose();
                }
            }
        };
    }

    private <ReqT, RespT> void closeCallWithValidationResult(ServerCall<ReqT, RespT> call, ValidationResult result) {
        List<Violation> violations = result.getViolations();

        String errorMessage = getValidationErrorMessage(violations);

        closeCallWithMessage(call, errorMessage);
    }

    private String getValidationErrorMessage(List<Violation> violations) {
        StringBuilder stringBuilder = new StringBuilder("Validation failed: ");

        for (int i = 0; i < violations.size(); i++) {
            Violation violation = violations.get(i);
            String violationMessage = getViolationMessage(violation);

            stringBuilder.append(violationMessage);

            if (i < violations.size() - 1) {
                stringBuilder.append(", ");
            }
        }

        return stringBuilder.toString();
    }

    private String getViolationMessage(Violation violation) {
        FieldValue fieldValue = violation.getFieldValue();
        FieldValue ruleValue = violation.getRuleValue();
        FieldDescriptorProto fieldDescriptorProto = fieldValue.getDescriptor().toProto();
        FieldDescriptorProto ruleDescriptorProto = ruleValue.getDescriptor().toProto();

        String fieldName = fieldDescriptorProto.getName();
        String fieldVal = String.valueOf(fieldValue.getValue());
        String constraint = ruleDescriptorProto.getName();
        String constraintVal = String.valueOf(ruleValue.getValue());

        return String.format("'%s'=%s violates %s=%s", fieldName, fieldVal, constraint, constraintVal);
    }

    private <ReqT, RespT> void closeCallWithMessage(ServerCall<ReqT, RespT> call, String message) {
        log.error(message);

        call.close(
            Status.INVALID_ARGUMENT.withDescription(message),
            new Metadata()
        );
    }

    private <ReqT, RespT> void closeCallWithValidationException(ServerCall<ReqT, RespT> call, ValidationException e) {
        log.error("Validation error: {}", e.getMessage());

        String description = switch (e) {
            case CompilationException ce -> "Validation compilation error: " + ce.getMessage();
            case ExecutionException ee -> "Validation execution error: " + ee.getMessage();
            default -> "Validation error: " + e.getMessage();
        };

        call.close(
            Status.INTERNAL.withDescription(description).withCause(e),
            new Metadata()
        );
    }

}