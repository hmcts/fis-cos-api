package uk.gov.hmcts.reform.cosapi.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.cosapi.edgecase.model.Applicant;
import uk.gov.hmcts.reform.cosapi.edgecase.model.CaseData;
import uk.gov.hmcts.reform.cosapi.model.DocumentInfo;
import uk.gov.hmcts.reform.cosapi.model.DocumentResponse;
import uk.gov.hmcts.reform.cosapi.services.DocumentManagementService;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static uk.gov.hmcts.reform.cosapi.util.TestFileUtil.loadJson;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
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

    private CaseData readCaseFromJsonFile (JsonNode node) {
        CaseData caseData = new CaseData();

        caseData.setNamedApplicant(node.get("namedApplicant").asText());
        caseData.setCaseTypeOfApplication(node.get("caseTypeOfApplication").asText());
        Applicant applicant = new Applicant();
        applicant.setFirstName(node.get("applicant").get("firstName").asText());
        applicant.setLastName(node.get("applicant").get("lastName").asText());

        DateTimeFormatter dobFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        applicant.setDateOfBirth(LocalDate.parse(node.get("applicant").get("dateOfBirth").asText(), dobFormat));
        applicant.setEmailAddress(node.get("applicant").get("emailAddress").asText());
        applicant.setPhoneNumber(node.get("applicant").get("phoneNumber").asText());
        applicant.setHomeNumber(node.get("applicant").get("homeNumber").asText());
        applicant.setAddress1(node.get("applicant").get("address1").asText());
        applicant.setAddress2(node.get("applicant").get("address2").asText());
        applicant.setAddressTown(node.get("applicant").get("addressTown").asText());
        applicant.setAddressCountry(node.get("applicant").get("addressCountry").asText());
        applicant.setAddressPostCode(node.get("applicant").get("addressPostCode").asText());
        caseData.setApplicant(applicant);

        return caseData;
    }

    @Test
    public void testUpdateDocument () throws Exception {
        ObjectMapper mapper = new ObjectMapper();

         String caseDataJson = loadJson ("C100CaseData.json");
        CaseData caseData = mapper.readValue(caseDataJson, CaseData.class);

        DocumentInfo documentInfo = DocumentInfo.builder()
            .documentId("/C100CaseData.json").build();

        Map<String, Object> documentDataMap = new HashMap<>();

        DocumentResponse documentResponse = DocumentResponse
            .builder().document(documentInfo).build();

//        MockMultipartFile multipartFile = new MockMultipartFile("/C100CaseData.json", updInpStream);
//
//
//        when (documentManagementService.uploadDocument(caseTestAuth, mFile)).thenReturn(documentResponse);
//
//        ResponseEntity<?> aCase = documentManagementController.uploadDocument(caseTestAuth, documentInfo);
//
//        CaseResponse testResponse = (CaseResponse) aCase.getBody();
//
//        assertNotNull(testResponse);
//        assertEquals(HttpStatus.OK, testResponse.getStatus());
    }

}
