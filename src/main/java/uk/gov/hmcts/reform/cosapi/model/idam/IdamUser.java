package uk.gov.hmcts.reform.cosapi.model.idam;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IdamUser {
    private String firstName;
    private String lastName;
    private String email;
}
