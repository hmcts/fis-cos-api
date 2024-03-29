package uk.gov.hmcts.reform.cosapi.handler;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import uk.gov.hmcts.reform.cosapi.exception.CaseCreateOrUpdateException;
import uk.gov.hmcts.reform.cosapi.exception.DocumentUploadOrDeleteException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({IllegalArgumentException.class, NullPointerException.class})
    public ResponseEntity<Object> handleBadRequestException(Exception exception) {
        log.warn(exception.getMessage(), exception);
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler({DocumentUploadOrDeleteException.class})
    public ResponseEntity<Object> handleDocumentException(Exception exception) {
        log.error(exception.getMessage(), exception);

        if (exception.getCause() instanceof HttpClientErrorException) {
            HttpStatus httpClientErrorException = ((HttpClientErrorException) exception.getCause()).getStatusCode();

            if (httpClientErrorException == HttpStatus.BAD_REQUEST) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(exception.getMessage());
            } else {
                return ResponseEntity.status(httpClientErrorException).body(exception.getMessage());
            }
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
    }

    @ExceptionHandler({CaseCreateOrUpdateException.class})
    public ResponseEntity<Object> handleCaseApiException(Exception exception) {
        log.error(exception.getMessage(), exception);

        if (exception.getCause() instanceof FeignException) {
            HttpStatus httpClientErrorException = HttpStatus.valueOf(((FeignException) exception.getCause()).status());

            if (httpClientErrorException == HttpStatus.BAD_REQUEST) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(exception.getMessage());
            } else if (httpClientErrorException == HttpStatus.NOT_FOUND) {
                return ResponseEntity.status(httpClientErrorException).body(exception.getMessage());
            } else {
                return ResponseEntity.status(httpClientErrorException).body(exception.getMessage());
            }
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
    }

    @ExceptionHandler({FeignException.ServiceUnavailable.class})
    public ResponseEntity<Object> handleFeignExceptionServiceUnavailableException(Exception exception) {
        log.error(exception.getMessage(), exception);

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(exception.getMessage());
    }

    @ExceptionHandler({FeignException.Unauthorized.class})
    public ResponseEntity<Object> handleFeignExceptionUnauthorizedException(Exception exception) {
        log.error(exception.getMessage(), exception);

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(exception.getMessage());
    }

}
