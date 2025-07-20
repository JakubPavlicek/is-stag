package com.stag.identity.user.config;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.stag.academics.student.v1.StudentServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;

@Configuration
public class GrpcClientConfig {

    @Autowired
    private EurekaClient eurekaClient;

    @Bean
    public ManagedChannel studentServiceChannel(GrpcChannelFactory grpcChannelFactory) {
        InstanceInfo instanceInfo = eurekaClient.getNextServerFromEureka("student-service", false);
        return ManagedChannelBuilder.forAddress(instanceInfo.getIPAddr(), instanceInfo.getPort())
                                    .usePlaintext()
                                    .build();
    }

    @Bean
    public StudentServiceGrpc.StudentServiceBlockingStub studentServiceStub(ManagedChannel studentServiceChannel) {
        return StudentServiceGrpc.newBlockingStub(studentServiceChannel);
    }

}
