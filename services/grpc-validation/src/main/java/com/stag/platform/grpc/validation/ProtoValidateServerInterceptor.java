package com.stag.platform.grpc.validation;

import build.buf.protovalidate.ValidationResult;
import build.buf.protovalidate.Validator;
import build.buf.protovalidate.exceptions.ValidationException;
import com.google.protobuf.Message;
import io.grpc.ForwardingServerCallListener;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProtoValidateServerInterceptor implements ServerInterceptor {

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
                    ProtoValidationUtils.closeCallWithValidationException(call, e);
                    return;
                }

                if (!result.isSuccess()) {
                    aborted = true;
                    ProtoValidationUtils.closeCallWithValidationResult(call, result);
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

}
