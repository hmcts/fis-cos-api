package uk.gov.hmcts.reform.cosapi.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class SystemUserConfiguration {

    private final String userName;
    private final String password;

    public SystemUserConfiguration(@Value("${idam.systemupdate.username}") String userName,
                                   @Value("${idam.systemupdate.password}") String password) {
        this.userName = userName;
        this.password = password;
    }
}
