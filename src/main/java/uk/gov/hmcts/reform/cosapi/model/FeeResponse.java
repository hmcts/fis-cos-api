package uk.gov.hmcts.reform.cosapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FeeResponse {

    private String feeCode;
    private String feeDescription;
    private Integer feeVersion;
    @JsonProperty(value = "fee_amount")
    private BigDecimal feeAmount;


}
