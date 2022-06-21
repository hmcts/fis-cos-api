package uk.gov.hmcts.reform.cosapi.handler;

import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;
import uk.gov.hmcts.reform.cosapi.exception.CaseCreateOrUpdateException;
import uk.gov.hmcts.reform.cosapi.exception.DocumentUploadOrDeleteException;

import java.io.IOException;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class GlobalExceptionHandlerTest {
    private static final String DELETE_DOCUMENT_FAILURE_MSG =
        "Failing while deleting the document. The error message is ";

    @InjectMocks
    GlobalExceptionHandler globalExceptionHandler;

    @Before
    void setUp() {
        MockitoAnnotations.openMocks(this);

    }

    @Test
    void handleDocumentUploadDeleteThroughHandlerNonHttpClientErrorException() {
        DocumentUploadOrDeleteException updException = new DocumentUploadOrDeleteException(
            DELETE_DOCUMENT_FAILURE_MSG,
            new IOException()
        );

        ResponseEntity<Object> exceptionResponseHandler = globalExceptionHandler.handleDocumentException(updException);

        assertEquals(
            HttpStatus.INTERNAL_SERVER_ERROR,
            exceptionResponseHandler.getStatusCode()
        );

        assertInstanceOf(IOException.class, updException.getCause());
        assertFalse(updException.getCause() instanceof HttpClientErrorException);

        assertEquals(
            exceptionResponseHandler.getBody(),
            DELETE_DOCUMENT_FAILURE_MSG
        );
    }

    @Test
    void handleDocumentUploadDeleteThroughHandlerInstanceOfHttpClientErrorException() {
        DocumentUploadOrDeleteException updException = new DocumentUploadOrDeleteException(
            DELETE_DOCUMENT_FAILURE_MSG,
            new HttpClientErrorException(HttpStatus.BAD_REQUEST)
        );

        ResponseEntity<Object> exceptionResponseHandler = globalExceptionHandler.handleDocumentException(updException);

        assertEquals(
            HttpStatus.SERVICE_UNAVAILABLE,
            exceptionResponseHandler.getStatusCode()
        );

        assertInstanceOf(HttpClientErrorException.class, updException.getCause());
        assertTrue(exceptionResponseHandler.getBody().toString().contains(
            DELETE_DOCUMENT_FAILURE_MSG));
    }


    @Test
    void handleCreateCaseApiExceptionThroughExceptionHandler() {
        CaseCreateOrUpdateException updException = new CaseCreateOrUpdateException(
            "Failing while creating the case",
            new RuntimeException()
        );

        ResponseEntity<?> exceptionResponseHandler = globalExceptionHandler.handleCaseApiException(updException);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exceptionResponseHandler.getStatusCode());
        assertTrue(exceptionResponseHandler.getBody().toString().contains("Failing while creating the case"));
    }

    @Test
    void handleUpdateCaseApiExceptionThroughExceptionHandlerInstanceOfHttpClientErrorException() {
        CaseCreateOrUpdateException updException = new CaseCreateOrUpdateException(
            "Failing while updating the case",
            new HttpClientErrorException(HttpStatus.BAD_REQUEST)
        );

        ResponseEntity<Object> exceptionResponseHandler = globalExceptionHandler.handleCaseApiException(updException);

        assertEquals(
            HttpStatus.SERVICE_UNAVAILABLE,
            exceptionResponseHandler.getStatusCode()
        );

        assertInstanceOf(HttpClientErrorException.class, updException.getCause());
        assertTrue(exceptionResponseHandler.getBody().toString().contains("Failing while updating the case"));
    }

    @Test
    void handleBadRequestExceptionCausedByIllegalArgumentExceptionThroughExceptionHandler() {
        IllegalArgumentException illException = new IllegalArgumentException("Illegal Argument Exception Caught");

        ResponseEntity<?> exceptionResponseHandler = globalExceptionHandler.handleBadRequestException(illException);

        assertEquals(HttpStatus.BAD_REQUEST, exceptionResponseHandler.getStatusCode());
    }

    @Test
    void handleBadRequestExceptionCausedByNullPointerExceptionThroughExceptionHandler() {
        NullPointerException nullException = new NullPointerException("NullPointer Exception Caught");

        ResponseEntity<?> exceptionResponseHandler = globalExceptionHandler.handleBadRequestException(nullException);

        assertEquals(HttpStatus.BAD_REQUEST, exceptionResponseHandler.getStatusCode());
    }

    @Test
    void handleFeignExceptionServiceUnavailableExceptionThroughExceptionHandler() {

        final byte[] emptyBody = {};
        Request request = Request.create(Request.HttpMethod.GET, "url",
                                         new HashMap<>(), null, new RequestTemplate()
        );

        FeignException serviceUnavailableException = new FeignException.ServiceUnavailable(
            "Service unavailable",
            request,
            null,
            null
        );

        ResponseEntity<?> serviceUnavailableResponseHandler =
            globalExceptionHandler.handleFeignExceptionServiceUnavailableException(serviceUnavailableException);

        assertEquals(serviceUnavailableException.getMessage(), serviceUnavailableResponseHandler.getBody());
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, serviceUnavailableResponseHandler.getStatusCode());
    }

    @Test
    void handleFeignExceptionUnauthorizedExceptionThroughExceptionHandler() {

        final byte[] emptyBody = {};
        Request request = Request.create(Request.HttpMethod.GET, "url",
                                         new HashMap<>(), null, new RequestTemplate()
        );

        FeignException unauthorizedException = new FeignException.Unauthorized(
            "Unauthorized message",
            request,
            null,
            null
        );

        ResponseEntity<?> unauthorizedResponseHandler =
            globalExceptionHandler.handleFeignExceptionUnauthorizedException(unauthorizedException);

        assertEquals(unauthorizedException.getMessage(), unauthorizedResponseHandler.getBody());

        assertEquals(HttpStatus.UNAUTHORIZED, unauthorizedResponseHandler.getStatusCode());
    }
}
