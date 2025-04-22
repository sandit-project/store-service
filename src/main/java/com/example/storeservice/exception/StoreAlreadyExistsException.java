package com.example.storeservice.exception;

public class StoreAlreadyExistsException extends Throwable {
    public StoreAlreadyExistsException(String storeName) {
        super("Store already exists: " + storeName);
    }
}
