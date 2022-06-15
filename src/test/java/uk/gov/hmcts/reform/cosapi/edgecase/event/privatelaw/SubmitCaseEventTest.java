package uk.gov.hmcts.reform.cosapi.edgecase.event.privatelaw;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.ccd.sdk.ConfigBuilderImpl;
import uk.gov.hmcts.ccd.sdk.api.Event;
import uk.gov.hmcts.reform.cosapi.edgecase.model.CaseData;
import uk.gov.hmcts.reform.cosapi.edgecase.model.State;
import uk.gov.hmcts.reform.cosapi.edgecase.model.UserRole;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.reform.cosapi.constants.CommonConstants.SUBMIT_CASE_EVENT_ID;
import static uk.gov.hmcts.reform.cosapi.util.ConfigTestUtil.createCaseDataConfigBuilder;
import static uk.gov.hmcts.reform.cosapi.util.ConfigTestUtil.getEventsFrom;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("PMD")
public class SubmitCaseEventTest {

    @InjectMocks
    private SubmitCaseEvent submitCaseEvent;

    @Test
    void shouldAddConfigurationToConfigBuilder() throws Exception {
        final ConfigBuilderImpl<CaseData, State, UserRole> configBuilder = createCaseDataConfigBuilder();

        submitCaseEvent.configure(configBuilder);

        assertThat(getEventsFrom(configBuilder).values())
            .extracting(Event::getId)
            .contains(SUBMIT_CASE_EVENT_ID);
    }
}
