package uk.gov.hmcts.reform.cosapi.config;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import uk.gov.hmcts.reform.cosapi.model.FeeApplicationType;

@Builder
@Data
@Getter
@Setter
@Configuration
@ConfigurationProperties("fees-register")
public class FeeRetrieveConfiguration {

    private Map<FeeApplicationType, FeeParameters> parameters;

    public FeeParameters getFeeParametersByFeeType(FeeApplicationType feeType) {
        return parameters.get(feeType);
    }

    @Builder
    @Data
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class FeeParameters {
        private String channel;
        private String event;
        private String jurisdiction1;
        private String jurisdiction2;
        private String keyword;
        private String service;
    }

}
