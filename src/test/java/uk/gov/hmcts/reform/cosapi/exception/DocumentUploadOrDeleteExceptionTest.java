package uk.gov.hmcts.reform.cosapi.exception;

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
import uk.gov.hmcts.reform.cosapi.model.DocumentInfo;
import uk.gov.hmcts.reform.cosapi.model.DocumentResponse;
import uk.gov.hmcts.reform.cosapi.services.DocumentManagementService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.cosapi.util.TestFileUtil.loadJson;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class DocumentUploadOrDeleteExceptionTest {
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
    public void testDeleteDocumentUploadOrDeleteException () throws Exception {
        DocumentInfo documentInfo = DocumentInfo.builder()
            .documentId("C100")
            .url("TestUrl")
            .fileName("C100CaseData.json").build();

        when (documentManagementService.deleteDocument(caseTestAuth, documentInfo.getDocumentId())).thenThrow(
            new DocumentUploadOrDeleteException ("Failing while deleting the document. The error message is ", new Throwable()));

        Exception exception = assertThrows(Exception.class, () -> {
            documentManagementController.deleteDocument(caseTestAuth, documentInfo.getDocumentId());
        });
        assertTrue (exception.getMessage().contains("Failing while deleting the document. The error message is "));
    }

    @Test
    public void testUpdateDocumentUploadOrDeleteException () throws Exception {
        DocumentInfo documentInfo = DocumentInfo.builder()
            .documentId("C100")
            .url("TestUrl")
            .fileName("C100CaseData.json").build();

        when (documentManagementService.deleteDocument(caseTestAuth, documentInfo.getDocumentId())).thenThrow(
            new DocumentUploadOrDeleteException ("Failing while updating the document. The error message is ", new Throwable()));

        Exception exception = assertThrows(Exception.class, () -> {
            documentManagementController.deleteDocument(caseTestAuth, documentInfo.getDocumentId());
        });
        assertTrue (exception.getMessage().contains("Failing while updating the document. The error message is "));
    }

}
