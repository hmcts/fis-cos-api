package uk.gov.hmcts.reform.cosapi.controllers;

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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.cosapi.edgecase.event.EventEnum;
import uk.gov.hmcts.reform.cosapi.edgecase.model.CaseData;
import uk.gov.hmcts.reform.cosapi.model.CaseResponse;
import uk.gov.hmcts.reform.cosapi.services.CaseManagementService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.cosapi.util.TestFileUtil.loadJson;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class CaseManagementControllerTest {
    private static final String CASE_TEST_AUTHORIZATION = "testAuth";
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private static final String CASE_DATA_FILE_C100 = "C100CaseData.json";
    private static final String CASE_DATA_C100_ID = "C100CaseData";

    @InjectMocks
    private CaseManagementController caseManagementController;

    @Mock
    CaseManagementService caseManagementService;

    @Before
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testC100CreateCaseData() throws Exception {
        String caseDataJson = loadJson(CASE_DATA_FILE_C100);
        CaseData caseData = mapper.readValue(caseDataJson, CaseData.class);

        Map<String, Object> caseDataMap = new ConcurrentHashMap<>();

        CaseResponse caseResponse = CaseResponse.builder().caseData(caseDataMap).build();

        when(caseManagementService.createCase(CASE_TEST_AUTHORIZATION, caseData)).thenReturn(caseResponse);

        ResponseEntity<?> createCaseResponse = caseManagementController.createCase(CASE_TEST_AUTHORIZATION, caseData);

        CaseResponse testResponse = (CaseResponse) createCaseResponse.getBody();

        assertNotNull(testResponse);
        assertEquals(HttpStatus.OK, createCaseResponse.getStatusCode());
    }

    @Test
    void testC100UpdateCaseData() throws Exception {
        String caseDataJson = loadJson(CASE_DATA_FILE_C100);
        CaseData caseData = mapper.readValue(caseDataJson, CaseData.class);

        Map<String, Object> caseDataMap = new ConcurrentHashMap<>();

        caseDataMap.put(CASE_DATA_C100_ID, caseData);
        CaseResponse caseResponse = CaseResponse.builder().caseData(caseDataMap).build();
        caseResponse.setId(123L);
        caseResponse.setStatus(null);

        when(caseManagementService.updateCase(CASE_TEST_AUTHORIZATION, EventEnum.UPDATE,
                                              caseData, 123L)).thenReturn(caseResponse);

        ResponseEntity<?> preUpdateCaseResponse = caseManagementController.updateCase(
            123L,
            CASE_TEST_AUTHORIZATION,
            EventEnum.UPDATE,
            caseData
        );

        CaseResponse testPreUpdResponse = (CaseResponse) preUpdateCaseResponse.getBody();
        assertEquals("test@test.com", caseData.getApplicant().getEmailAddress());

        CaseData caseDataUpdate = (CaseData) testPreUpdResponse.getCaseData().get(CASE_DATA_C100_ID);
        caseDataUpdate.getApplicant().setEmailAddress("testUpdate@test.com");

        preUpdateCaseResponse = caseManagementController.updateCase(
            123L,
            CASE_TEST_AUTHORIZATION,
            EventEnum.UPDATE,
            caseDataUpdate
        );

        CaseResponse caseDataUpdateResponse = (CaseResponse) (preUpdateCaseResponse.getBody());

        CaseData caseDataUpdatedFromResponse = (CaseData) (caseDataUpdateResponse.getCaseData().get(CASE_DATA_C100_ID));

        assertEquals(caseDataUpdatedFromResponse.getApplicant().getEmailAddress(),
                     caseDataUpdate.getApplicant().getEmailAddress());
        assertEquals("testUpdate@test.com", caseDataUpdate.getApplicant().getEmailAddress());

        assertNotNull(testPreUpdResponse);
        assertEquals(HttpStatus.OK, preUpdateCaseResponse.getStatusCode());
    }
}
