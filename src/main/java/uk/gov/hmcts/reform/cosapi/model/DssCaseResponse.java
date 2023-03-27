package uk.gov.hmcts.reform.cosapi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class DssCaseResponse {
    private Long caseId;
    private String dssHeaderDetails;
    private List<DssQuestionAnswerPair> dssQuestionAnswerPairs;
    private List<DssQuestionAnswerDatePair> dssQuestionAnswerDatePairs;
    private String caseTypeId;
    private String jurisdiction;
}
