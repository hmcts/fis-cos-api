package uk.gov.hmcts.reform.cosapi.controllers;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.hmcts.reform.cosapi.services.DocumentManagementService;

@RestController
@RequestMapping("/idam/dss-orhestration")
public class IdamManagementController {

    @Autowired
    DocumentManagementService documentManagementService;

    @RequestMapping(
        value = "/user/details",
        method = RequestMethod.GET,
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiOperation("Call CDAM to upload document")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Fetch User Details From Token Successfully"),
        @ApiResponse(code = 400, message = "Bad Request while fetching user details"),
        @ApiResponse(code = 401, message = "Provided Authroization token is missing or invalid"),
        @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<?> getUserDetails(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorisation) {

        return null;
    }
}
