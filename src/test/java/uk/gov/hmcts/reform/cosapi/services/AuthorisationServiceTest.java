package uk.gov.hmcts.reform.cosapi.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.reform.authorisation.ServiceAuthorisationApi;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorisationServiceTest {

    @InjectMocks
    AuthorisationService authorisationService;

    @Mock
    ServiceAuthorisationApi serviceAuthorisationApi;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(authorisationService, "s2sAuthorisedServices", "payment_api");
    }

    @Test
    void authoriseWhenTheServiceIsCalledFromPayment() {

        when(serviceAuthorisationApi.getServiceName(any())).thenReturn("payment_api");
        assertTrue(authorisationService.authoriseService("Bearer abcasda"));

    }

    @Test
    void doNotAuthoriseWhenTheServiceIsCalledFromUnknownApi() {
        when(serviceAuthorisationApi.getServiceName(any())).thenReturn("unknown_api");
        assertFalse(authorisationService.authoriseService("Bearer abc"));

    }

    @Test
    void throwUnAuthorisedExceptionWhenS2sTokenIsMalformed() {
        assertFalse(authorisationService.authoriseService("Bearer malformed"));
    }
}
