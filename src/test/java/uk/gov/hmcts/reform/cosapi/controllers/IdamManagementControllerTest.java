package uk.gov.hmcts.reform.cosapi.controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.cosapi.model.idam.User;
import uk.gov.hmcts.reform.cosapi.services.idam.IdamManagementService;

@SpringBootTest
class IdamManagementControllerTest {

    @Mock
    private IdamManagementService idamManagementService;

    @InjectMocks
    private IdamManagementController idamManagementController = new IdamManagementController();

    @Test
    void getUserDetails() {
        User user = User.builder()
            .firstName("hmcts")
            .lastName("gov")
            .email("gov@htcts.net")
            .build();
        Mockito.when(idamManagementService.getUserDetails("Authorization")).thenReturn(user);
        ResponseEntity<?> responseEntity = idamManagementController.getUserDetails("Authorization");
        User responseEntityBody = (User) responseEntity.getBody();
        Assertions.assertEquals(user.getFirstName(), responseEntityBody.getFirstName());
        Assertions.assertEquals(user.getLastName(), responseEntityBody.getLastName());
        Assertions.assertEquals(user.getEmail(), responseEntityBody.getEmail());
    }

    @Test
    void userNull() {
        Mockito.when(idamManagementService.getUserDetails("Authorization")).thenReturn(null);
        ResponseEntity<?> responseEntity = idamManagementController.getUserDetails("Authorization");
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
