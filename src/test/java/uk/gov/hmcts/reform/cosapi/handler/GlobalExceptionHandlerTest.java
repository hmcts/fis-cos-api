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
import uk.gov.hmcts.reform.cosapi.exception.CaseCreateOrUpdateException;
import uk.gov.hmcts.reform.cosapi.exception.DocumentUploadOrDeleteException;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@SuppressWarnings("PMD")
public class GlobalExceptionHandlerTest {

    @InjectMocks
    GlobalExceptionHandler globalExceptionHandler;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

    }

    @Test
    public void handleDocumentUploadDeleteThroughHandler() {
        DocumentUploadOrDeleteException updException = new DocumentUploadOrDeleteException(
            "Failing while deleting the document. The error message is ",
            new Throwable()
        );

        ResponseEntity<?> exceptionResponseHandler = globalExceptionHandler.handleDocumentException(updException);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exceptionResponseHandler.getStatusCode());
        assertTrue(exceptionResponseHandler.getBody().toString().contains(
            "Failing while deleting the document. The error message is "));
    }


    @Test
    public void handleCreateCaseApiExceptionThroughExceptionHandler() {
        CaseCreateOrUpdateException updException = new CaseCreateOrUpdateException(
            "Failing while creating the case",
            new Throwable()
        );

        ResponseEntity<?> exceptionResponseHandler = globalExceptionHandler.handleDocumentException(updException);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exceptionResponseHandler.getStatusCode());
        assertTrue(exceptionResponseHandler.getBody().toString().contains("Failing while creating the case"));
    }

    @Test
    public void handleUpdateCaseApiExceptionThroughExceptionHandler() {
        CaseCreateOrUpdateException updException = new CaseCreateOrUpdateException(
            "Failing while updating the case",
            new Throwable()
        );

        ResponseEntity<?> exceptionResponseHandler = globalExceptionHandler.handleDocumentException(updException);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exceptionResponseHandler.getStatusCode());
        assertTrue(exceptionResponseHandler.getBody().toString().contains("Failing while updating the case"));
    }

    @Test
    public void handleBadRequestExceptionCausedByIllegalArgumentExceptionThroughExceptionHandler() {
        IllegalArgumentException illException = new IllegalArgumentException("Illegal Argument Exception Caught");

        ResponseEntity<?> exceptionResponseHandler = globalExceptionHandler.handleBadRequestException(illException);

        assertEquals(HttpStatus.BAD_REQUEST, exceptionResponseHandler.getStatusCode());
    }

    @Test
    public void handleBadRequestExceptionCausedByNullPointerExceptionThroughExceptionHandler() {
        NullPointerException nullException = new NullPointerException("NullPointer Exception Caught");

        ResponseEntity<?> exceptionResponseHandler = globalExceptionHandler.handleBadRequestException(nullException);

        assertEquals(HttpStatus.BAD_REQUEST, exceptionResponseHandler.getStatusCode());
    }

    @Test
    public void handleFeignExceptionServiceUnavailableExceptionThroughExceptionHandler() {

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
    public void handleFeignExceptionUnauthorizedExceptionThroughExceptionHandler() {

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
