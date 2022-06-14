package uk.gov.hmcts.reform.cosapi.edgecase.event.privatelaw;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.ccd.sdk.ConfigBuilderImpl;
import uk.gov.hmcts.ccd.sdk.api.CaseDetails;
import uk.gov.hmcts.ccd.sdk.api.Event;
import uk.gov.hmcts.ccd.sdk.api.callback.AboutToStartOrSubmitResponse;
import uk.gov.hmcts.reform.cosapi.edgecase.model.CaseData;
import uk.gov.hmcts.reform.cosapi.edgecase.model.State;
import uk.gov.hmcts.reform.cosapi.edgecase.model.UserRole;

import static org.assertj.core.api.Assertions.assertThat;

import static uk.gov.hmcts.reform.cosapi.edgecase.event.privatelaw.SubmitCaseEvent.CITIZEN_SUBMIT;
import static uk.gov.hmcts.reform.cosapi.testUtil.ConfigTestUtil.createCaseDataConfigBuilder;
import static uk.gov.hmcts.reform.cosapi.testUtil.ConfigTestUtil.getEventsFrom;
import static uk.gov.hmcts.reform.cosapi.util.TestFileUtil.loadJson;

@ExtendWith(MockitoExtension.class)
public class SubmitCaseEventTest {

    @InjectMocks
    private SubmitCaseEvent submitCaseEvent;

    @Test
    void shouldAddConfigurationToConfigBuilder() {
        final ConfigBuilderImpl<CaseData, State, UserRole> configBuilder = createCaseDataConfigBuilder();

        submitCaseEvent.configure(configBuilder);

        assertThat(getEventsFrom(configBuilder).values())
            .extracting(Event::getId)
            .contains(CITIZEN_SUBMIT);
    }

//    @Test
//    public void givenEventStartedWithEmptyCaseThenGiveValidationErrors() throws Exception {
//        final long caseId = 143L;
//        final CaseDetails<CaseData, State> caseDetails = new CaseDetails<>();
//        final CaseData caseData = CaseData.builder().caseTypeOfApplication("C100").namedApplicant("ApplicationTestApplicant").build();
//        caseData.getApplicant().setEmailAddress("onlineApplicant2@email.com");
//        caseDetails.setData(caseData);
//        caseDetails.setId(caseId);
//
//        final AboutToStartOrSubmitResponse<CaseData, State> response = submitCaseEvent.aboutToSubmit(
//            caseDetails,
//            caseDetails
//        );
//
//        //assertThat(response.getErrors().size()).isEqualTo(14);
//        assertThat(response.getErrors()).contains("Applicant1FirstName cannot be empty or null");
//        assertThat(response.getErrors()).contains("ApplicationType cannot be empty or null");
//
//        //assertThat(response.getData()).contains("Applicant1FirstName cannot be empty or null");
//    }
}
