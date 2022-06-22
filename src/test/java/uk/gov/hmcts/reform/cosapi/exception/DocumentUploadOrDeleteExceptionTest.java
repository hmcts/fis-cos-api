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
import static uk.gov.hmcts.reform.cosapi.util.TestConstant.CASE_DATA_FILE_C100;
import static uk.gov.hmcts.reform.cosapi.util.TestConstant.CASE_TEST_AUTHORISATION;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class DocumentUploadOrDeleteExceptionTest {
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
    void testDeleteDocumentUploadOrDeleteException() throws Exception {
        documentInfo = DocumentInfo.builder()
            .documentId("C100")
            .url("TestUrl")
            .fileName(CASE_DATA_FILE_C100).build();

        when(documentManagementService.deleteDocument(CASE_TEST_AUTHORISATION, documentInfo.getDocumentId())).thenThrow(
            new DocumentUploadOrDeleteException(
                "Failing while deleting the document. The error message is ",
                new Throwable()
            ));

        Exception exception = assertThrows(Exception.class, () -> {
            documentManagementController.deleteDocument(CASE_TEST_AUTHORISATION, documentInfo.getDocumentId());
        });
        assertTrue(exception.getMessage().contains("Failing while deleting the document. The error message is "),
            String.valueOf(true));
    }

    @Test
    void testUpdateDocumentUploadOrDeleteException() throws Exception {
        documentInfo = DocumentInfo.builder()
            .documentId("C100")
            .url("TestUrl")
            .fileName(CASE_DATA_FILE_C100).build();

        when(documentManagementService.deleteDocument(CASE_TEST_AUTHORISATION, documentInfo.getDocumentId())).thenThrow(
            new DocumentUploadOrDeleteException(
                "Failing while updating the document. The error message is ",
                new Throwable()
            ));

        Exception exception = assertThrows(Exception.class, () -> {
            documentManagementController.deleteDocument(CASE_TEST_AUTHORISATION, documentInfo.getDocumentId());
        });
        assertTrue(exception.getMessage().contains("Failing while updating the document. The error message is "),
            String.valueOf(true));
    }
}
