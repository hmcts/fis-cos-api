package uk.gov.hmcts.reform.cosapi.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.cosapi.config.SystemUserConfiguration;
import uk.gov.hmcts.reform.idam.client.IdamClient;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class SystemUserService {

    private final IdamClient idamClient;
    private final SystemUserConfiguration userConfig;

    public String getUserId(String userToken) {
        return idamClient.getUserInfo(userToken).getUid();
    }

    public String getSysUserToken() {
        log.info("test1 {}", userConfig.getTest1());
        log.info("test2 {}", userConfig.getTest2());
        log.info("test3 {}", userConfig.getUserName());
        return idamClient.getAccessToken(userConfig.getUserName(), userConfig.getPassword());
    }
}
