package uk.gov.hmcts.reform.cosapi.exception;

public class DocumentUploadOrDeleteException extends RuntimeException {
    private static final long serialVersionUID = 7442994120484411078L;

    public DocumentUploadOrDeleteException(String message, Throwable cause) {
        super(message, cause);
    }

    public DocumentUploadOrDeleteException(String message) {
        super(message);
    }
}
