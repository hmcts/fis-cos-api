package uk.gov.hmcts.reform.cosapi.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.authorisation.validators.AuthTokenValidator;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class S2sAuthService {

    private final AuthTokenValidator authTokenValidator;

    private final AuthTokenGenerator authTokenGenerator;

    public void tokenValidator(String token) {
        authTokenValidator.validate(token);
    }

    public String serviceAuthTokenGenerator() {
        return authTokenGenerator.generate();
    }
}
