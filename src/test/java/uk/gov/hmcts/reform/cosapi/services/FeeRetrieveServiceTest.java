package uk.gov.hmcts.reform.cosapi.services;

import java.math.BigDecimal;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.hmcts.reform.cosapi.clients.RegisterFeeAPI;
import uk.gov.hmcts.reform.cosapi.config.FeeRetrieveConfiguration;
import uk.gov.hmcts.reform.cosapi.model.FeeApplicationType;
import uk.gov.hmcts.reform.cosapi.model.FeeResponse;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
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

       FeeRetrieveConfiguration.FeeParameters  feeParameters = FeeRetrieveConfiguration.FeeParameters
            .builder()
            .channel("default")
            .event("miscellaneous")
            .service("private law")
            .jurisdiction1("family")
            .jurisdiction2("family court")
            .keyword("ChildArrangements")
            .build();

        when(feeRetrieveConfiguration.getFeeParametersByFeeType(FeeApplicationType.APPLY_ADOPTION)).thenReturn(parameters);
        when(feeRetrieveService.retrieveFeeDetails(FeeApplicationType.APPLY_ADOPTION)).thenReturn(feeResponse);

        assertEquals(feeRetrieveConfiguration.getFeeParametersByFeeType(FeeApplicationType.APPLY_ADOPTION),parameters);

        when(feeRetrieveService.retrieveFeeDetails(FeeApplicationType.APPLY_ADOPTION)).thenReturn(feeResponse);
        assertEquals(feeRetrieveService.retrieveFeeDetails(FeeApplicationType.APPLY_ADOPTION),feeResponse);
        BigDecimal actualResult = feeRetrieveService.retrieveFeeDetails(FeeApplicationType.APPLY_ADOPTION).getFeeAmount();

        assertEquals(BigDecimal.valueOf(232.00), actualResult);


    }
}
