package uk.gov.hmcts.reform.cosapi.controllers;

import com.microsoft.applicationinsights.boot.dependencies.apachecommons.lang3.StringUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.cosapi.model.idam.IdamUser;
import uk.gov.hmcts.reform.cosapi.services.idam.IdamManagementService;

import java.util.Optional;

@RestController
@RequestMapping("/idam/dss-orhestration")
public class IdamManagementController {

    @Autowired
    private IdamManagementService idamManagementService;

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
    public ResponseEntity<?> getUserDetails(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        if (StringUtils.isEmpty(authorization)) {
            return ResponseEntity.badRequest().body("Authorization Access Token Is Empty");
        } else {
            Optional<IdamUser> user = Optional.ofNullable(idamManagementService.getUserDetails(authorization));
            if (user.isPresent()) {
                return ResponseEntity.ok(user.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    "User details not found for the given user access token");
            }
        }
    }
}
