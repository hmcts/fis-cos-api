package uk.gov.hmcts.reform.cosapi.controllers;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.cosapi.edgecase.event.EventEnum;
import uk.gov.hmcts.reform.cosapi.edgecase.model.CaseData;
import uk.gov.hmcts.reform.cosapi.exception.CaseCreateOrUpdateException;
import uk.gov.hmcts.reform.cosapi.model.CaseResponse;
import uk.gov.hmcts.reform.cosapi.model.DssCaseRequest;
import uk.gov.hmcts.reform.cosapi.model.DssCaseResponse;
import uk.gov.hmcts.reform.cosapi.services.AuthorisationService;
import uk.gov.hmcts.reform.cosapi.services.CaseManagementService;
import uk.gov.hmcts.reform.cosapi.services.SystemUserService;

@RestController
@RequestMapping("/case/dss-orchestration")
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CaseManagementController {

    public static final String SERVICE_AUTHORISATION = "ServiceAuthorization";

    private final CaseManagementService caseManagementService;

    private final SystemUserService systemUserService;

    private final AuthorisationService authorisationService;

    @PostMapping("/create")
    @ApiOperation("Call CCD to create case")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = "created"),
        @ApiResponse(code = 401, message = "Provided Authroization token is missing or invalid"),
        @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<CaseResponse> createCase(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorisation,
                                        @RequestBody final CaseData caseData) {

        CaseResponse createdCase = caseManagementService.createCase(authorisation, caseData);
        return ResponseEntity.ok(createdCase);
    }

    @PutMapping("/{caseId}/update")
    @ApiOperation("Call CCD to create case")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "updated"),
        @ApiResponse(code = 401, message = "Provided Authroization token is missing or invalid"),
        @ApiResponse(code = 500, message = "Internal Server Error"),
        @ApiResponse(code = 404, message = "Case Not found")
    })
    public ResponseEntity<CaseResponse> updateCase(@PathVariable final Long caseId,
                                        @RequestHeader(HttpHeaders.AUTHORIZATION) String authorisation,
                                        @RequestParam final EventEnum event,
                                        @RequestBody final CaseData caseData) {

        log.info("Event Received from UI: " + event);

        CaseResponse updatedCase = caseManagementService.updateCase(authorisation, event, caseData, caseId);
        return ResponseEntity.ok(updatedCase);
    }

    @PutMapping("/dss/{caseId}/update")
    @ApiOperation("Call CCD to create case")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Dss case updated"),
        @ApiResponse(code = 401, message = "Provided Authorisation token is missing or invalid"),
        @ApiResponse(code = 404, message = "Dss Case Not found")
    })
    public ResponseEntity<CaseResponse> updateDssCase(@PathVariable final Long caseId,
                                           @RequestHeader(SERVICE_AUTHORISATION) String s2sToken,
                                           @RequestParam final EventEnum event,
                                           @RequestBody final DssCaseRequest dssCaseRequest) {
        if (isAuthorized(s2sToken)) {
            CaseResponse updatedCase = caseManagementService
                    .updateDssCase(systemUserService.getSysUserToken(), event, dssCaseRequest, caseId);
            return ResponseEntity.ok(updatedCase);
        } else {
            throw new CaseCreateOrUpdateException("Invalid Client");
        }
    }

    @GetMapping("/fetchCaseDetails/{caseId}")
    @ApiOperation("Call CCD to fetch the citizen case details")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "updated"),
        @ApiResponse(code = 401, message = "Provided Authroization token is missing or invalid"),
        @ApiResponse(code = 500, message = "Internal Server Error"),
        @ApiResponse(code = 404, message = "Case Not found")
    })
    public ResponseEntity<CaseResponse> fetchCaseDetails(@PathVariable final Long caseId,
                                              @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        CaseResponse caseResponse = caseManagementService.fetchCaseDetails(authorization,caseId);
        return ResponseEntity.ok(caseResponse);
    }

    @GetMapping("/{caseId}")
    @ApiOperation("Call CCD to fetch the dss case details")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "case found"),
        @ApiResponse(code = 401, message = "Provided Authorization token is missing or invalid"),
        @ApiResponse(code = 404, message = "Case Not found")
    })
    public ResponseEntity<DssCaseResponse> fetchDssQuestionAnswerDetails(@PathVariable final Long caseId,
                         @RequestHeader(SERVICE_AUTHORISATION) String s2sToken) {
        if (isAuthorized(s2sToken)) {
            DssCaseResponse dssCaseResponse = caseManagementService
                    .fetchDssQuestionAnswerDetails(systemUserService.getSysUserToken(), caseId);
            return ResponseEntity.ok(dssCaseResponse);
        }  else {
            throw new CaseCreateOrUpdateException("Invalid Client");
        }
    }

    private boolean isAuthorized(String s2sToken) {
        return authorisationService.authoriseService(s2sToken);
    }
}
