package uk.gov.hmcts.reform.cosapi.services.ccd;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi;
import uk.gov.hmcts.reform.ccd.client.model.CaseDataContent;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.ccd.client.model.Event;
import uk.gov.hmcts.reform.ccd.client.model.StartEventResponse;
import uk.gov.hmcts.reform.cosapi.common.config.AppsConfig;
import uk.gov.hmcts.reform.cosapi.edgecase.event.EventEnum;
import uk.gov.hmcts.reform.cosapi.edgecase.model.CaseData;
import uk.gov.hmcts.reform.cosapi.services.SystemUserService;
import uk.gov.hmcts.reform.cosapi.util.AppsUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.cosapi.constants.CommonConstants.PRL_CASE_TYPE;
import static uk.gov.hmcts.reform.cosapi.constants.CommonConstants.PRL_JURISDICTION;
import static uk.gov.hmcts.reform.cosapi.util.TestConstant.CASE_DATA_FILE_FGM;
import static uk.gov.hmcts.reform.cosapi.util.TestConstant.CASE_DATA_FGM_ID;
import static uk.gov.hmcts.reform.cosapi.util.TestConstant.TEST_USER;
import static uk.gov.hmcts.reform.cosapi.util.TestConstant.TEST_AUTHORIZATION_TOKEN;
import static uk.gov.hmcts.reform.cosapi.util.TestConstant.CASE_TEST_AUTHORIZATION;
import static uk.gov.hmcts.reform.cosapi.util.TestConstant.TEST_CASE_ID;
import static uk.gov.hmcts.reform.cosapi.util.TestFileUtil.loadJson;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestPropertySource("classpath:application.yaml")
@ActiveProfiles("test")
@SuppressWarnings("PMD")
class CaseApiServiceTest {
    public static final String CASEWORKER_UPDATE_DSS_APPLICATION = "caseworker-update-dss-application";
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private static final String TEST_CASE_REFERENCE = "123";
    private AppsConfig.AppsDetails fgmAppDetails;

    @InjectMocks
    private CaseApiService caseApiService;

    @Mock
    private AuthTokenGenerator authTokenGenerator;

    @Mock
    SystemUserService systemUserService;

    @Mock
    StartEventResponse eventRes;

    @Autowired
    AppsConfig appsConfig;

