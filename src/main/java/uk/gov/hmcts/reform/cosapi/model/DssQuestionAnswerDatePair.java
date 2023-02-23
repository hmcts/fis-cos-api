package uk.gov.hmcts.reform.cosapi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class DssQuestionAnswerDatePair {
    private String question;
    private LocalDate answer;
}
