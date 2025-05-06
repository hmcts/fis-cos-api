package uk.gov.hmcts.reform.cosapi.services.ccd;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

import static java.util.Objects.nonNull;
import static uk.gov.hmcts.reform.cosapi.constants.CommonConstants.DSS_UPDATE_CASE_CCD_EVENT;

@Service
@Slf4j
@SuppressWarnings("PMD")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CaseApiService {

    private final CoreCaseDataApi coreCaseDataApi;

    private final AuthTokenGenerator authTokenGenerator;

    private final SystemUserService systemUserService;

    public CaseDetails createCase(String authorization, CaseData caseData,
            AppsConfig.AppsDetails appsDetails) {

        String userId = systemUserService.getUserId(authorization);

        return coreCaseDataApi.submitForCitizen(
                authorization,
                authTokenGenerator.generate(),
                userId,
                appsDetails.getJurisdiction(),
                appsDetails.getCaseType(),
                true,
                getCaseDataContent(authorization, caseData, userId, appsDetails));
    }

    public CaseDetails updateCase(String authorization, EventEnum eventEnum, Long caseId,
            Object caseData, AppsConfig.AppsDetails appsDetails, boolean isCitizen) {

        String userId = systemUserService.getUserId(authorization);

        if (isCitizen) {
            return coreCaseDataApi.submitEventForCitizen(
                    authorization,
                    authTokenGenerator.generate(),
                    userId,
                    appsDetails.getJurisdiction(),
                    appsDetails.getCaseType(),
                    String.valueOf(caseId),
                    true,
                    getCaseDataContent(authorization, caseData, eventEnum, userId,
                            String.valueOf(caseId), appsDetails, isCitizen));
        } else {
            return coreCaseDataApi.submitEventForCaseWorker(
                    authorization,
                    authTokenGenerator.generate(),
                    userId,
                    appsDetails.getJurisdiction(),
                    appsDetails.getCaseType(),
                    String.valueOf(caseId),
                    true,
                    getCaseDataContent(authorization, caseData, eventEnum, userId,
                            String.valueOf(caseId), appsDetails, isCitizen));
        }
    }

    public CaseDetails updateDssCaseJourney(String authorization, EventEnum eventEnum, Long caseId,
            Object caseData, String caseTypeId, String jurisdiction) {

        String userId = systemUserService.getUserId(authorization);

        return coreCaseDataApi.submitEventForCaseWorker(
                authorization,
                authTokenGenerator.generate(),
                userId,
                jurisdiction,
                caseTypeId,
                String.valueOf(caseId),
                true,
                getCaseDataContentForDssUpdateCaseJourney(authorization, caseData, eventEnum, userId,
                        String.valueOf(caseId), caseTypeId, jurisdiction));
    }

    private CaseDataContent getCaseDataContent(String authorization, CaseData caseData, String userId,
            AppsConfig.AppsDetails appsDetails) {
        return CaseDataContent.builder()
                .data(caseData)
                .event(Event.builder().id(appsDetails.getEventIds().getCreateEvent()).build())
                .eventToken(
                        getEventToken(authorization, userId, appsDetails.getEventIds().getCreateEvent(), appsDetails))
                .build();
    }

    private CaseDataContent getCaseDataContent(String authorization, Object caseData, EventEnum eventEnum,
            String userId, String caseId, AppsConfig.AppsDetails appsDetails,
            boolean isCitizen) {
        CaseDataContent.CaseDataContentBuilder builder = CaseDataContent.builder().data(caseData);
        if (eventEnum.getEventType().equalsIgnoreCase(EventEnum.UPDATE.getEventType())) {
            builder.event(Event.builder().id(appsDetails.getEventIds().getUpdateEvent()).build())
                    .eventToken(
                            getEventTokenForUpdate(authorization, userId, appsDetails.getEventIds().getUpdateEvent(),
                                    caseId, appsDetails, isCitizen));
        } else if (eventEnum.getEventType().equalsIgnoreCase(EventEnum.SUBMIT.getEventType())) {
            builder.event(Event.builder().id(appsDetails.getEventIds().getSubmitEvent()).build())
                    .eventToken(
                            getEventTokenForUpdate(authorization, userId, appsDetails.getEventIds().getSubmitEvent(),
                                    caseId, appsDetails, isCitizen));
        }

        return builder.build();
    }

    private CaseDataContent getCaseDataContentForDssUpdateCaseJourney(String authorization, Object caseData,
            EventEnum eventEnum,
            String userId, String caseId, String caseTypeId, String jurisdiction) {
        CaseDataContent.CaseDataContentBuilder builder = CaseDataContent.builder().data(caseData);
        if (eventEnum.getEventType().equalsIgnoreCase(EventEnum.UPDATE.getEventType())) {
            builder.event(Event.builder().id(DSS_UPDATE_CASE_CCD_EVENT).build())
                    .eventToken(generateEventToken(authorization, userId, DSS_UPDATE_CASE_CCD_EVENT,
                            caseId, caseTypeId, jurisdiction, false));
        }

        return builder.build();
    }

    public String generateEventToken(String authorization, String userId, String eventId, String caseId,
            String caseTypeId, String jurisdiction, boolean isCitizen) {
        StartEventResponse res;
        if (isCitizen) {
            res = coreCaseDataApi.startEventForCitizen(authorization,
                    authTokenGenerator.generate(),
                    userId,
                    jurisdiction,
                    caseTypeId,
                    caseId,
                    eventId);
        } else {
            res = coreCaseDataApi.startEventForCaseWorker(authorization,
                    authTokenGenerator.generate(),
                    userId,
                    jurisdiction,
                    caseTypeId,
                    caseId,
                    eventId);
        }

        return nonNull(res) ? res.getToken() : null;
    }

    public String getEventToken(String authorization, String userId, String eventId,
            AppsConfig.AppsDetails appsDetails) {
        StartEventResponse res = coreCaseDataApi.startForCitizen(authorization,
                authTokenGenerator.generate(),
                userId,
                appsDetails.getJurisdiction(),
                appsDetails.getCaseType(),
                eventId);

        // This has to be removed
        log.info("Response of create event token: " + res.getToken());

        return nonNull(res) ? res.getToken() : null;
    }

    public String getEventTokenForUpdate(String authorization, String userId, String eventId, String caseId,
            AppsConfig.AppsDetails appsDetails, boolean isCitizen) {
        StartEventResponse res;
        if (isCitizen) {
            res = coreCaseDataApi.startEventForCitizen(authorization,
                    authTokenGenerator.generate(),
                    userId,
                    appsDetails.getJurisdiction(),
                    appsDetails.getCaseType(),
                    caseId,
                    eventId);
        } else {
            res = coreCaseDataApi.startEventForCaseWorker(authorization,
                    authTokenGenerator.generate(),
                    userId,
                    appsDetails.getJurisdiction(),
                    appsDetails.getCaseType(),
                    caseId,
                    eventId);
        }

        // This has to be removed
        log.info("Response of update event token: " + res.getToken());

        return nonNull(res) ? res.getToken() : null;
    }

    public CaseDetails getCaseDetails(String authorization, Long caseId) {

        return coreCaseDataApi.getCase(
                authorization,
                authTokenGenerator.generate(),
                String.valueOf(caseId));
    }
}
