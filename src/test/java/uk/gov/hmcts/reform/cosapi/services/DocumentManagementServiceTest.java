package uk.gov.hmcts.reform.cosapi.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import uk.gov.hmcts.reform.cosapi.edgecase.model.CaseData;
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
@SuppressWarnings("PMD")
public class DocumentManagementServiceTest {
    private final String caseTestAuth = "testAuth";

    @InjectMocks
    private DocumentManagementController documentManagementController;

    @Mock
    DocumentManagementService documentManagementService;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testUploadC100Document() throws Exception {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        String caseDataJson = loadJson("C100CaseData.json");
        CaseData caseData = mapper.readValue(caseDataJson, CaseData.class);

        DocumentInfo documentInfo = DocumentInfo.builder()
            .documentId("C100")
            .url("TestUrl")
            .fileName("C100CaseData.json").build();

        DocumentResponse documentUploadResponse = DocumentResponse
            .builder().document(documentInfo).build();

        MockMultipartFile multipartFile = new MockMultipartFile(
            "json",
            "C100CaseData.json",
            "application/json",
            caseDataJson.getBytes()
        );

        when(documentManagementService.uploadDocument(caseTestAuth, multipartFile)).thenReturn(documentUploadResponse);

        ResponseEntity<?> uploadCaseResponse = documentManagementController.uploadDocument(caseTestAuth, multipartFile);
        DocumentResponse testUploadResponse = (DocumentResponse) uploadCaseResponse.getBody();

        assertNotNull(testUploadResponse);
        assertEquals(documentInfo.getDocumentId(), testUploadResponse.getDocument().getDocumentId());
        assertEquals(documentInfo.getFileName(), testUploadResponse.getDocument().getFileName());
        assertEquals(documentInfo.getUrl(), testUploadResponse.getDocument().getUrl());
        assertEquals(HttpStatus.OK, uploadCaseResponse.getStatusCode());
    }

    @Test
    public void testDeleteC100Document() throws Exception {
        DocumentInfo documentInfo = DocumentInfo.builder()
            .documentId("C100")
            .url("TestUrl")
            .fileName("C100CaseData.json").build();

        DocumentResponse documentResponse = DocumentResponse
            .builder().document(documentInfo).build();

        when(documentManagementService.deleteDocument(caseTestAuth, "C100")).thenReturn(documentResponse);

        ResponseEntity<?> deleteCaseResponse = documentManagementController.deleteDocument(caseTestAuth, "C100");
        DocumentResponse testDeleteResponse = (DocumentResponse) deleteCaseResponse.getBody();

        assertNotNull(testDeleteResponse);
        assertEquals(documentInfo.getDocumentId(), testDeleteResponse.getDocument().getDocumentId());
        assertEquals(documentInfo.getFileName(), testDeleteResponse.getDocument().getFileName());
        assertEquals(documentInfo.getUrl(), testDeleteResponse.getDocument().getUrl());
        assertEquals(HttpStatus.OK, deleteCaseResponse.getStatusCode());
    }

    @Test
    public void testDeleteC100DocumentFailedWithException() throws Exception {
        DocumentInfo documentInfo = DocumentInfo.builder()
            .documentId("C100")
            .url("TestUrl")
            .fileName("C100CaseData.json").build();

        when(documentManagementService.deleteDocument(caseTestAuth, documentInfo.getDocumentId())).thenThrow(
            new DocumentUploadOrDeleteException(
                "Failing while deleting the document. The error message is ",
                new Throwable()
            ));

        Exception exception = assertThrows(Exception.class, () -> {
            documentManagementController.deleteDocument(caseTestAuth, documentInfo.getDocumentId());
        });
        assertTrue(exception.getMessage().contains("Failing while deleting the document. The error message is "));
    }

}
