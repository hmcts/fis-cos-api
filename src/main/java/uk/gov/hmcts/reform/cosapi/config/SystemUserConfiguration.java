package uk.gov.hmcts.reform.cosapi.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class SystemUserConfiguration {

    private final String userName;
    private final String password;
    private final String test1;
    private final String test2;

    public SystemUserConfiguration(@Value("${idam.systemupdate.username}") String userName,
                                   @Value("${idam.systemupdate.password}") String password,
                                   @Value("${idam.client.id}") String test1,
                                   @Value("${idam.client.secret}") String test2) {
        this.userName = userName;
        this.password = password;
        this.test1 = test1;
        this.test2 = test2;
    }
}
