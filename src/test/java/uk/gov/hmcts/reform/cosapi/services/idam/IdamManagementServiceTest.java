package uk.gov.hmcts.reform.cosapi.services.idam;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.hmcts.reform.cosapi.idam.IdamService;
import uk.gov.hmcts.reform.idam.client.models.User;
import uk.gov.hmcts.reform.idam.client.models.UserDetails;

@SpringBootTest
class IdamManagementServiceTest {

    @Mock
    private IdamService idamService;

    @InjectMocks
    private IdamManagementService idamManagementService = new IdamManagementService();

    @Test
    void getUserDetails() {
        UserDetails userDetails = UserDetails.builder()
            .forename("hmcts")
            .surname("gov")
            .email("gov@hmcts.net")
            .build();
        uk.gov.hmcts.reform.idam.client.models.User expectedUser = new User("AuthToken", userDetails);
        Mockito.when(idamService.retrieveUser("Authorization")).thenReturn(expectedUser);
        uk.gov.hmcts.reform.cosapi.model.idam.User actualUser = idamManagementService.getUserDetails("Authorization");
        Assertions.assertEquals(expectedUser.getUserDetails().getForename(), actualUser.getFirstName());
        Assertions.assertEquals(expectedUser.getUserDetails().getSurname().get(), actualUser.getLastName());
        Assertions.assertEquals(expectedUser.getUserDetails().getEmail(), actualUser.getEmail());
    }

    @Test
    void userNull(){
        Mockito.when(idamService.retrieveUser("Authorization")).thenReturn(null);
        uk.gov.hmcts.reform.cosapi.model.idam.User actualUser = idamManagementService.getUserDetails("Authorization");
        Assertions.assertEquals(null, actualUser);
    }
}
