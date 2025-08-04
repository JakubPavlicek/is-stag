package com.stag.platform.grpc.validation;

import build.buf.protovalidate.ValidationResult;
import build.buf.protovalidate.Validator;
import build.buf.protovalidate.exceptions.ValidationException;
import com.google.protobuf.Message;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall;
import io.grpc.MethodDescriptor;
import lombok.RequiredArgsConstructor;

/// @see <a href="https://github.com/DanielLiu1123/grpc-starter/blob/main/grpc-extensions/grpc-validation/src/main/java/grpcstarter/extensions/validation/ProtoValidateClientInterceptor.java">grpc-starter ProtoValidateClientInterceptor.java</a>
@RequiredArgsConstructor
public class ProtoValidateClientInterceptor implements ClientInterceptor {

    private final Validator validator;

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
        MethodDescriptor<ReqT, RespT> method,
        CallOptions callOptions,
        Channel next
    ) {
        return new ForwardingClientCall.SimpleForwardingClientCall<>(next.newCall(method, callOptions)) {
            @Override
            public void sendMessage(ReqT message) {
                ValidationResult result;

                try {
                    result = validator.validate((Message) message);
                } catch (ValidationException e) {
                    ProtoValidationUtils.throwInvalidArgumentException(e);
                    return;
                }

                if (!result.isSuccess()) {
                    ProtoValidationUtils.throwInternalException(result);
                    return;
                }

                super.sendMessage(message);
            }
        };
    }

}
