package uk.gov.hmcts.reform.cosapi.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.ccd.sdk.type.ListValue;
import uk.gov.hmcts.reform.cosapi.edgecase.event.EventEnum;
import uk.gov.hmcts.reform.cosapi.edgecase.model.CaseData;
import uk.gov.hmcts.reform.cosapi.model.CaseResponse;
import uk.gov.hmcts.reform.cosapi.model.DssCaseRequest;
import uk.gov.hmcts.reform.cosapi.model.DssCaseResponse;
import uk.gov.hmcts.reform.cosapi.model.DssDocumentInfo;
import uk.gov.hmcts.reform.cosapi.services.AuthorisationService;
import uk.gov.hmcts.reform.cosapi.services.CaseManagementService;
import uk.gov.hmcts.reform.cosapi.services.SystemUserService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.cosapi.util.TestConstant.CASE_DATA_FGM_ID;
import static uk.gov.hmcts.reform.cosapi.util.TestConstant.CASE_DATA_FILE_FGM;
import static uk.gov.hmcts.reform.cosapi.util.TestConstant.CASE_TEST_AUTHORIZATION;
import static uk.gov.hmcts.reform.cosapi.util.TestConstant.TEST_CASE_ID;
import static uk.gov.hmcts.reform.cosapi.util.TestFileUtil.loadJson;

@RunWith(MockitoJUnitRunner.class)
class CaseManagementControllerTest {
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @InjectMocks
    private CaseManagementController caseManagementController;

    @Mock
    CaseManagementService caseManagementService;

    @Mock
    AuthorisationService authorisationService;

    @Mock
    SystemUserService systemUserService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFgmCreateCaseData() throws Exception {
        String caseDataJson = loadJson(CASE_DATA_FILE_FGM);
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
    void testFgmUpdateCaseData() throws Exception {
        String caseDataJson = loadJson(CASE_DATA_FILE_FGM);
        CaseData caseData = mapper.readValue(caseDataJson, CaseData.class);

        Map<String, Object> caseDataMap = new ConcurrentHashMap<>();

        caseDataMap.put(CASE_DATA_FGM_ID, caseData);
        CaseResponse caseResponse = CaseResponse.builder().caseData(caseDataMap).build();
        caseResponse.setId(TEST_CASE_ID);
        caseResponse.setStatus(null);

        when(caseManagementService.updateCase(CASE_TEST_AUTHORIZATION, EventEnum.UPDATE,
                caseData, TEST_CASE_ID)).thenReturn(caseResponse);

        ResponseEntity<?> preUpdateCaseResponse = caseManagementController.updateCase(
                TEST_CASE_ID,
                CASE_TEST_AUTHORIZATION,
                EventEnum.UPDATE,
                caseData);

        CaseResponse testPreUpdResponse = (CaseResponse) preUpdateCaseResponse.getBody();

        CaseData caseDataUpdate = (CaseData) testPreUpdResponse.getCaseData().get(CASE_DATA_FGM_ID);

        ResponseEntity<?> postUpdateCaseResponse = caseManagementController.updateCase(
                TEST_CASE_ID,
                CASE_TEST_AUTHORIZATION,
                EventEnum.UPDATE,
                caseDataUpdate);

        assertNotNull(testPreUpdResponse);
        assertEquals(HttpStatus.OK, postUpdateCaseResponse.getStatusCode());
    }

    @Test
    void testDssUpdateCaseData() throws Exception {
        String caseDataJson = loadJson(CASE_DATA_FILE_FGM);
        CaseData caseData = mapper.readValue(caseDataJson, CaseData.class);

        Map<String, Object> caseDataMap = new ConcurrentHashMap<>();

        caseDataMap.put(CASE_DATA_FGM_ID, caseData);
        CaseResponse caseResponse = CaseResponse.builder().caseData(caseDataMap).build();
        List<ListValue<DssDocumentInfo>> dssDocumentInfoList = new ArrayList<>();
        DssCaseRequest dssCaseRequest = DssCaseRequest.builder().dssDocumentInfoList(dssDocumentInfoList).build();
        caseResponse.setId(TEST_CASE_ID);

        when(caseManagementService.updateDssCase(CASE_TEST_AUTHORIZATION, EventEnum.UPDATE,
                dssCaseRequest, TEST_CASE_ID)).thenReturn(caseResponse);
        when(authorisationService.authoriseService(CASE_TEST_AUTHORIZATION)).thenReturn(true);
        when(systemUserService.getSysUserToken()).thenReturn(CASE_TEST_AUTHORIZATION);

        ResponseEntity<?> preUpdateCaseResponse = caseManagementController.updateDssCase(
                TEST_CASE_ID,
                CASE_TEST_AUTHORIZATION,
                EventEnum.UPDATE,
                dssCaseRequest);

        CaseResponse testPreUpdResponse = (CaseResponse) preUpdateCaseResponse.getBody();

        ResponseEntity<?> postUpdateCaseResponse = caseManagementController.updateDssCase(
                TEST_CASE_ID,
                CASE_TEST_AUTHORIZATION,
                EventEnum.UPDATE,
                dssCaseRequest);

        assertNotNull(postUpdateCaseResponse);
        assertEquals(testPreUpdResponse, postUpdateCaseResponse.getBody());
        assertEquals(HttpStatus.OK, postUpdateCaseResponse.getStatusCode());
    }

    @Test
    void testFetchCaseDetails() throws IOException {

        String caseDataJson = loadJson(CASE_DATA_FILE_FGM);
        CaseData caseData = mapper.readValue(caseDataJson, CaseData.class);

        Map<String, Object> caseDataMap = new ConcurrentHashMap<>();
        caseDataMap.put(CASE_DATA_FGM_ID, caseData);

        CaseResponse caseResponse = CaseResponse.builder().caseData(caseDataMap).build();
        caseResponse.setId(TEST_CASE_ID);
        caseResponse.setStatus(null);

        when(caseManagementService.fetchCaseDetails(CASE_TEST_AUTHORIZATION, TEST_CASE_ID)).thenReturn(caseResponse);

        ResponseEntity<?> postFetchCaseResponse = caseManagementController.fetchCaseDetails(
                TEST_CASE_ID,
                CASE_TEST_AUTHORIZATION);

        CaseResponse caseDataFetchResponse = (CaseResponse) (postFetchCaseResponse.getBody());

        assertEquals(caseDataFetchResponse.getId(), caseResponse.getId());
        assertEquals(HttpStatus.OK, postFetchCaseResponse.getStatusCode());

    }

    @Test
    void testFetchDssQuestionAnswerDetails() {

        DssCaseResponse expectedDssCaseResponse = DssCaseResponse.builder().build();
        when(caseManagementService.fetchDssQuestionAnswerDetails(CASE_TEST_AUTHORIZATION, TEST_CASE_ID))
                .thenReturn(expectedDssCaseResponse);
        when(authorisationService.authoriseService(CASE_TEST_AUTHORIZATION)).thenReturn(true);
        when(systemUserService.getSysUserToken()).thenReturn(CASE_TEST_AUTHORIZATION);

        ResponseEntity<?> postFetchCaseResponse = caseManagementController.fetchDssQuestionAnswerDetails(
                TEST_CASE_ID,
                CASE_TEST_AUTHORIZATION);

        DssCaseResponse actualDssCaseResponse = (DssCaseResponse) postFetchCaseResponse.getBody();

        assertEquals(expectedDssCaseResponse, actualDssCaseResponse);
        assertEquals(HttpStatus.OK,postFetchCaseResponse.getStatusCode());
    }

}
