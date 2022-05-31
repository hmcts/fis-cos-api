package uk.gov.hmcts.reform.cosapi.controllers;

import java.math.BigDecimal;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.cosapi.model.FeeResponse;
import uk.gov.hmcts.reform.cosapi.services.FeeRetrieveService;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class FeeRetrieveControllerTest {


    @InjectMocks
    private FeeRetrieveController feeRetrieveController;


    @Mock
    private FeeRetrieveService feeRetrieveService;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

    }

    @Test
     void testFeeController() throws Exception {
        FeeResponse  feeResponse = FeeResponse.builder().code("FEE0310")
            .feeAmount(BigDecimal.valueOf(183.00)).build();
        when(feeRetrieveService.retrieveFeeDetails("APPLY_ADOPTION")).thenReturn(feeResponse);
        BigDecimal feeAmount = feeRetrieveController.populateFees("APPLY_ADOPTION").getFeeAmount();
        assertEquals(BigDecimal.valueOf(183.00), feeAmount);
    }
}

