package uk.gov.hmcts.reform.cosapi.services.cdam;

import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.ccd.document.am.feign.CaseDocumentClient;
import uk.gov.hmcts.reform.ccd.document.am.model.Classification;
import uk.gov.hmcts.reform.ccd.document.am.model.Document;
import uk.gov.hmcts.reform.ccd.document.am.model.UploadResponse;
import uk.gov.hmcts.reform.cosapi.constants.CommonConstants;
import uk.gov.hmcts.reform.cosapi.model.DocumentInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static uk.gov.hmcts.reform.cosapi.util.TestConstant.CASE_DATA_FILE_C100;
import static uk.gov.hmcts.reform.cosapi.util.TestConstant.JSON_FILE_TYPE;
import static uk.gov.hmcts.reform.cosapi.util.TestConstant.JSON_CONTENT_TYPE;
import static uk.gov.hmcts.reform.cosapi.util.TestConstant.CASE_DATA_C100_ID;
import static uk.gov.hmcts.reform.cosapi.util.TestConstant.TEST_URL;
import static uk.gov.hmcts.reform.cosapi.util.TestConstant.TEST_AUTHORIZATION_TOKEN;
import static uk.gov.hmcts.reform.cosapi.util.TestConstant.CASE_TEST_AUTHORIZATION;
import static uk.gov.hmcts.reform.cosapi.util.TestConstant.TEST_CASE_DATA_FILE_UUID;

import static uk.gov.hmcts.reform.cosapi.util.TestFileUtil.loadJson;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class CaseDocumentApiServiceTest {

    @InjectMocks
    CaseDocumentApiService caseDocumentApiService;

    @Mock
    CaseDocumentClient caseDocumentClient;

    @Mock
    AuthTokenGenerator authTokenGenerator;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testC100UpdateCaseDocumentService() throws Exception {
        String caseDataJson = loadJson(CASE_DATA_FILE_C100);

        MockMultipartFile multipartFile = new MockMultipartFile(
            JSON_FILE_TYPE,
            CASE_DATA_FILE_C100,
            JSON_CONTENT_TYPE,
            caseDataJson.getBytes()
        );

        List<MultipartFile> multipartFileLst = new ArrayList<>();

        multipartFileLst.add(multipartFile);

        when(authTokenGenerator.generate()).thenReturn(TEST_AUTHORIZATION_TOKEN);

        Document.Links links = new Document.Links();
        links.binary = new Document.Link();
        links.self = new Document.Link();
        links.binary.href = "binaryUrl";
        links.self.href = "selfURL";

        Document document = Document.builder()
            .classification(Classification.RESTRICTED)
            .originalDocumentName(CASE_DATA_FILE_C100)
            .hashToken("SomeToken")
            .size(caseDataJson.getBytes().length)
            .mimeType("application/json")
            .build();

        document.links = links;

        List<Document> documents = new ArrayList<>();
        documents.add(document);

        UploadResponse uploadResponse = new UploadResponse(documents);

        DocumentInfo documentInfo = DocumentInfo.builder()
            .documentId(CASE_DATA_C100_ID)
            .url(TEST_URL)
            .fileName(CASE_DATA_FILE_C100).build();

        when(caseDocumentClient.uploadDocuments(
            CASE_TEST_AUTHORIZATION,
            TEST_AUTHORIZATION_TOKEN,
            CommonConstants.CASE_TYPE,
            CommonConstants.JURISDICTION,
            multipartFileLst
        )).thenReturn(uploadResponse);

        DocumentInfo testUploadResponse = caseDocumentApiService.uploadDocument(
            CASE_TEST_AUTHORIZATION,
            multipartFile
        );

        Assertions.assertEquals(documentInfo.getFileName(), testUploadResponse.getFileName());
        Assertions.assertEquals(document.links.self.href, testUploadResponse.getUrl());
    }

    @Test
    void testDeleteDocumentWithCaseDocumentService() {
        final boolean permanent = true;

        when(authTokenGenerator.generate()).thenReturn(TEST_AUTHORIZATION_TOKEN);


        doNothing().when(caseDocumentClient)
            .deleteDocument(
                CASE_TEST_AUTHORIZATION,
                TEST_AUTHORIZATION_TOKEN,
                TEST_CASE_DATA_FILE_UUID,
                permanent);

        caseDocumentApiService.deleteDocument(CASE_TEST_AUTHORIZATION, String.valueOf(TEST_CASE_DATA_FILE_UUID));

        verify(caseDocumentClient).deleteDocument(
            CASE_TEST_AUTHORIZATION,
            TEST_AUTHORIZATION_TOKEN,
            UUID.fromString(String.valueOf(TEST_CASE_DATA_FILE_UUID)),
            permanent
        );
    }
}
