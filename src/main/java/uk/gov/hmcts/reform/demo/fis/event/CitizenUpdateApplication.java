package uk.gov.hmcts.reform.demo.fis.event;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.ccd.sdk.api.CCDConfig;
import uk.gov.hmcts.ccd.sdk.api.ConfigBuilder;
import uk.gov.hmcts.reform.demo.fis.event.model.CaseData;
import uk.gov.hmcts.reform.demo.fis.event.model.State;
import uk.gov.hmcts.reform.demo.fis.event.model.UserRole;

import static uk.gov.hmcts.reform.demo.fis.event.model.Permissions.CREATE_READ_UPDATE;
import static uk.gov.hmcts.reform.demo.fis.event.model.Permissions.READ;
import static uk.gov.hmcts.reform.demo.fis.event.model.State.Draft;
import static uk.gov.hmcts.reform.demo.fis.event.model.State.Submitted;
import static uk.gov.hmcts.reform.demo.fis.event.model.UserRole.CITIZEN;
import static uk.gov.hmcts.reform.demo.fis.event.model.UserRole.CREATOR;
import static uk.gov.hmcts.reform.demo.fis.event.model.UserRole.SUPER_USER;

@Component
public class CitizenUpdateApplication implements CCDConfig<CaseData, State, UserRole> {

    public static final String CITIZEN_UPDATE = "citizen-update-application";

    @Override
    public void configure(final ConfigBuilder<CaseData, State, UserRole> configBuilder) {

        configBuilder
            .event(CITIZEN_UPDATE)
            .forStates(Draft, Submitted)
            .name("Edge case")
            .description("Edge Case application update")
            .retries(120, 120)
            .grant(CREATE_READ_UPDATE, CITIZEN)
            .grant(CREATE_READ_UPDATE, CREATOR)
            .grant(READ, SUPER_USER);
    }
}
