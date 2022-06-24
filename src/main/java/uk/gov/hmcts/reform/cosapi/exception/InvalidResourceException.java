package uk.gov.hmcts.reform.cosapi.exception;

import java.io.IOException;

public class InvalidResourceException extends IOException {
    private static final long serialVersionUID = 7442120412671078L;

    public InvalidResourceException(String message, Throwable cause) {
        super(message, cause);
    }
}
