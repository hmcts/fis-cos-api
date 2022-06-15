package uk.gov.hmcts.reform.cosapi.services.idam;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.cosapi.idam.IdamService;
import uk.gov.hmcts.reform.cosapi.model.idam.User;
import uk.gov.hmcts.reform.idam.client.models.UserDetails;

@NoArgsConstructor
@AllArgsConstructor
@Service
public class IdamManagementService {

    @Autowired
    private IdamService idamService;

    public User getUserDetails(String authorization) {
        UserDetails userDetails = idamService.retrieveUser(authorization).getUserDetails();
        return User.builder()
            .firstName(userDetails.getForename())
            .lastName(userDetails.getSurname().get())
            .emailAddress(userDetails.getEmail())
            .build();
    }

}
