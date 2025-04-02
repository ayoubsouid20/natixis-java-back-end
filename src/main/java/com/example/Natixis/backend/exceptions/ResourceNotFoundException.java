package com.example.Natixis.backend.exceptions;

// this class is created to handle not found exception
// when performing get or delete or any kind of fetch

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}