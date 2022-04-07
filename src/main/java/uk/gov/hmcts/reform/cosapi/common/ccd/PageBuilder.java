package uk.gov.hmcts.reform.cosapi.common.ccd;

import uk.gov.hmcts.ccd.sdk.api.Event.EventBuilder;
import uk.gov.hmcts.ccd.sdk.api.FieldCollection.FieldCollectionBuilder;
import uk.gov.hmcts.ccd.sdk.api.callback.MidEvent;
import uk.gov.hmcts.reform.cosapi.edgecase.model.CaseData;
import uk.gov.hmcts.reform.cosapi.edgecase.model.State;
import uk.gov.hmcts.reform.cosapi.edgecase.model.UserRole;

public class PageBuilder {

    private final EventBuilder<CaseData, UserRole, State> eventBuilder;

    public PageBuilder(final EventBuilder<CaseData, UserRole, State> eventBuilder) {
        this.eventBuilder = eventBuilder;
    }

    public FieldCollectionBuilder<CaseData, State, EventBuilder<CaseData, UserRole, State>> page(final String id) {
        return eventBuilder.fields().page(id);
    }

    public FieldCollectionBuilder<CaseData, State, EventBuilder<CaseData, UserRole, State>> page(
        final String id,
        final MidEvent<CaseData, State> callback) {

        return eventBuilder.fields().page(id, callback);
    }
}
