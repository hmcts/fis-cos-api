package uk.gov.hmcts.reform.cosapi.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import uk.gov.hmcts.reform.cosapi.exception.DocumentUploadOrDeleteException;
import uk.gov.hmcts.reform.cosapi.model.DocumentInfo;
import uk.gov.hmcts.reform.cosapi.model.DocumentResponse;
import uk.gov.hmcts.reform.cosapi.services.AuthorisationService;
import uk.gov.hmcts.reform.cosapi.services.DocumentManagementService;
import uk.gov.hmcts.reform.cosapi.services.SystemUserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.gov.hmcts.reform.cosapi.util.TestConstant.CASE_DATA_FGM_ID;
import static uk.gov.hmcts.reform.cosapi.util.TestConstant.CASE_DATA_FILE_FGM;
import static uk.gov.hmcts.reform.cosapi.util.TestConstant.CASE_TEST_AUTHORIZATION;
import static uk.gov.hmcts.reform.cosapi.util.TestConstant.INVALID_CLIENT;
import static uk.gov.hmcts.reform.cosapi.util.TestConstant.S2S_TOKEN;
import static uk.gov.hmcts.reform.cosapi.util.TestConstant.TEST_URL;
import static uk.gov.hmcts.reform.cosapi.util.TestConstant.RESPONSE_STATUS_SUCCESS;
import static uk.gov.hmcts.reform.cosapi.util.TestConstant.JSON_CONTENT_TYPE;
import static uk.gov.hmcts.reform.cosapi.util.TestConstant.JSON_FILE_TYPE;
import static uk.gov.hmcts.reform.cosapi.util.TestConstant.DOCUMENT_DELETE_FAILURE_MSG;
import static uk.gov.hmcts.reform.cosapi.util.TestFileUtil.loadJson;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
class DocumentManagementControllerTest {

    private static final String CASE_TYPE_ID = "A58";

    private static final String JURISDICTION = "Adoption";

    @InjectMocks
    private DocumentManagementController documentManagementController;

    @Mock
    DocumentManagementService documentManagementService;

    @Mock
    private AuthorisationService authorisationService;

    @Mock
    private SystemUserService systemUserService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFgmDocumentControllerFileUpload() throws Exception {
        String caseDataJson = loadJson(CASE_DATA_FILE_FGM);

        DocumentInfo document = DocumentInfo.builder()
            .documentId(CASE_DATA_FGM_ID)
            .url(TEST_URL)
            .fileName(CASE_DATA_FILE_FGM).build();

        DocumentResponse documentResponse = DocumentResponse.builder()
            .status(RESPONSE_STATUS_SUCCESS)
            .document(document).build();

        MockMultipartFile multipartFile = new MockMultipartFile(
            JSON_FILE_TYPE,
            CASE_DATA_FILE_FGM,
            JSON_CONTENT_TYPE,
            caseDataJson.getBytes()
        );

        when(documentManagementService.uploadDocument(
            CASE_TEST_AUTHORIZATION,
            CASE_DATA_FGM_ID,
            multipartFile
        )).thenReturn(
            documentResponse);

        ResponseEntity<?> uploadDocumentResponse = documentManagementController.uploadDocument(
            CASE_TEST_AUTHORIZATION,
            CASE_DATA_FGM_ID,
            multipartFile
        );

        DocumentResponse testResponse = (DocumentResponse) uploadDocumentResponse.getBody();

        assertNotNull(testResponse);
        assertEquals(document.getDocumentId(), testResponse.getDocument().getDocumentId());
        assertEquals(document.getFileName(), testResponse.getDocument().getFileName());
        assertEquals(document.getUrl(), testResponse.getDocument().getUrl());
        assertEquals(RESPONSE_STATUS_SUCCESS, testResponse.getStatus());
    }

