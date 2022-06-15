package uk.gov.hmcts.reform.cosapi.exception;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.cosapi.controllers.DocumentManagementController;
import uk.gov.hmcts.reform.cosapi.model.DocumentInfo;
import uk.gov.hmcts.reform.cosapi.services.DocumentManagementService;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@SuppressWarnings("PMD")
public class DocumentUploadOrDeleteExceptionTest {
    private final String caseTestAuth = "testAuth";
    private DocumentInfo documentInfo;

    @InjectMocks
    private DocumentManagementController documentManagementController;

    @Mock
    DocumentManagementService documentManagementService;


    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

    }

    @Test
    public void testDeleteDocumentUploadOrDeleteException() throws Exception {
        documentInfo = DocumentInfo.builder()
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

    @Test
    public void testUpdateDocumentUploadOrDeleteException() throws Exception {
        documentInfo = DocumentInfo.builder()
            .documentId("C100")
            .url("TestUrl")
            .fileName("C100CaseData.json").build();

        when(documentManagementService.deleteDocument(caseTestAuth, documentInfo.getDocumentId())).thenThrow(
            new DocumentUploadOrDeleteException(
                "Failing while updating the document. The error message is ",
                new Throwable()
            ));

        Exception exception = assertThrows(Exception.class, () -> {
            documentManagementController.deleteDocument(caseTestAuth, documentInfo.getDocumentId());
        });
        assertTrue(exception.getMessage().contains("Failing while updating the document. The error message is "));
    }

}
