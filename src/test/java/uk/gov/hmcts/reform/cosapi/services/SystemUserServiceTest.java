package uk.gov.hmcts.reform.cosapi.services;

import com.microsoft.applicationinsights.boot.dependencies.apachecommons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.cosapi.config.SystemUserConfiguration;
import uk.gov.hmcts.reform.idam.client.IdamClient;
import uk.gov.hmcts.reform.idam.client.models.UserInfo;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class SystemUserServiceTest {

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    @Mock
    IdamClient idamClient;

    @Mock
    SystemUserConfiguration userConfig;

    @InjectMocks
    SystemUserService systemUserService;

    String token = "";

    @BeforeEach
    public void setUp() {
        systemUserService = new SystemUserService(idamClient, userConfig);
        token = RandomStringUtils.randomAlphanumeric(10);
    }

    @Test
    void shouldReturnSystemUserId() {
        UserInfo userInfo = UserInfo.builder()
            .uid(UUID.randomUUID().toString())
            .build();

        when(idamClient.getUserInfo(token)).thenReturn(userInfo);

        assertThat(userInfo.getUid()).isEqualTo(systemUserService.getUserId(token));
    }

    @Test
    void shouldReturnToken() {
        when(userConfig.getUserName()).thenReturn(USERNAME);
        when(userConfig.getPassword()).thenReturn(PASSWORD);
        when(idamClient.getAccessToken(anyString(), anyString())).thenReturn(token);

        assertThat(token).isEqualTo(systemUserService.getSysUserToken());
    }
}
