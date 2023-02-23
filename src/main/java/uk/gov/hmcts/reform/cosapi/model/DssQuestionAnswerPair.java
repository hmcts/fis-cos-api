package uk.gov.hmcts.reform.cosapi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class DssQuestionAnswerPair {
    private String question;
    private String answer;
}
