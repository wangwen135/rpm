package com.wwh.rpm.common.exception;

public class ConfigException extends RPMException {

    private static final long serialVersionUID = 1L;

    public ConfigException(String message) {
	super(message);
    }

    public ConfigException(String message, Throwable cause) {
	super(message, cause);
    }

    public ConfigException(Throwable cause) {
	super(cause);
    }

}
