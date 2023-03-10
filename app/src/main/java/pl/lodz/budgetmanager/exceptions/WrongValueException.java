package pl.lodz.budgetmanager.exceptions;

public class WrongValueException extends Exception {
    public WrongValueException() {
    }

    public WrongValueException(String message) {
        super(message);
    }

    public WrongValueException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongValueException(Throwable cause) {
        super(cause);
    }

    public WrongValueException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
