package uk.gov.hmcts.reform.cosapi.exception;


public class FeeCodeNotFoundException extends Exception {

    private static final long serialVersionUID = 7442994120484411077L;

    public FeeCodeNotFoundException(String message) {
        super(message);
    }

}
