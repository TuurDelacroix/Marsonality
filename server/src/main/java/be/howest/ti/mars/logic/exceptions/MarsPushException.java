package be.howest.ti.mars.logic.exceptions;

public class MarsPushException extends RuntimeException {

    public static final long serialVersionUID = 10000;

    public MarsPushException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
