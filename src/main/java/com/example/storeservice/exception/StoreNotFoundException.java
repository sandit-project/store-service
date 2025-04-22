package com.example.storeservice.exception;

public class StoreNotFoundException extends RuntimeException {
    public StoreNotFoundException(Long uid) {
        super("Store does not exists: " + uid);
    }
}
