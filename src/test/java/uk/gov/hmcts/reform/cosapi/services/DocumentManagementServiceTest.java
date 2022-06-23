package uk.gov.hmcts.reform.cosapi.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.cosapi.exception.DocumentUploadOrDeleteException;
import uk.gov.hmcts.reform.cosapi.model.DocumentInfo;
import uk.gov.hmcts.reform.cosapi.model.DocumentResponse;
import uk.gov.hmcts.reform.cosapi.services.cdam.CaseDocumentApiService;

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
    private DocumentManagementService documentManagementService;

    @Mock
    CaseDocumentApiService caseDocumentApiService;

    @BeforeEach
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

        MockMultipartFile multipartFile = new MockMultipartFile(
            "json",
            CASE_DATA_FILE_C100,
            "application/json",
            caseDataJson.getBytes()
        );

        when(caseDocumentApiService.uploadDocument(CASE_TEST_AUTHORISATION, multipartFile)).thenReturn(documentInfo);

        DocumentResponse testUploadResponse = (DocumentResponse) documentManagementService.uploadDocument(
            CASE_TEST_AUTHORISATION,
            multipartFile
        );


        Assertions.assertNotNull(testUploadResponse);
        Assertions.assertEquals(documentInfo.getDocumentId(), testUploadResponse.getDocument().getDocumentId());
        Assertions.assertEquals(documentInfo.getFileName(), testUploadResponse.getDocument().getFileName());
        Assertions.assertEquals(documentInfo.getUrl(), testUploadResponse.getDocument().getUrl());
        Assertions.assertEquals("Success", testUploadResponse.getStatus());
    }

    @Test
    void testUploadC100DocumentFailedWithException() throws Exception {
        String caseDataJson = loadJson(CASE_DATA_FILE_C100);

        MockMultipartFile multipartFile = new MockMultipartFile(
            "json",
            CASE_DATA_FILE_C100,
            "application/json",
            caseDataJson.getBytes()
        );

        when(caseDocumentApiService.uploadDocument(CASE_TEST_AUTHORISATION, multipartFile)).thenThrow(
            new DocumentUploadOrDeleteException(
                "Failing while uploading the document. The error message is ",
                new Throwable()
            ));

        Exception exception = assertThrows(Exception.class, () -> {
            documentManagementService.uploadDocument(CASE_TEST_AUTHORISATION, multipartFile);
        });

        assertTrue(exception.getMessage().contains("Failing while uploading the document. The error message is "));
    }

    @Test
    void testDeleteC100Document() {

        DocumentResponse testDeleteResponse = (DocumentResponse) documentManagementService.deleteDocument(
            CASE_TEST_AUTHORISATION,
            CASE_DATA_DOCUMENT_ID_C100
        );

        Assertions.assertNotNull(testDeleteResponse);
        Assertions.assertEquals("Success", testDeleteResponse.getStatus());
    }

    @Test
    void testDeleteC100DocumentFailedWithException() throws Exception {
        DocumentInfo documentInfo = DocumentInfo.builder()
            .documentId(CASE_DATA_DOCUMENT_ID_C100)
            .url(TEST_URL)
            .fileName(CASE_DATA_FILE_C100).build();

        when(documentManagementService.deleteDocument(
            CASE_TEST_AUTHORISATION,
            documentInfo.getDocumentId()
        )).thenThrow(
            new DocumentUploadOrDeleteException(
                "Failing while deleting the document. The error message is ",
                new Throwable()
            ));

        Exception exception = assertThrows(Exception.class, () -> {
            documentManagementService.deleteDocument(CASE_TEST_AUTHORISATION, documentInfo.getDocumentId());
        });
        assertTrue(exception.getMessage().contains("Failing while deleting the document. The error message is "));
    }
}
