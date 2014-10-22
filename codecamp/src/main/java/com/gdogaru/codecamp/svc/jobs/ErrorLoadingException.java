package com.gdogaru.codecamp.svc.jobs;

/**
 * Created by Gabriel on 10/22/2014.
 */
public class ErrorLoadingException extends RuntimeException {
    public ErrorLoadingException() {
    }

    public ErrorLoadingException(String detailMessage) {
        super(detailMessage);
    }

    public ErrorLoadingException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public ErrorLoadingException(Throwable throwable) {
        super(throwable);
    }
}
