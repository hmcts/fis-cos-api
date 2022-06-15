package uk.gov.hmcts.reform.cosapi.controllers;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.cosapi.model.idam.User;
import uk.gov.hmcts.reform.cosapi.services.idam.IdamManagementService;

@RestController
@RequestMapping("/idam/dss-orhestration")
public class IdamManagementController {

    @Autowired
    IdamManagementService idamManagementService;

    @RequestMapping(
        value = "/user/details",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiOperation("Call CDAM to upload document")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Fetch User Details From Token Successfully"),
        @ApiResponse(code = 400, message = "Bad Request while fetching user details"),
        @ApiResponse(code = 401, message = "Provided Authroization token is missing or invalid"),
        @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<?> getUserDetails(@RequestHeader(value=HttpHeaders.AUTHORIZATION) String authorization) {
        User user = idamManagementService.getUserDetails(authorization);
        return ResponseEntity.ok(user);
    }
}
