package com.aravind.projectmanagementapi.exception;

public class ResourceAlreadyExistsException
        extends RuntimeException {

    public ResourceAlreadyExistsException(String message) {
        super(message);
    }
}