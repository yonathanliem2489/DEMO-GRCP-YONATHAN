package com.tiket.tix.train.libs.grpc.model.validation;

public interface Validator {

    void validate(String protoName, String fieldName, Object fieldValue, Object extensionValue) throws IllegalArgumentException;
}
