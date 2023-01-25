package uk.gov.hmcts.reform.cosapi.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ccd.sdk.type.ListValue;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.cosapi.common.config.AppsConfig;
import uk.gov.hmcts.reform.cosapi.edgecase.event.EventEnum;
import uk.gov.hmcts.reform.cosapi.edgecase.model.CaseData;
import uk.gov.hmcts.reform.cosapi.exception.CaseCreateOrUpdateException;
import uk.gov.hmcts.reform.cosapi.model.CaseResponse;
import uk.gov.hmcts.reform.cosapi.model.DssCaseResponse;
import uk.gov.hmcts.reform.cosapi.model.DssDocumentInfo;
import uk.gov.hmcts.reform.cosapi.model.DssQuestionAnswerDatePair;
import uk.gov.hmcts.reform.cosapi.model.DssQuestionAnswerPair;
import uk.gov.hmcts.reform.cosapi.services.ccd.CaseApiService;
import uk.gov.hmcts.reform.cosapi.util.AppsUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class CaseManagementService {

    private static final String SUCCESS = "Success";

    @Autowired
    CaseApiService caseApiService;

    @Autowired
    AppsConfig appsConfig;

    public CaseResponse createCase(String authorization, CaseData caseData) {
        try {
            // Validate Case Data (CHECKING CASE TYPE ALONE)
            log.info("Case data received from UI: " + caseData.toString());
            if (!AppsUtil.isValidCaseTypeOfApplication(appsConfig, caseData)) {
                throw new CaseCreateOrUpdateException("Invalid Case type application. Please check the request.");
            }

            // creating case to CCD.
            CaseDetails caseDetails = caseApiService.createCase(authorization, caseData,
                                                                AppsUtil.getExactAppsDetails(appsConfig, caseData));
            log.info("Created case details: " + caseDetails.toString());
            return CaseResponse.builder().caseData(caseDetails.getData())
                .id(caseDetails.getId()).status(SUCCESS).build();


        } catch (Exception e) {
            log.error("Error while creating case." + e);
            throw new CaseCreateOrUpdateException("Failing while creating the case" + e.getMessage(), e);
        }
    }

    public CaseResponse updateCase(String authorization, EventEnum event, CaseData caseData, Long caseId) {
        try {
            // Validate Case Type of application
            if (!AppsUtil.isValidCaseTypeOfApplication(appsConfig, caseData)) {
                throw new CaseCreateOrUpdateException("Invalid Case type application. Please check the request.");
            }

            // Updating or Submitting case to CCD..
            CaseDetails caseDetails = caseApiService.updateCase(authorization, event,
                    caseId, caseData, AppsUtil.getExactAppsDetails(appsConfig, caseData), true);
            log.info("Updated case details: " + caseDetails.toString());
            return CaseResponse.builder().caseData(caseDetails.getData())
                .id(caseDetails.getId()).status("Success").build();
        } catch (Exception e) {
            //This has to be corrected
            log.error("Error while updating case." + e);
            throw new CaseCreateOrUpdateException("Failing while updating the case" + e.getMessage(), e);
        }
    }

    public CaseResponse updateDssCase(String authorisation, EventEnum event,
                                      List<ListValue<DssDocumentInfo>> dssDocumentInfoList, Long caseId) {
        try {
            CaseDetails retrievedCaseDetails = caseApiService.getCaseDetails(authorisation, caseId);
            Map<String, Object> caseData = retrievedCaseDetails.getData();
            caseData.put("dssDocuments", dssDocumentInfoList);
            CaseDetails caseDetails = caseApiService.updateCase(authorisation, event, caseId,
                    caseData,
                    AppsUtil.getExactAppsDetails(appsConfig,
                            new ObjectMapper().convertValue(caseData, CaseData.class)), false);
            return CaseResponse.builder().caseData(caseDetails.getData())
                    .id(caseDetails.getId()).status(SUCCESS).build();
        } catch (Exception e) {
            log.error("Error while updating case." + e);
            throw new CaseCreateOrUpdateException("Failing while updating the dss case" + e.getMessage(), e);
        }

    }

    public CaseResponse fetchCaseDetails(String authorization,Long caseId) {

        try {
            CaseDetails caseDetails = caseApiService.getCaseDetails(authorization,
                                                                    caseId);
            log.info("Case Details for CaseID :{} and CaseDetails:{}", caseId, caseDetails);
            return CaseResponse.builder().caseData(caseDetails.getData())
                .id(caseDetails.getId()).status("Success").build();
        } catch (Exception e) {
            log.error("Error while fetching Case Details" + e);
            throw new CaseCreateOrUpdateException("Failing while fetcing the case details" + e.getMessage(), e);
        }

    }

    public DssCaseResponse fetchDssQuestionAnswerDetails(String authorization, Long caseId) {
        try {
            CaseDetails caseDetails = caseApiService.getCaseDetails(authorization, caseId);
            return DssCaseResponse.builder().dssQuestionAnswerPairs(buildDssQuestionAnswerPairs(caseDetails.getData()))
                    .dssQuestionAnswerDatePairs(buildDssQuestionAnswerDatePairs(caseDetails.getData())).build();
        } catch (Exception e) {
            log.error("Error while fetching Dss Case Details" + e);
            throw new CaseCreateOrUpdateException("Failing while fetching the dss case details" + e.getMessage(), e);
        }
    }

    private List<DssQuestionAnswerDatePair> buildDssQuestionAnswerDatePairs(Map<String, Object> data) {
        DssQuestionAnswerDatePair dssQuestionAnswerPair1 = DssQuestionAnswerDatePair
                .builder()
                .question((String) data.get("dssQuestion3"))
                .answer(LocalDate.parse(retrieveCaseDataValue((String) data.get("dssAnswer3"), data)))
                .build();
        return List.of(dssQuestionAnswerPair1);
    }

    private List<DssQuestionAnswerPair> buildDssQuestionAnswerPairs(Map<String, Object> data) {
        DssQuestionAnswerPair dssQuestionAnswerPair1 = DssQuestionAnswerPair
                .builder()
                .question((String) data.get("dssQuestion1"))
                .answer(retrieveCaseDataValue((String) data.get("dssAnswer1"), data))
                .build();

        DssQuestionAnswerPair dssQuestionAnswerPair2 = DssQuestionAnswerPair
                .builder()
                .question((String) data.get("dssQuestion2"))
                .answer(retrieveCaseDataValue((String) data.get("dssAnswer2"), data))
                .build();
        return List.of(dssQuestionAnswerPair1, dssQuestionAnswerPair2);
    }

    @SuppressWarnings({"unchecked"})
    private String retrieveCaseDataValue(String answerField, Map<String, Object> data) {
        String[] fieldList = answerField.split("[.]", 0);
        Map<String, Object> caseData = data;
        String value = null;

        for (int id = 1; id < fieldList.length; id++) {
            if (id == fieldList.length - 1) {
                value = (String) caseData.get(fieldList[id]);
                break;
            }
            caseData = (Map<String, Object>) caseData.get(fieldList[id]);
        }
        return value;
    }
}
