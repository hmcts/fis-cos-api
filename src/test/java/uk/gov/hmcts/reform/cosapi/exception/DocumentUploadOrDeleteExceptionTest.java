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
import static uk.gov.hmcts.reform.cosapi.util.TestConstant.CASE_DATA_C100_ID;
import static uk.gov.hmcts.reform.cosapi.util.TestConstant.CASE_TEST_AUTHORIZATION;
import static uk.gov.hmcts.reform.cosapi.util.TestConstant.TEST_URL;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class DocumentUploadOrDeleteExceptionTest {
    private static final String DELETE_DOCUMENT_FAILURE_MSG =
        "Failing while deleting the document. The error message is ";
    private static final String UPLOAD_DOCUMENT_FAILURE_MSG =
        "Failing while uploading the document. The error message is ";
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
            .documentId(CASE_DATA_C100_ID)
            .url(TEST_URL)
            .fileName(CASE_DATA_FILE_C100).build();

        when(documentManagementService.deleteDocument(CASE_TEST_AUTHORIZATION, documentInfo.getDocumentId())).thenThrow(
            new DocumentUploadOrDeleteException(
                DELETE_DOCUMENT_FAILURE_MSG,
                new RuntimeException()
            ));

        Exception exception = assertThrows(Exception.class, () -> {
            documentManagementController.deleteDocument(CASE_TEST_AUTHORIZATION, documentInfo.getDocumentId());
        });
        assertTrue(exception.getMessage().contains(DELETE_DOCUMENT_FAILURE_MSG),
            String.valueOf(true));
    }

    @Test
    void testUpdateDocumentUploadOrDeleteException() throws Exception {
        documentInfo = DocumentInfo.builder()
            .documentId(CASE_DATA_C100_ID)
            .url(TEST_URL)
            .fileName(CASE_DATA_FILE_C100).build();

        when(documentManagementService.deleteDocument(CASE_TEST_AUTHORIZATION, documentInfo.getDocumentId())).thenThrow(
            new DocumentUploadOrDeleteException(
                UPLOAD_DOCUMENT_FAILURE_MSG,
                new RuntimeException()
            ));

        Exception exception = assertThrows(Exception.class, () -> {
            documentManagementController.deleteDocument(CASE_TEST_AUTHORIZATION, documentInfo.getDocumentId());
        });
        assertTrue(exception.getMessage().contains(UPLOAD_DOCUMENT_FAILURE_MSG),
            String.valueOf(true));
    }
}
