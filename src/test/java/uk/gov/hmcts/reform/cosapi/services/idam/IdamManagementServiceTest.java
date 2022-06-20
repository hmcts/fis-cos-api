package uk.gov.hmcts.reform.cosapi.services.idam;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.hmcts.reform.cosapi.idam.IdamService;
import uk.gov.hmcts.reform.cosapi.model.idam.IdamUser;
import uk.gov.hmcts.reform.idam.client.models.User;
import uk.gov.hmcts.reform.idam.client.models.UserDetails;

@SpringBootTest
class IdamManagementServiceTest {

    public static final String AUTHORIZATION = "Authorization";
    @Mock
    private IdamService idamService;

    @InjectMocks
    private final IdamManagementService idamManagementService = new IdamManagementService();

    @Test
    void userDetails() {
        UserDetails userDetails = UserDetails.builder()
            .forename("hmcts")
            .surname("gov")
            .email("gov@hmcts.net")
            .build();
        User expectedUser = new User("AuthToken", userDetails);
        Mockito.when(idamService.retrieveUser(AUTHORIZATION)).thenReturn(expectedUser);
        IdamUser actualUser = idamManagementService.getUserDetails(AUTHORIZATION);
        Assertions.assertEquals(expectedUser.getUserDetails().getForename(), actualUser.getFirstName());
        Assertions.assertEquals(expectedUser.getUserDetails().getSurname().get(), actualUser.getLastName());
        Assertions.assertEquals(expectedUser.getUserDetails().getEmail(), actualUser.getEmail());
    }

    @Test
    void userNull() {
        Mockito.when(idamService.retrieveUser(AUTHORIZATION)).thenReturn(null);
        IdamUser actualUser = idamManagementService.getUserDetails(AUTHORIZATION);
        Assertions.assertEquals(null, actualUser);
    }
}