    @Mock
    CoreCaseDataApi coreCaseDataApi;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        fgmAppDetails = appsConfig.getApps().stream().filter(eachApps -> eachApps.getCaseTypeOfApplication().contains(
            CASE_DATA_FGM_ID)).findAny().orElse(null);

    }

    @Test
    void testFgmCreateCaseData() throws Exception {
        String caseDataJson = loadJson(CASE_DATA_FILE_FGM);
        CaseData caseData = mapper.readValue(caseDataJson, CaseData.class);

        Map<String, Object> caseDataMap = new ConcurrentHashMap<>();

        caseDataMap.put(TEST_CASE_REFERENCE, caseData);
        CaseDetails caseDetail = CaseDetails.builder().caseTypeId(CASE_DATA_FGM_ID)
            .id(TEST_CASE_ID)
            .data(caseDataMap)
            .jurisdiction(PRL_JURISDICTION)
            .build();

        eventRes = StartEventResponse.builder()
            .eventId(AppsUtil.getExactAppsDetailsByCaseType(appsConfig, PRL_CASE_TYPE).getEventIds().getSubmitEvent())
            .caseDetails(caseDetail)
            .token(TEST_AUTHORIZATION_TOKEN)
            .build();

        when(systemUserService.getUserId(CASE_TEST_AUTHORIZATION)).thenReturn(TEST_USER);

        when(authTokenGenerator.generate()).thenReturn(TEST_USER);

        when(coreCaseDataApi.startForCitizen(
            CASE_TEST_AUTHORIZATION,
            authTokenGenerator.generate(),
            TEST_USER,
            PRL_JURISDICTION,
            PRL_CASE_TYPE,
            AppsUtil.getExactAppsDetailsByCaseType(appsConfig, PRL_CASE_TYPE).getEventIds().getCreateEvent()
        )).thenReturn(eventRes);

        CaseDataContent caseDataContent = CaseDataContent.builder()
            .data(caseData)
            .event(Event.builder().id(AppsUtil.getExactAppsDetailsByCaseType(
                appsConfig,
                PRL_CASE_TYPE
            ).getEventIds().getCreateEvent()).build())
            .eventToken(TEST_AUTHORIZATION_TOKEN)
            .build();

        when(coreCaseDataApi.submitForCitizen(
            CASE_TEST_AUTHORIZATION,
            authTokenGenerator.generate(),
            TEST_USER,
            PRL_JURISDICTION,
            PRL_CASE_TYPE,
            true,
            caseDataContent
        )).thenReturn(caseDetail);

        CaseDetails createCaseDetail = caseApiService.createCase(CASE_TEST_AUTHORIZATION, caseData, fgmAppDetails);

        assertEquals(CASE_DATA_FGM_ID, createCaseDetail.getCaseTypeId());
        assertEquals(createCaseDetail.getId(), caseDetail.getId());
        assertEquals(createCaseDetail.getCaseTypeId(), caseDetail.getCaseTypeId());
        assertEquals(createCaseDetail.getData(), caseDetail.getData());
        assertEquals(createCaseDetail.getData().get(TEST_CASE_REFERENCE), caseDataMap.get(TEST_CASE_REFERENCE));
    }

    @Test
    void testFgmUpdateCaseData() throws Exception {
        String caseDataJson = loadJson(CASE_DATA_FILE_FGM);
        CaseData caseData = mapper.readValue(caseDataJson, CaseData.class);

        Map<String, Object> caseDataMap = new ConcurrentHashMap<>();

        caseDataMap.put(TEST_CASE_REFERENCE, caseData);
        CaseDetails caseDetail = CaseDetails.builder().caseTypeId(CASE_DATA_FGM_ID)
            .id(TEST_CASE_ID)
            .data(caseDataMap)
            .jurisdiction(PRL_JURISDICTION)
            .build();

        String userId = TEST_USER;
        eventRes = StartEventResponse.builder()
            .eventId(String.valueOf(Event.builder().id(AppsUtil.getExactAppsDetailsByCaseType(
                appsConfig,
                PRL_CASE_TYPE
            ).getEventIds().getUpdateEvent())))
            .caseDetails(caseDetail)
            .token(TEST_AUTHORIZATION_TOKEN)
            .build();
        when(coreCaseDataApi.startEventForCitizen(
            CASE_TEST_AUTHORIZATION,
            authTokenGenerator.generate(),
            userId,
            PRL_JURISDICTION,
            PRL_CASE_TYPE,
            String.valueOf(TEST_CASE_ID),
            AppsUtil.getExactAppsDetailsByCaseType(
                appsConfig,
                PRL_CASE_TYPE
            ).getEventIds().getUpdateEvent()
        )).thenReturn(eventRes);

        when(systemUserService.getUserId(CASE_TEST_AUTHORIZATION)).thenReturn(userId);

        when(authTokenGenerator.generate()).thenReturn(userId);

        when(coreCaseDataApi.startEventForCitizen(
            CASE_TEST_AUTHORIZATION,
            authTokenGenerator.generate(),
            userId,
            PRL_JURISDICTION,
            PRL_CASE_TYPE,
            TEST_CASE_REFERENCE,
            AppsUtil.getExactAppsDetailsByCaseType(
                appsConfig,
                PRL_CASE_TYPE
            ).getEventIds().getUpdateEvent()
        )).thenReturn(eventRes);

        CaseDataContent caseDataContent = CaseDataContent.builder()
            .data(caseData)
            .event(Event.builder().id(AppsUtil.getExactAppsDetailsByCaseType(
                appsConfig,
                PRL_CASE_TYPE
            ).getEventIds().getUpdateEvent()).build())
            .eventToken(TEST_AUTHORIZATION_TOKEN)
            .build();

        when(coreCaseDataApi.submitEventForCitizen(
            CASE_TEST_AUTHORIZATION,
            authTokenGenerator.generate(),
            userId,
            PRL_JURISDICTION,
            PRL_CASE_TYPE,
            TEST_CASE_REFERENCE,
            true,
            caseDataContent
        )).thenReturn(caseDetail);

        CaseDetails createCaseDetail = caseApiService.updateCase(
            CASE_TEST_AUTHORIZATION,
            EventEnum.UPDATE,
            TEST_CASE_ID,
            caseData,
            fgmAppDetails,
                true
        );

        assertEquals(CASE_DATA_FGM_ID, createCaseDetail.getCaseTypeId());
        assertEquals(createCaseDetail.getId(), caseDetail.getId());
        assertEquals(createCaseDetail.getCaseTypeId(), caseDetail.getCaseTypeId());
        assertEquals(createCaseDetail.getData(), caseDetail.getData());
        assertEquals(createCaseDetail.getData().get(TEST_CASE_REFERENCE), caseDataMap.get(TEST_CASE_REFERENCE));
    }

    @Test
    void getCaseDetails() throws Exception {
        String caseDatajson = loadJson(CASE_DATA_FILE_FGM);
        CaseData caseData = mapper.readValue(caseDatajson,CaseData.class);

        Map<String, Object> caseDataMap = new ConcurrentHashMap<>();
        caseDataMap.put(TEST_CASE_REFERENCE, caseData);

        CaseDetails caseDetail = CaseDetails.builder().caseTypeId(CASE_DATA_FGM_ID)
            .id(TEST_CASE_ID)
            .data(caseDataMap)
            .jurisdiction(PRL_JURISDICTION)
            .build();


        when(coreCaseDataApi.getCase(
            CASE_TEST_AUTHORIZATION,
            authTokenGenerator.generate(),
            String.valueOf(TEST_CASE_ID)))
            .thenReturn(caseDetail);

        CaseDetails caseDetails = caseApiService.getCaseDetails(
            CASE_TEST_AUTHORIZATION,
            TEST_CASE_ID);

        assertEquals(caseDetails.getId(),caseDetail.getId());
        assertEquals(caseDetails.getData(),caseDetails.getData());

    }

    @Test
    void testDssUpdateCaseData() throws Exception {
        String caseDataJson = loadJson(CASE_DATA_FILE_FGM);
        CaseData caseData = mapper.readValue(caseDataJson, CaseData.class);

        Map<String, Object> caseDataMap = new ConcurrentHashMap<>();

        caseDataMap.put(TEST_CASE_REFERENCE, caseData);
        CaseDetails caseDetail = CaseDetails.builder().caseTypeId(CASE_DATA_FGM_ID)
                .id(TEST_CASE_ID)
                .data(caseDataMap)
                .jurisdiction(PRL_JURISDICTION)
                .build();

        String userId = TEST_USER;
        eventRes = StartEventResponse.builder()
                .eventId(String.valueOf(Event.builder().id(AppsUtil.getExactAppsDetailsByCaseType(
                        appsConfig,
                        PRL_CASE_TYPE
                ).getEventIds().getUpdateEvent())))
                .caseDetails(caseDetail)
                .token(TEST_AUTHORIZATION_TOKEN)
                .build();
        when(coreCaseDataApi.startEventForCaseWorker(
                CASE_TEST_AUTHORIZATION,
                authTokenGenerator.generate(),
                userId,
                PRL_JURISDICTION,
                PRL_CASE_TYPE,
                String.valueOf(TEST_CASE_ID),
                AppsUtil.getExactAppsDetailsByCaseType(
                        appsConfig,
                        PRL_CASE_TYPE
                ).getEventIds().getUpdateEvent()
        )).thenReturn(eventRes);

        when(systemUserService.getUserId(CASE_TEST_AUTHORIZATION)).thenReturn(userId);

        when(authTokenGenerator.generate()).thenReturn(userId);

        when(coreCaseDataApi.startEventForCaseWorker(
                CASE_TEST_AUTHORIZATION,
                authTokenGenerator.generate(),
                userId,
                PRL_JURISDICTION,
                PRL_CASE_TYPE,
                TEST_CASE_REFERENCE,
                AppsUtil.getExactAppsDetailsByCaseType(
                        appsConfig,
                        PRL_CASE_TYPE
                ).getEventIds().getUpdateEvent()
        )).thenReturn(eventRes);

        CaseDataContent caseDataContent = CaseDataContent.builder()
                .data(caseData)
                .event(Event.builder().id(AppsUtil.getExactAppsDetailsByCaseType(
                        appsConfig,
                        PRL_CASE_TYPE
                ).getEventIds().getUpdateEvent()).build())
                .eventToken(TEST_AUTHORIZATION_TOKEN)
                .build();

        when(coreCaseDataApi.submitEventForCaseWorker(
                CASE_TEST_AUTHORIZATION,
                authTokenGenerator.generate(),
                userId,
                PRL_JURISDICTION,
                PRL_CASE_TYPE,
                TEST_CASE_REFERENCE,
                true,
                caseDataContent
        )).thenReturn(caseDetail);

        CaseDetails createCaseDetail = caseApiService.updateCase(
                CASE_TEST_AUTHORIZATION,
                EventEnum.UPDATE,
                TEST_CASE_ID,
                caseData,
                fgmAppDetails,
                false
        );

        assertEquals(CASE_DATA_FGM_ID, createCaseDetail.getCaseTypeId());
        assertEquals(createCaseDetail.getId(), caseDetail.getId());
        assertEquals(createCaseDetail.getCaseTypeId(), caseDetail.getCaseTypeId());
        assertEquals(createCaseDetail.getData(), caseDetail.getData());
        assertEquals(createCaseDetail.getData().get(TEST_CASE_REFERENCE), caseDataMap.get(TEST_CASE_REFERENCE));
    }

    @Test
    void testDssupdateDssCaseJourney() throws Exception {

        String caseDataJson = loadJson(CASE_DATA_FILE_FGM);
        CaseData caseData = mapper.readValue(caseDataJson, CaseData.class);

        Map<String, Object> caseDataMap = new ConcurrentHashMap<>();

        caseDataMap.put(TEST_CASE_REFERENCE, caseData);
        CaseDetails caseDetail = CaseDetails.builder().caseTypeId(CASE_DATA_FGM_ID)
            .id(TEST_CASE_ID)
            .data(caseDataMap)
            .jurisdiction(PRL_JURISDICTION)
            .build();

        String userId = TEST_USER;
        eventRes = StartEventResponse.builder()
            .eventId(String.valueOf(Event.builder().id(CASEWORKER_UPDATE_DSS_APPLICATION)))
            .caseDetails(caseDetail)
            .token(TEST_AUTHORIZATION_TOKEN)
            .build();

        when(systemUserService.getUserId(CASE_TEST_AUTHORIZATION)).thenReturn(userId);

        when(authTokenGenerator.generate()).thenReturn(userId);

        when(coreCaseDataApi.startEventForCaseWorker(
            CASE_TEST_AUTHORIZATION,
            authTokenGenerator.generate(),
            userId,
            PRL_JURISDICTION,
            PRL_CASE_TYPE,
            TEST_CASE_REFERENCE,
            CASEWORKER_UPDATE_DSS_APPLICATION
        )).thenReturn(eventRes);

        CaseDataContent caseDataContent = CaseDataContent.builder()
            .data(caseData)
            .event(Event.builder().id(CASEWORKER_UPDATE_DSS_APPLICATION).build())
            .eventToken(TEST_AUTHORIZATION_TOKEN)
            .build();

        when(coreCaseDataApi.submitEventForCaseWorker(
            CASE_TEST_AUTHORIZATION,
            authTokenGenerator.generate(),
            userId,
            PRL_JURISDICTION,
            PRL_CASE_TYPE,
            TEST_CASE_REFERENCE,
            true,
            caseDataContent
        )).thenReturn(caseDetail);

        CaseDetails createCaseDetail = caseApiService.updateDssCaseJourney(
            CASE_TEST_AUTHORIZATION,
            EventEnum.UPDATE,
            TEST_CASE_ID,
            caseData,
            PRL_CASE_TYPE,
            PRL_JURISDICTION
        );

        assertEquals(CASE_DATA_FGM_ID, createCaseDetail.getCaseTypeId());
        assertEquals(createCaseDetail.getId(), caseDetail.getId());
        assertEquals(createCaseDetail.getCaseTypeId(), caseDetail.getCaseTypeId());
        assertEquals(createCaseDetail.getData(), caseDetail.getData());
        assertEquals(createCaseDetail.getData().get(TEST_CASE_REFERENCE), caseDataMap.get(TEST_CASE_REFERENCE));

    }
}
