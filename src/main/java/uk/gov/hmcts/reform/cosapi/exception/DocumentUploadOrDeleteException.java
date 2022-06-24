package uk.gov.hmcts.reform.cosapi.exception;

public class DocumentUploadOrDeleteException extends RuntimeException {
    private static final long serialVersionUID = 7442120415331078L;

    public DocumentUploadOrDeleteException(String message, Throwable cause) {
        super(message, cause);
    }
}
