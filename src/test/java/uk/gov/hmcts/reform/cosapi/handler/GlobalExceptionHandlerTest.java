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
import static uk.gov.hmcts.reform.cosapi.util.TestConstant.TEST_URL;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class GlobalExceptionHandlerTest {

    @InjectMocks
    GlobalExceptionHandler globalExceptionHandler;

    @Before
    void setUp() {
        MockitoAnnotations.openMocks(this);

    }

    @Test
    void handleDocumentUploadDeleteThroughHandler() {
        DocumentUploadOrDeleteException updException = new DocumentUploadOrDeleteException(
            "Failing while deleting the document. The error message is ",
            new RuntimeException()
        );

        ResponseEntity<?> exceptionResponseHandler = globalExceptionHandler.handleDocumentException(updException);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,
                     exceptionResponseHandler.getStatusCode());

        assertTrue(exceptionResponseHandler.getBody().toString().contains(
            "Failing while deleting the document. The error message is "));
    }


    @Test
    void handleCreateCaseApiExceptionThroughExceptionHandler() {
        CaseCreateOrUpdateException updException = new CaseCreateOrUpdateException(
            "Failing while creating the case",
            new RuntimeException()
        );

        ResponseEntity<?> exceptionResponseHandler = globalExceptionHandler.handleDocumentException(updException);

        assertEquals(500, exceptionResponseHandler.getStatusCodeValue());
        assertTrue(exceptionResponseHandler.getBody().toString().contains("Failing while creating the case"));
    }

    @Test
    void handleUpdateCaseApiExceptionThroughExceptionHandler() {
        CaseCreateOrUpdateException updException = new CaseCreateOrUpdateException(
            "Failing while updating the case",
            new RuntimeException()
        );

        ResponseEntity<?> exceptionResponseHandler = globalExceptionHandler.handleDocumentException(updException);

        assertEquals(500, exceptionResponseHandler.getStatusCodeValue());
        assertTrue(exceptionResponseHandler.getBody().toString().contains("Failing while updating the case"));
    }

    @Test
    void handleBadRequestExceptionCausedByIllegalArgumentExceptionThroughExceptionHandler() {
        IllegalArgumentException illException = new IllegalArgumentException("Illegal Argument Exception Caught");

        ResponseEntity<?> exceptionResponseHandler = globalExceptionHandler.handleBadRequestException(illException);

        assertEquals(400, exceptionResponseHandler.getStatusCodeValue());
    }

    @Test
    void handleBadRequestExceptionCausedByNullPointerExceptionThroughExceptionHandler() {
        NullPointerException nullException = new NullPointerException("NullPointer Exception Caught");

        ResponseEntity<?> exceptionResponseHandler = globalExceptionHandler.handleBadRequestException(nullException);

        assertEquals(400, exceptionResponseHandler.getStatusCodeValue());
    }

    @Test
    void handleFeignExceptionServiceUnavailableExceptionThroughExceptionHandler() {

        final byte[] emptyBody = {};
        Request request = Request.create(Request.HttpMethod.GET, TEST_URL,
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
        Request request = Request.create(Request.HttpMethod.GET, TEST_URL,
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
