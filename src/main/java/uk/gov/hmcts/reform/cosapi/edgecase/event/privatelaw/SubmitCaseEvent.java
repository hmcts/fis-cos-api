package uk.gov.hmcts.reform.cosapi.edgecase.event.privatelaw;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.ccd.sdk.api.CCDConfig;
import uk.gov.hmcts.ccd.sdk.api.CaseDetails;
import uk.gov.hmcts.ccd.sdk.api.ConfigBuilder;
import uk.gov.hmcts.ccd.sdk.api.callback.AboutToStartOrSubmitResponse;
import uk.gov.hmcts.reform.cosapi.edgecase.model.CaseData;
import uk.gov.hmcts.reform.cosapi.edgecase.model.State;
import uk.gov.hmcts.reform.cosapi.edgecase.model.UserRole;

import static uk.gov.hmcts.reform.cosapi.edgecase.model.UserRole.CITIZEN;
import static uk.gov.hmcts.reform.cosapi.edgecase.model.UserRole.SUPER_USER;
import static uk.gov.hmcts.reform.cosapi.edgecase.model.access.Permissions.CREATE_READ_UPDATE;
import static uk.gov.hmcts.reform.cosapi.edgecase.model.access.Permissions.READ;

@Component
@Slf4j
public class SubmitCaseEvent implements CCDConfig<CaseData, State, UserRole> {

    public static final String CITIZEN_SUBMIT = "citizen-submit-event";

    @Override
    public void configure(final ConfigBuilder<CaseData, State, UserRole> configBuilder) {

        configBuilder
            .event(CITIZEN_SUBMIT)
            .forStates(State.DRAFT)
            .name("Applicant Submitting the case")
            .description("The applicant confirms SOT")
            .retries(120, 120)
            .grant(CREATE_READ_UPDATE, CITIZEN)
            .grant(READ, SUPER_USER)
            .aboutToSubmitCallback(this::aboutToSubmit);
    }

    public AboutToStartOrSubmitResponse<CaseData, State> aboutToSubmit(CaseDetails<CaseData, State> details,
                                                                       CaseDetails<CaseData, State> beforeDetails) {

        //TODO logic needs to be updated separately as per edge-case application requirement
        return AboutToStartOrSubmitResponse.<CaseData, State>builder()
            .data(details.getData())
            .state(details.getState())
            .build();
    }

}
