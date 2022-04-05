package uk.gov.hmcts.reform.demo.fis.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.ccd.sdk.api.CCDConfig;
import uk.gov.hmcts.ccd.sdk.api.CaseDetails;
import uk.gov.hmcts.ccd.sdk.api.ConfigBuilder;
import uk.gov.hmcts.ccd.sdk.api.callback.AboutToStartOrSubmitResponse;
import uk.gov.hmcts.reform.demo.fis.event.model.CaseData;
import uk.gov.hmcts.reform.demo.fis.event.model.State;
import uk.gov.hmcts.reform.demo.fis.event.model.UserRole;

import static uk.gov.hmcts.reform.demo.fis.event.model.Permissions.CREATE_READ_UPDATE;
import static uk.gov.hmcts.reform.demo.fis.event.model.Permissions.READ;
import static uk.gov.hmcts.reform.demo.fis.event.model.State.AwaitingPayment;
import static uk.gov.hmcts.reform.demo.fis.event.model.State.Draft;
import static uk.gov.hmcts.reform.demo.fis.event.model.UserRole.CITIZEN;
import static uk.gov.hmcts.reform.demo.fis.event.model.UserRole.SUPER_USER;

@Slf4j
@Component
public class CitizenSubmitApplication implements CCDConfig<CaseData, State, UserRole> {

    public static final String CITIZEN_SUBMIT = "citizen-submit-application";

    // @Autowired
    // private PaymentService paymentService;

    /*
     * @Autowired
     * private SubmissionService submissionService;
     */

    @Override
    public void configure(final ConfigBuilder<CaseData, State, UserRole> configBuilder) {

        configBuilder
                .event(CITIZEN_SUBMIT)
                .forStates(Draft, AwaitingPayment)
                .name("Applicant Statement of Truth")
                .description("The applicant confirms SOT")
                .retries(120, 120)
                .grant(CREATE_READ_UPDATE, CITIZEN)
                .grant(READ, SUPER_USER)
                .aboutToSubmitCallback(this::aboutToSubmit);
    }

    public AboutToStartOrSubmitResponse<CaseData, State> aboutToSubmit(CaseDetails<CaseData, State> details,
                                                                       CaseDetails<CaseData, State> beforeDetails) {
        log.info("Submit application about to submit callback invoked");

//        CaseData data = details.getData();
//        State state = details.getState();
//
//        log.info("Validating case data");
//        final List<String> validationErrors = validateReadyForPayment(data);
//
//        if (!validationErrors.isEmpty()) {
//            log.info("Validation errors: ");
//            for (String error : validationErrors) {
//                log.info(error);
//            }
//
//            return AboutToStartOrSubmitResponse.<CaseData, State>builder()
//                    .data(data)
//                    .errors(validationErrors)
//                    .state(state)
//                    .build();
//        }
//
//        // TODO uncomment this when there is help with fees flow
//        // Application application = data.getApplication();
//        // if (application.isHelpWithFeesApplication()) {
//        // var submittedDetails = submissionService.submitApplication(details);
//        // data = submittedDetails.getData();
//        // state = submittedDetails.getState();
//        // } else
//        {
//            state = AwaitingPayment;
//        }

        return AboutToStartOrSubmitResponse.<CaseData, State>builder()
                .data(null)
                .state(null)
                .build();
    }

}
