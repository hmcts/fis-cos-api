package uk.gov.hmcts.reform.cosapi.controllers;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.cosapi.model.FeeApplicationType;
import uk.gov.hmcts.reform.cosapi.model.FeeResponse;
import uk.gov.hmcts.reform.cosapi.services.FeeRetrieveService;
//import static javax.ws.rs.core.MediaType.APPLICATION_JSON;


@Slf4j
@RestController
@RequiredArgsConstructor
public class FeeRetrieveController {

    @Autowired
    FeeRetrieveService feeRetrieveService;

    @PostMapping(path = "/retrieveFeeDetails")
    @ApiOperation(value = "Call to retrieve fee details ")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Fee received."),
        @ApiResponse(code = 400, message = "Bad Request")})
    public FeeResponse populateFees() throws Exception {
        FeeResponse feeResponse = feeRetrieveService.retrieveFeeDetails(FeeApplicationType.APPLY_ADOPTION);
           return feeResponse;
    }
}
