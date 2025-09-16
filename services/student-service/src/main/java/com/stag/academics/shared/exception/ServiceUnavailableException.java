package com.stag.academics.shared.exception;

import lombok.Getter;

@Getter
public class ServiceUnavailableException extends RuntimeException {

    private final String serviceName;

    public ServiceUnavailableException(String serviceName, Throwable cause) {
        super(serviceName + " is currently unavailable", cause);
        this.serviceName = serviceName;
    }

}
