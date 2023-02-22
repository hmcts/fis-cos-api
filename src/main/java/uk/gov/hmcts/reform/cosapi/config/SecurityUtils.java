package uk.gov.hmcts.reform.cosapi.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.cosapi.idam.IdamService;

@Component
public class SecurityUtils {

    public static final String BEARER = "Bearer ";
    public static final String SERVICE_AUTHORIZATION = "ServiceAuthorization";

    private final AuthTokenGenerator authTokenGenerator;
    private final IdamService idamRepository;

    @Autowired
    public SecurityUtils(AuthTokenGenerator authTokenGenerator, IdamService idamRepository) {
        this.authTokenGenerator = authTokenGenerator;
        this.idamRepository = idamRepository;
    }

    public HttpHeaders serviceAuthorizationHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(SERVICE_AUTHORIZATION, authTokenGenerator.generate());
        return headers;
    }

    public String getUserBearerToken() {
        return BEARER + idamRepository.retrieveSystemUpdateUserDetails().getAuthToken();
    }

}
