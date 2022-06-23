package uk.gov.hmcts.reform.cosapi.common;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.cosapi.edgecase.model.UserRole;

import java.util.ArrayList;
import java.util.List;

@Component
public class AddSystemUpdateRole {
    private static final String ENVIRONMENT_AAT = "aat";

    public List<UserRole> addIfConfiguredForEnvironment(List<UserRole> userRoles) {
        List<UserRole> existingRoles = new ArrayList<>(userRoles);
        String environment = System.getenv().getOrDefault("ENVIRONMENT", null);

        return existingRoles;
    }

    public boolean isEnvironmentAat() {
        String environment = System.getenv().getOrDefault("ENVIRONMENT", null);
        return null != environment && environment.equalsIgnoreCase(ENVIRONMENT_AAT);
    }
}
