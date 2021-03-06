package uk.gov.hmcts.reform.cosapi.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.cosapi.common.config.AppsConfig;
import uk.gov.hmcts.reform.cosapi.edgecase.event.EventEnum;
import uk.gov.hmcts.reform.cosapi.edgecase.model.CaseData;
import uk.gov.hmcts.reform.cosapi.exception.CaseCreateOrUpdateException;
import uk.gov.hmcts.reform.cosapi.model.CaseResponse;
import uk.gov.hmcts.reform.cosapi.services.ccd.CaseApiService;
import uk.gov.hmcts.reform.cosapi.util.AppsUtil;

@Service
@Slf4j
public class CaseManagementService {

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
                .id(caseDetails.getId()).status("Success").build();


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
            CaseDetails caseDetails = caseApiService.updateCase(authorization, event, caseId, caseData,
                                                                AppsUtil.getExactAppsDetails(appsConfig, caseData));
            log.info("Updated case details: " + caseDetails.toString());
            return CaseResponse.builder().caseData(caseDetails.getData())
                .id(caseDetails.getId()).status("Success").build();
        } catch (Exception e) {
            //This has to be corrected
            log.error("Error while updating case." + e);
            throw new CaseCreateOrUpdateException("Failing while updating the case" + e.getMessage(), e);
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


}
