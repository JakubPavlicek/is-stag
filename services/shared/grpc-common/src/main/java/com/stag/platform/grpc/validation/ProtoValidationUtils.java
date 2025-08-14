package com.stag.platform.grpc.validation;

import build.buf.protovalidate.ValidationResult;
import build.buf.protovalidate.Violation;
import build.buf.protovalidate.exceptions.CompilationException;
import build.buf.protovalidate.exceptions.ExecutionException;
import build.buf.protovalidate.exceptions.ValidationException;
import com.google.protobuf.DescriptorProtos;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@UtilityClass
class ProtoValidationUtils {

    public static void throwInvalidArgumentException(ValidationException e) {
        log.error("Validation error: {}", e.getMessage());

        String description = getValidationExceptionMessage(e);

        throw new StatusRuntimeException(
            Status.INVALID_ARGUMENT.withDescription(description).withCause(e)
        );
    }

    public static <ReqT, RespT> void closeCallWithValidationResult(ServerCall<ReqT, RespT> call, ValidationResult result) {
        List<Violation> violations = result.getViolations();
        String errorMessage = getValidationErrorMessage(violations);

        log.error(errorMessage);

        call.close(
            Status.INVALID_ARGUMENT.withDescription(errorMessage),
            new Metadata()
        );
    }

    public static void throwInternalException(ValidationResult result) {
        List<Violation> violations = result.getViolations();
        String errorMessage = getValidationErrorMessage(violations);

        log.error(errorMessage);

        throw new StatusRuntimeException(
            Status.INTERNAL.withDescription(errorMessage)
        );
    }

    public static <ReqT, RespT> void closeCallWithValidationException(ServerCall<ReqT, RespT> call, ValidationException e) {
        log.error("Validation error: {}", e.getMessage());

        String description = getValidationExceptionMessage(e);

        call.close(
            Status.INTERNAL.withDescription(description).withCause(e),
            new Metadata()
        );
    }

    private static String getValidationErrorMessage(List<Violation> violations) {
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

    private static String getViolationMessage(Violation violation) {
        Violation.FieldValue fieldValue = violation.getFieldValue();
        Violation.FieldValue ruleValue = violation.getRuleValue();
        DescriptorProtos.FieldDescriptorProto fieldDescriptorProto = fieldValue.getDescriptor().toProto();
        DescriptorProtos.FieldDescriptorProto ruleDescriptorProto = ruleValue.getDescriptor().toProto();

        String fieldName = fieldDescriptorProto.getName();
        String fieldVal = String.valueOf(fieldValue.getValue());
        String constraint = ruleDescriptorProto.getName();
        String constraintVal = String.valueOf(ruleValue.getValue());

        return String.format("'%s'=%s violates %s=%s", fieldName, fieldVal, constraint, constraintVal);
    }

    private static String getValidationExceptionMessage(ValidationException e) {
        return switch (e) {
            case CompilationException ce -> "Validation compilation error: " + ce.getMessage();
            case ExecutionException ee -> "Validation execution error: " + ee.getMessage();
            default -> "Validation error: " + e.getMessage();
        };
    }

}