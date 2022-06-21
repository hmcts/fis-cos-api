package uk.gov.hmcts.reform.cosapi.functional.controllers;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.reform.cosapi.functional.idam.IdamTokenGenerator;
import uk.gov.hmcts.reform.cosapi.idam.IdamService;
import uk.gov.hmcts.reform.idam.client.IdamApi;
import uk.gov.hmcts.reform.idam.client.IdamClient;
import uk.gov.hmcts.reform.idam.client.models.User;

@EnableFeignClients(basePackages = {"uk.gov.hmcts.reform.idam.client"})
@EnableAutoConfiguration
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {IdamClient.class, IdamApi.class, IdamTokenGenerator.class, IdamService.class})
@RunWith(SpringRunner.class)
@ContextConfiguration
@PropertySource("classpath:application.yaml")
public class IdamManagementControllerTest {

    @Autowired
    private IdamTokenGenerator idamTokenGenerator;

    @Autowired
    private IdamService idamService;

    @Test
    public void userDetails() throws Exception {

        String token = idamTokenGenerator.generateIdamTokenForSystem();
        User user = idamService.retrieveUser(token);
        Assertions.assertNotNull(user);
        Assertions.assertNotNull(user.getUserDetails());
        Assertions.assertNotNull(user.getUserDetails().getForename());
        Assertions.assertNotNull(user.getUserDetails().getSurname());
        Assertions.assertNotNull(user.getUserDetails().getEmail());
    }

}
