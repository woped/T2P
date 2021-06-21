package de.dhbw.text2process.helper.rest;

import java.util.Arrays;

public class Response<E> {

    private E response;
    private boolean error;
    private Throwable exception;
    private String message;
    private StackTraceElement[] stackTrace;

    public Response(E response) {
        this.response = response;
    }

    public Response(boolean error, Throwable exception, String message, StackTraceElement[] stackTrace) {
        this.response = response;
        this.error = error;
        this.exception = exception;
        this.message = message;
        this.stackTrace = stackTrace;
    }

    public E getResponse() {
        return response;
    }

    public void setResponse(E response) {
        this.response = response;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public StackTraceElement[] getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(StackTraceElement[] stackTrace) {
        this.stackTrace = stackTrace;
    }

    @Override
    public String toString() {
        return "Response{" +
                "response=" + response +
                ", error=" + error +
                ", exception='" + exception + '\'' +
                ", message='" + message + '\'' +
                ", stackTrace=" + Arrays.toString(stackTrace) +
                '}';
    }
}
