package uk.gov.hmcts.reform.cosapi.controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.cosapi.model.idam.IdamUser;
import uk.gov.hmcts.reform.cosapi.services.idam.IdamManagementService;

@SpringBootTest
class IdamManagementControllerTest {

    public static final String AUTHORIZATION = "Authorization";

    @Mock
    private IdamManagementService idamManagementService;

    @InjectMocks
    private final IdamManagementController idamManagementController = new IdamManagementController();

    @Test
    void userDetails() {
        IdamUser user = IdamUser.builder()
            .firstName("hmcts")
            .lastName("gov")
            .email("gov@htcts.net")
            .build();
        Mockito.when(idamManagementService.getUserDetails(AUTHORIZATION)).thenReturn(user);
        ResponseEntity<?> responseEntity = idamManagementController.getUserDetails(AUTHORIZATION);
        IdamUser responseEntityBody = (IdamUser) responseEntity.getBody();
        Assertions.assertEquals(user.getFirstName(), responseEntityBody.getFirstName());
        Assertions.assertEquals(user.getLastName(), responseEntityBody.getLastName());
        Assertions.assertEquals(user.getEmail(), responseEntityBody.getEmail());
    }

    @Test
    void userNull() {
        Mockito.when(idamManagementService.getUserDetails(AUTHORIZATION)).thenReturn(null);
        ResponseEntity<?> responseEntity = idamManagementController.getUserDetails(AUTHORIZATION);
        String responseEntityBody = responseEntity.getBody().toString();
        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        Assertions.assertEquals("User details not found for the given user access token", responseEntityBody);
    }

    @Test
    void userAccessTokenEmpty() {
        ResponseEntity<?> responseEntity = idamManagementController.getUserDetails("");
        String responseEntityBody = responseEntity.getBody().toString();
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        Assertions.assertEquals("Authorization Access Token Is Empty", responseEntityBody);
    }
}
