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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.cosapi.edgecase.event.EventEnum;
import uk.gov.hmcts.reform.cosapi.edgecase.model.CaseData;
import uk.gov.hmcts.reform.cosapi.exception.CaseCreateOrUpdateException;
import uk.gov.hmcts.reform.cosapi.model.CaseResponse;
import uk.gov.hmcts.reform.cosapi.services.ccd.CaseApiService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.gov.hmcts.reform.cosapi.util.TestConstant.CASE_DATA_C100_ID;
import static uk.gov.hmcts.reform.cosapi.util.TestConstant.CASE_DATA_FILE_C100;
import static uk.gov.hmcts.reform.cosapi.util.TestConstant.CASE_TEST_AUTHORIZATION;
import static uk.gov.hmcts.reform.cosapi.util.TestConstant.RESPONSE_STATUS_SUCCESS;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.cosapi.util.TestFileUtil.loadJson;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class CaseManagementServiceTest {
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private static final String TEST_CASE_EMAIL_UPDATE = "testUpdate@test.com";
    private static final String CREATE_CASE_FAILURE_MSG = "Failing while creating the case ";

    @InjectMocks
    private CaseManagementService caseManagementService;

    @Mock
    CaseApiService caseApiService;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testC100CreateCaseData() throws Exception {
        String caseDataJson = loadJson(CASE_DATA_FILE_C100);
        CaseData caseData = mapper.readValue(caseDataJson, CaseData.class);

        Map<String, Object> caseDataMap = new ConcurrentHashMap<>();
        caseDataMap.put(CASE_DATA_C100_ID, caseData);

        CaseDetails caseDetail = CaseDetails.builder().caseTypeId(CASE_DATA_C100_ID)
            .id(123L)
            .data(caseDataMap)
            .build();

        CaseResponse caseResponse = CaseResponse.builder().caseData(caseDataMap).build();

        when(caseApiService.createCase(CASE_TEST_AUTHORIZATION, caseData)).thenReturn(caseDetail);

        CaseResponse createCaseResponse = caseManagementService.createCase(CASE_TEST_AUTHORIZATION, caseData);
        assertEquals(createCaseResponse.getCaseData(), caseResponse.getCaseData());
        assertEquals(createCaseResponse.getId(), caseDetail.getId());
        assertTrue(createCaseResponse.getCaseData().containsKey(CASE_DATA_C100_ID));

        CaseData caseResponseData = (CaseData) createCaseResponse.getCaseData().get(CASE_DATA_C100_ID);
        assertNotNull(createCaseResponse);
        assertEquals(
            createCaseResponse.getCaseData().get(CASE_DATA_C100_ID),
            caseDetail.getData().get(CASE_DATA_C100_ID)
        );
        assertEquals(caseResponseData.getNamedApplicant(), caseData.getNamedApplicant());
        assertEquals(caseResponseData.getCaseTypeOfApplication(), caseData.getCaseTypeOfApplication());
        assertEquals(caseResponseData.getApplicant(), caseData.getApplicant());
        assertEquals(RESPONSE_STATUS_SUCCESS, createCaseResponse.getStatus());
    }

    @Test
    void testCreateCaseC100FailedWithCaseCreateUpdateException() throws Exception {
        String caseDataJson = loadJson(CASE_DATA_FILE_C100);
        CaseData caseData = mapper.readValue(caseDataJson, CaseData.class);

        Map<String, Object> caseDataMap = new ConcurrentHashMap<>();

        caseDataMap.put(CASE_DATA_C100_ID, caseData);

        when(caseApiService.createCase(CASE_TEST_AUTHORIZATION, caseData)).thenThrow(
            new CaseCreateOrUpdateException(
                CREATE_CASE_FAILURE_MSG,
                new RuntimeException()
            ));

        Exception exception = assertThrows(Exception.class, () -> {
            caseManagementService.createCase(CASE_TEST_AUTHORIZATION, caseData);
        });

        assertTrue(exception.getMessage().contains("Failing while creating the case "));
    }

    @Test
    void testC100UpdateCaseData() throws Exception {
        String caseDataJson = loadJson(CASE_DATA_FILE_C100);
        CaseData caseData = mapper.readValue(caseDataJson, CaseData.class);

        Map<String, Object> caseDataMap = new ConcurrentHashMap<>();
        caseDataMap.put(CASE_DATA_C100_ID, caseData);

        CaseDetails caseDetail = CaseDetails.builder().caseTypeId(CASE_DATA_C100_ID)
            .id(123L)
            .data(caseDataMap)
            .build();

        String origEmailAddress = caseData.getApplicant().getEmailAddress();
        caseData.getApplicant().setEmailAddress(TEST_CASE_EMAIL_UPDATE);
        assertNotEquals(caseData.getApplicant().getEmailAddress(), origEmailAddress);

        when(caseApiService.updateCase(
            CASE_TEST_AUTHORIZATION,
            EventEnum.UPDATE,
            123L,
            caseData
        )).thenReturn(caseDetail);

        CaseResponse updateCaseResponse = caseManagementService.updateCase(
            CASE_TEST_AUTHORIZATION,
            EventEnum.UPDATE,
            caseData,
            123L
        );
        assertEquals(updateCaseResponse.getId(), caseDetail.getId());
        assertTrue(updateCaseResponse.getCaseData().containsKey(CASE_DATA_C100_ID));


        CaseData caseResponseData = (CaseData) updateCaseResponse.getCaseData().get(CASE_DATA_C100_ID);

        assertNotEquals(caseResponseData.getApplicant().getEmailAddress(), origEmailAddress);

        assertNotNull(updateCaseResponse);

        assertEquals(
            updateCaseResponse.getCaseData().get(CASE_DATA_C100_ID),
            caseDetail.getData().get(CASE_DATA_C100_ID)
        );
        assertEquals(caseResponseData.getNamedApplicant(), caseData.getNamedApplicant());
        assertEquals(caseResponseData.getCaseTypeOfApplication(), caseData.getCaseTypeOfApplication());
        assertEquals(caseResponseData.getApplicant().getEmailAddress(), caseData.getApplicant().getEmailAddress());
        assertEquals(RESPONSE_STATUS_SUCCESS, updateCaseResponse.getStatus());
    }

    @Test
    void testUpdateCaseC100FailedWithCaseCreateUpdateException() throws Exception {
        String caseDataJson = loadJson(CASE_DATA_FILE_C100);
        CaseData caseData = mapper.readValue(caseDataJson, CaseData.class);

        Map<String, Object> caseDataMap = new ConcurrentHashMap<>();

        caseDataMap.put(CASE_DATA_C100_ID, caseData);

        when(caseApiService.createCase(CASE_TEST_AUTHORIZATION, caseData)).thenThrow(
            new CaseCreateOrUpdateException(
                CREATE_CASE_FAILURE_MSG,
                new RuntimeException()
            ));

        Exception exception = assertThrows(Exception.class, () -> {
            caseManagementService.createCase(CASE_TEST_AUTHORIZATION, caseData);
        });

        assertTrue(exception.getMessage().contains(CREATE_CASE_FAILURE_MSG));
    }

}