package uk.gov.hmcts.reform.cosapi.edgecase.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.ccd.sdk.api.CCD;
import uk.gov.hmcts.reform.cosapi.common.MappableObject;
import uk.gov.hmcts.reform.cosapi.edgecase.model.access.CaseworkerAccess;
import uk.gov.hmcts.reform.cosapi.edgecase.model.access.DefaultAccess;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class CaseData implements MappableObject {

    @CCD(
        label = "Named Applicant",
        access = {DefaultAccess.class}
    )
    private String namedApplicant;

    @CCD(
        label = "caseTypeOfApplication",
        access = {CaseworkerAccess.class}
    )
    private String caseTypeOfApplication;

    @CCD(access = {CaseworkerAccess.class})
    private String hyphenatedCaseRef;

    @JsonUnwrapped(prefix = "applicant")
    @Builder.Default
    @CCD(access = {DefaultAccess.class})
    private Applicant applicant = new Applicant();

    @JsonIgnore
    public String formatCaseRef(long caseId) {
        String temp = String.format("%016d", caseId);
        return String.format("%4s-%4s-%4s-%4s",
                             temp.substring(0, 4),
                             temp.substring(4, 8),
                             temp.substring(8, 12),
                             temp.substring(12, 16)
        );
    }

}
