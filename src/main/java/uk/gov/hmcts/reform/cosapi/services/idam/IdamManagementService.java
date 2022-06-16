package uk.gov.hmcts.reform.cosapi.services.idam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.cosapi.idam.IdamService;
import uk.gov.hmcts.reform.cosapi.model.idam.User;
import uk.gov.hmcts.reform.idam.client.models.UserDetails;

@Service
public class IdamManagementService {

    @Autowired
    private IdamService idamService;

    public User getUserDetails(String authorization) {
        uk.gov.hmcts.reform.idam.client.models.User user = idamService.retrieveUser(
            authorization);

        UserDetails userDetails = user.getUserDetails();
        return User.builder()
            .firstName(userDetails.getForename())
            .lastName(userDetails.getSurname().get())
            .emailAddress(userDetails.getEmail())
            .build();
    }

}
