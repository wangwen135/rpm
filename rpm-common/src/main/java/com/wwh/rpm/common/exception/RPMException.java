package com.wwh.rpm.common.exception;

public class RPMException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RPMException(String message) {
	super(message);
    }

    public RPMException(Throwable cause) {
	super(cause);
    }

    public RPMException(String message, Throwable cause) {
	super(message, cause);
    }

}
