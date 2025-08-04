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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ProtoValidateClientInterceptor implements ClientInterceptor {

    private final Validator validator;

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {
            @Override
            public void sendMessage(ReqT message) {
                ValidationResult result;

                try {
                    result = validator.validate((Message) message);
                } catch (ValidationException e) {
                    ProtoValidationUtils.closeCallWithValidationException(delegate(), e);
                    return;
                }

                if (!result.isSuccess()) {
                    ProtoValidationUtils.closeCallWithValidationResult(delegate(), result);
                    return;
                }

                super.sendMessage(message);
            }
        };
    }

}
