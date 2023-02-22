package uk.gov.hmcts.reform.cosapi.edgecase.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)
@Builder
public class NewApplicant {

    private String firstName;

    
    private String lastName;

    private LocalDate dateOfBirth;

    private String emailAddress;

    private String phoneNumber;

    private String homeNumber;

    private String address1;

    private String address2;

    private String addressTown;

    private String addressCountry;

    private String addressPostCode;

}
