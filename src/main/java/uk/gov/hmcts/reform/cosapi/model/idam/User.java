package uk.gov.hmcts.reform.cosapi.model.idam;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {

    private String firstName;
    private String lastName;
    private String emailAddress;
}
