package uk.gov.hmcts.reform.cosapi.controllers;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.cosapi.exception.FeeCodeNotFoundException;
import uk.gov.hmcts.reform.cosapi.model.FeeResponse;
import uk.gov.hmcts.reform.cosapi.services.FeeRetrieveService;



@Slf4j
@RestController
@RequiredArgsConstructor
public class FeeRetrieveController {

    @Autowired
    FeeRetrieveService feeRetrieveService;

    @PostMapping(path = "/retrieveFeeDetails/{caseType}")
    @ApiOperation(value = "Call to retrieve fee details ")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Fee received.")})
    public FeeResponse populateFees(@PathVariable String caseType) {

        FeeResponse feeResponse = null;
        try {
            feeResponse = feeRetrieveService.retrieveFeeDetails(caseType);
            return feeResponse;
        } catch (FeeCodeNotFoundException e) {
            feeResponse = feeResponse.builder()
                .description("Fee Code is not valid").build();
            return feeResponse;
        }
    }
}
