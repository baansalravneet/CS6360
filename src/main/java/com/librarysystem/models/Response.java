package com.librarysystem.models;

public class Response {
    private boolean success;
    private String errorMessage;

    public Response(String errorMessage) {
        this.success = false;
        this.errorMessage = errorMessage;
    }

    public Response() {
        this.success = true;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
