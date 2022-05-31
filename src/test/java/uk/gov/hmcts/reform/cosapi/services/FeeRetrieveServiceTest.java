package uk.gov.hmcts.reform.cosapi.services;

import feign.FeignException;
import java.math.BigDecimal;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.cosapi.clients.RegisterFeeAPI;
import uk.gov.hmcts.reform.cosapi.config.FeeRetrieveConfiguration;
import uk.gov.hmcts.reform.cosapi.model.FeeResponse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class FeeRetrieveServiceTest {

    @InjectMocks
    FeeRetrieveService feeRetrieveService;

    @Mock
    RegisterFeeAPI registerFeeAPI;

    @Mock
    FeeResponse feeResponse;

    @Mock
    FeeRetrieveConfiguration feeRetrieveConfiguration;

    @Mock
    FeeRetrieveConfiguration.FeeParameters parameters;


    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        feeResponse = FeeResponse.builder().code("FEE0310")
            .feeAmount(BigDecimal.valueOf(183.00)).build();


    }

    @Test
    public void testForApplyAdoptionWithCorrectFeeAmount() throws Exception {

        FeeRetrieveConfiguration.FeeParameters feeParameters = FeeRetrieveConfiguration.FeeParameters
            .builder()
            .channel("default")
            .event("miscellaneous")
            .service("private law")
            .jurisdiction1("family")
            .jurisdiction2("family court")
            .keyword("ChildArrangements")
            .build();

        when(feeRetrieveConfiguration.getFeeParametersByFeeType("APPLY_ADOPTION")).thenReturn(
            feeParameters);
        when(feeRetrieveService.retrieveFeeDetails("APPLY_ADOPTION")).thenReturn(feeResponse);
        BigDecimal actualResult = feeRetrieveService.retrieveFeeDetails("APPLY_ADOPTION").getFeeAmount();

        assertEquals(BigDecimal.valueOf(183.00), actualResult);


    }

    @Test
    public void testForApplyAdoptionWithWrongFeeAmount() throws Exception {

        FeeRetrieveConfiguration.FeeParameters feeParameters = FeeRetrieveConfiguration.FeeParameters
            .builder()
            .channel("default")
            .event("miscellaneous")
            .service("private law")
            .jurisdiction1("family")
            .jurisdiction2("family court")
            .keyword("ChildArrangements")
            .build();

        when(feeRetrieveConfiguration.getFeeParametersByFeeType("APPLY_ADOPTION")).thenReturn(
            parameters);
        when(feeRetrieveService.retrieveFeeDetails("APPLY_ADOPTION")).thenReturn(feeResponse);
        BigDecimal actualResult = feeRetrieveService.retrieveFeeDetails("APPLY_ADOPTION").getFeeAmount();

        assertNotEquals(BigDecimal.valueOf(100.00), actualResult);


    }


    @Test
    public void whenFeeDetailsNotFetchedThrowError() throws Exception {

        FeeRetrieveConfiguration.FeeParameters feeParameters = FeeRetrieveConfiguration.FeeParameters
            .builder()
            .channel("default")
            .event("miscellaneous")
            .service("private law")
            .jurisdiction1("family")
            .jurisdiction2("family court")
            .keyword("ChildArrangements")
            .build();

        when(feeRetrieveConfiguration.getFeeParametersByFeeType("APPLY_ADOPTION")).thenReturn(
            feeParameters);
        assertNotNull(when(feeRetrieveService.retrieveFeeDetails("APPLY_ADOPTION")).thenThrow(
            FeignException.class));
    }
}
