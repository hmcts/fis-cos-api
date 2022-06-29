package uk.gov.hmcts.reform.cosapi.services.idam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.cosapi.idam.IdamService;
import uk.gov.hmcts.reform.cosapi.model.idam.IdamUser;

import java.util.Optional;

@Service
public class IdamManagementService {

    @Autowired
    private IdamService idamService;

    public IdamUser getUserDetails(String authorization) {
        Optional<uk.gov.hmcts.reform.idam.client.models.User> userOptional =
            Optional.ofNullable(idamService.retrieveUser(authorization));

        Optional<uk.gov.hmcts.reform.idam.client.models.UserDetails> userDetailsOptional = Optional.empty();

        if (userOptional.isPresent()) {
            userDetailsOptional = Optional.ofNullable(userOptional.get().getUserDetails());
        }

        if (userOptional.isPresent() && userDetailsOptional.isPresent()) {
            return IdamUser.builder()
                .firstName(userDetailsOptional.get().getForename())
                .lastName(userDetailsOptional.get().getSurname().get())
                .email(userDetailsOptional.get().getEmail())
                .build();
        } else {
            return null;
        }
    }

}