    @Test
    void testDeleteFgmDocumentControllerFailedWithException() throws Exception {
        DocumentInfo documentInfo = DocumentInfo.builder()
            .documentId(CASE_DATA_FGM_ID)
            .url(TEST_URL)
            .fileName(CASE_DATA_FILE_FGM).build();

        when(documentManagementService.deleteDocument(
            CASE_TEST_AUTHORIZATION,
            documentInfo.getDocumentId()
        )).thenThrow(
            new DocumentUploadOrDeleteException(
                DOCUMENT_DELETE_FAILURE_MSG,
                new Throwable()
            ));

        Exception exception = assertThrows(Exception.class, () -> {
            documentManagementService.deleteDocument(CASE_TEST_AUTHORIZATION, documentInfo.getDocumentId());
        });
        assertTrue(exception.getMessage().contains(DOCUMENT_DELETE_FAILURE_MSG));
    }

    @Test
    void testDssFileUpload() throws Exception {
        String caseDataJson = loadJson(CASE_DATA_FILE_FGM);

        DocumentInfo document = DocumentInfo.builder()
            .documentId(CASE_DATA_FGM_ID)
            .url(TEST_URL)
            .fileName(CASE_DATA_FILE_FGM).build();

        DocumentResponse documentResponse = DocumentResponse.builder()
            .status(RESPONSE_STATUS_SUCCESS)
            .document(document).build();

        MockMultipartFile multipartFile = new MockMultipartFile(
            JSON_FILE_TYPE,
            CASE_DATA_FILE_FGM,
            JSON_CONTENT_TYPE,
            caseDataJson.getBytes()
        );

        when(documentManagementService.uploadDocumentForDssUpdate(
            systemUserService.getSysUserToken(),
            CASE_TYPE_ID,
            JURISDICTION,
            multipartFile
        )).thenReturn(
            documentResponse);

        when(authorisationService.authoriseService(S2S_TOKEN)).thenReturn(true);

        ResponseEntity<?> uploadDocumentResponse = documentManagementController.uploadDocumentForDssUpdateCase(
            S2S_TOKEN,
            CASE_TYPE_ID,
            JURISDICTION, multipartFile
        );

        DocumentResponse testResponse = (DocumentResponse) uploadDocumentResponse.getBody();

        assertNotNull(testResponse);
        assertEquals(document.getDocumentId(), testResponse.getDocument().getDocumentId());
        assertEquals(document.getFileName(), testResponse.getDocument().getFileName());
        assertEquals(document.getUrl(), testResponse.getDocument().getUrl());
        assertEquals(RESPONSE_STATUS_SUCCESS, testResponse.getStatus());
    }

    @Test
    void testUploadDssDocumentException() throws Exception {
        String caseDataJson = loadJson(CASE_DATA_FILE_FGM);


        MockMultipartFile multipartFile = new MockMultipartFile(
            JSON_FILE_TYPE,
            CASE_DATA_FILE_FGM,
            JSON_CONTENT_TYPE,
            caseDataJson.getBytes()
        );

        when(authorisationService.authoriseService(S2S_TOKEN)).thenReturn(false);

        Exception exception = assertThrows(Exception.class, () -> {
            documentManagementController.uploadDocumentForDssUpdateCase(
                S2S_TOKEN,
                CASE_TYPE_ID,
                JURISDICTION, multipartFile);
        });

        assertTrue(exception.getMessage().contains(INVALID_CLIENT));
    }


    @Test
    void testDeleteDssDocument() {

        final DocumentResponse documentResponse = DocumentResponse.builder().status("Success").build();

        when(authorisationService.authoriseService(S2S_TOKEN)).thenReturn(true);

        when(documentManagementService
                 .deleteDocument(systemUserService.getSysUserToken(), "12345")).thenReturn(documentResponse);

        final ResponseEntity<?> response = documentManagementController.deleteDssDocument(S2S_TOKEN, "12345");

        final DocumentResponse responseBody = (DocumentResponse) response.getBody();

        assertEquals(RESPONSE_STATUS_SUCCESS, responseBody.getStatus());

    }

    @Test
    void testDeleteDssDocumentException() {

        when(authorisationService.authoriseService(S2S_TOKEN)).thenReturn(false);

        Exception exception = assertThrows(Exception.class, () -> {
            documentManagementController.deleteDssDocument(
                S2S_TOKEN,
                CASE_DATA_FGM_ID);
        });

        assertTrue(exception.getMessage().contains(INVALID_CLIENT));
    }
}
