package uk.gov.hmcts.reform.cosapi.services;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.cosapi.controllers.DocumentManagementController;
import uk.gov.hmcts.reform.cosapi.exception.DocumentUploadOrDeleteException;
import uk.gov.hmcts.reform.cosapi.model.DocumentInfo;
import uk.gov.hmcts.reform.cosapi.model.DocumentResponse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.cosapi.util.TestFileUtil.loadJson;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class DocumentManagementServiceTest {
    private static final String CASE_TEST_AUTHORISATION = "testAuth";
    private static final String CASE_DATA_FILE_C100 = "C100CaseData.json";
    private static final String CASE_DATA_DOCUMENT_ID_C100 = "C100";
    private static final String TEST_URL = "TestUrl";

    @InjectMocks
    private DocumentManagementController documentManagementController;

    @Mock
    DocumentManagementService documentManagementService;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUploadC100Document() throws Exception {
        String caseDataJson = loadJson(CASE_DATA_FILE_C100);

        DocumentInfo documentInfo = DocumentInfo.builder()
            .documentId(CASE_DATA_DOCUMENT_ID_C100)
            .url(TEST_URL)
            .fileName(CASE_DATA_FILE_C100).build();

        DocumentResponse documentUploadResponse = DocumentResponse
            .builder().document(documentInfo).build();

        MockMultipartFile multipartFile = new MockMultipartFile(
            "json",
            CASE_DATA_FILE_C100,
            "application/json",
            caseDataJson.getBytes()
        );

        when(documentManagementService.uploadDocument(CASE_TEST_AUTHORISATION, multipartFile))
            .thenReturn(documentUploadResponse);

        ResponseEntity<?> uploadCaseResponse = documentManagementController
            .uploadDocument(CASE_TEST_AUTHORISATION, multipartFile);
        DocumentResponse testUploadResponse = (DocumentResponse) uploadCaseResponse.getBody();

        assertNotNull(testUploadResponse);
        assertEquals(documentInfo.getDocumentId(), testUploadResponse.getDocument().getDocumentId());
        assertEquals(documentInfo.getFileName(), testUploadResponse.getDocument().getFileName());
        assertEquals(documentInfo.getUrl(), testUploadResponse.getDocument().getUrl());
        assertEquals(HttpStatus.OK, uploadCaseResponse.getStatusCode());
    }

    @Test
    void testDeleteC100Document() throws Exception {
        DocumentInfo documentInfo = DocumentInfo.builder()
            .documentId(CASE_DATA_DOCUMENT_ID_C100)
            .url(TEST_URL)
            .fileName(CASE_DATA_FILE_C100).build();

        DocumentResponse documentResponse = DocumentResponse
            .builder().document(documentInfo).build();

        when(documentManagementService.deleteDocument(CASE_TEST_AUTHORISATION, "C100")).thenReturn(documentResponse);

        ResponseEntity<?> deleteCaseResponse = documentManagementController
            .deleteDocument(CASE_TEST_AUTHORISATION, CASE_DATA_DOCUMENT_ID_C100);

        DocumentResponse testDeleteResponse = (DocumentResponse) deleteCaseResponse.getBody();

        assertNotNull(testDeleteResponse);
        assertEquals(documentInfo.getDocumentId(), testDeleteResponse.getDocument().getDocumentId());
        assertEquals(documentInfo.getFileName(), testDeleteResponse.getDocument().getFileName());
        assertEquals(documentInfo.getUrl(), testDeleteResponse.getDocument().getUrl());
        assertEquals(HttpStatus.OK, deleteCaseResponse.getStatusCode());
    }

    @Test
    void testDeleteC100DocumentFailedWithException() throws Exception {
        DocumentInfo documentInfo = DocumentInfo.builder()
            .documentId(CASE_DATA_DOCUMENT_ID_C100)
            .url(TEST_URL)
            .fileName(CASE_DATA_FILE_C100).build();

        when(documentManagementService.deleteDocument(CASE_TEST_AUTHORISATION, documentInfo.getDocumentId())).thenThrow(
            new DocumentUploadOrDeleteException(
                "Failing while deleting the document. The error message is ",
                new Throwable()
            ));

        Exception exception = assertThrows(Exception.class, () -> {
            documentManagementController.deleteDocument(CASE_TEST_AUTHORISATION, documentInfo.getDocumentId());
        });
        assertTrue(exception.getMessage().contains("Failing while deleting the document. The error message is "));
    }
}
