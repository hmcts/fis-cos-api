package uk.gov.hmcts.reform.cosapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FeeResponse {

    private String code;
    private String description;
    private Integer version;
    private BigDecimal feeAmount;


}
