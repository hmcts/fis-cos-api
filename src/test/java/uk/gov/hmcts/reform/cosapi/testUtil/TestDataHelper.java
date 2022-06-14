package uk.gov.hmcts.reform.cosapi.testUtil;

import uk.gov.hmcts.reform.cosapi.edgecase.model.Applicant;
import uk.gov.hmcts.reform.cosapi.edgecase.model.CaseData;

import static com.ibm.icu.util.GenderInfo.Gender.FEMALE;

public class TestDataHelper {

    public static Applicant getApplicant() {
        return getApplicant();
    }
    public static CaseData caseData() {
        return CaseData.builder()
            .applicant(getApplicant())
            //.divorceOrDissolution(DIVORCE)
            //.caseInvite(new CaseInvite(null, null, null))
            .build();
    }
}
