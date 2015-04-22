package com.ragego.network;

/**
 * Created by philippegeek on 22/04/15.
 */
public class RageGoServerException extends RuntimeException {

    public enum ExceptionType{
        OFFLINE,
        DATA_MALFORMED,
        UNKNOWN
    }

    private final ExceptionType type;

    public RageGoServerException(ExceptionType type, Exception e) {
        super(e);
        this.type = type;
    }

    public ExceptionType getType() {
        return type;
    }
}
