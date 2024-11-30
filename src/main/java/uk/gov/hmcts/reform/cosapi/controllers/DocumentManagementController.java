package uk.gov.hmcts.reform.cosapi.controllers;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.hmcts.reform.cosapi.exception.DocumentUploadOrDeleteException;
import uk.gov.hmcts.reform.cosapi.model.DocumentResponse;
import uk.gov.hmcts.reform.cosapi.services.AuthorisationService;
import uk.gov.hmcts.reform.cosapi.services.DocumentManagementService;
import uk.gov.hmcts.reform.cosapi.services.SystemUserService;

import static uk.gov.hmcts.reform.cosapi.controllers.CaseManagementController.SERVICE_AUTHORISATION;

@RestController
@RequestMapping("/doc/dss-orhestration")
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DocumentManagementController {

    private final DocumentManagementService documentManagementService;

    private final SystemUserService systemUserService;

    private final AuthorisationService authorisationService;

    @PostMapping(
        value = "/upload",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiOperation("Call CDAM to upload document")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Uploaded Successfully"),
        @ApiResponse(code = 400, message = "Bad Request while uploading the document"),
        @ApiResponse(code = 401, message = "Provided Authroization token is missing or invalid"),
        @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<DocumentResponse> uploadDocument(
        @RequestHeader(HttpHeaders.AUTHORIZATION) String authorisation,
        @RequestParam("caseTypeOfApplication") String caseTypeOfApplication,
        @RequestParam("file") MultipartFile file) {

        return ResponseEntity.ok(documentManagementService.uploadDocument(authorisation, caseTypeOfApplication, file));
    }

    @DeleteMapping("/{documentId}/delete")
    @ApiOperation("Call CDAM to delete document")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Deleted document successfully"),
        @ApiResponse(code = 400, message = "Bad Request while deleting the document"),
        @ApiResponse(code = 401, message = "Provided Authroization token is missing or invalid"),
        @ApiResponse(code = 404, message = "Document Not found"),
        @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<DocumentResponse> deleteDocument(
        @RequestHeader(HttpHeaders.AUTHORIZATION) String authorisation,
        @PathVariable("documentId") String documentId) {

        return ResponseEntity.ok(documentManagementService.deleteDocument(authorisation, documentId));
    }

    @DeleteMapping("/dss/{documentId}/delete")
    @ApiOperation("Call CDAM to dss delete document")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Deleted document successfully"),
        @ApiResponse(code = 400, message = "Bad Request while deleting the document"),
        @ApiResponse(code = 401, message = "Provided Authorisation token is missing or invalid"),
        @ApiResponse(code = 404, message = "Document Not found")
    })
    public ResponseEntity<DocumentResponse> deleteDssDocument(@RequestHeader(SERVICE_AUTHORISATION) String s2sToken,
                                               @PathVariable("documentId") String documentId) {
        if (isAuthorized(s2sToken)) {
            return ResponseEntity.ok(documentManagementService
                                         .deleteDocument(systemUserService.getSysUserToken(), documentId));
        } else {
            throw new DocumentUploadOrDeleteException("Invalid Client");
        }
    }

    private boolean isAuthorized(String s2sToken) {
        return authorisationService.authoriseService(s2sToken);
    }

    @PostMapping(
        value = "/upload-for-dss-update",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiOperation("Call CDAM to upload document")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Uploaded Successfully"),
        @ApiResponse(code = 400, message = "Bad Request while uploading the document"),
        @ApiResponse(code = 401, message = "Provided Authroization token is missing or invalid"),
        @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ResponseEntity<DocumentResponse> uploadDocumentForDssUpdateCase(
        @RequestHeader(SERVICE_AUTHORISATION) String s2sToken,
        @RequestParam("caseTypeId") String caseTypeId,
        @RequestParam("jurisdiction") String jurisdiction,
        @RequestParam("file") MultipartFile file) {
        if (isAuthorized(s2sToken)) {
            log.info("File Upload request received for CaseType - {} "
                    + " and jurisdiction - {} ", caseTypeId, jurisdiction);
            return ResponseEntity.ok(
                documentManagementService.uploadDocumentForDssUpdate(systemUserService.getSysUserToken(),
                                                                     caseTypeId, jurisdiction, file
                ));
        } else {
            throw new DocumentUploadOrDeleteException("Invalid Client");
        }
    }
}
