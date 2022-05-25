package uk.gov.hmcts.reform.cosapi.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.cosapi.edgecase.model.CaseData;
import uk.gov.hmcts.reform.cosapi.model.CaseResponse;
import uk.gov.hmcts.reform.cosapi.services.ccd.CaseApiService;

@Service
@Slf4j
public class CaseManagementService {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    CaseApiService caseApiService;

    public CaseResponse createCase(String authorization, CaseData caseData) {
        try {
            // Validate Case Data (CHECKING CASE TYPE ALONE)

            // Submiiting case to CCD.
            CaseDetails caseDetails = caseApiService.createCase(authorization, caseData);
            log.info("Created case details: " + caseDetails.toString());
            return CaseResponse.builder().caseData(caseDetails.getData())
                .id(caseDetails.getId()).status("Success").build();


        } catch (Exception e) {
            log.error("Error while creating case." + e.getStackTrace());
        }
        return null;
    }

    public CaseResponse updateCase(String authorization, CaseData caseData, Long caseId) {
        try {
            // Validate Case Data (CHECKING CASE TYPE ALONE)

            // Submiiting case to CCD..
            CaseDetails caseDetails = caseApiService.updateCase(authorization, caseId, caseData);
            log.info("Updated case details: " + caseDetails.toString());
            return CaseResponse.builder().caseData(caseDetails.getData())
                .id(caseDetails.getId()).status("Success").build();
        } catch (Exception e) {
            //This has to be corrected
            log.error("Error while creating case." + e.getStackTrace());
        }
        return null;
    }
}