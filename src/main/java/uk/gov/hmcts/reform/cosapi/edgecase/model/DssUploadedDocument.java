package uk.gov.hmcts.reform.cosapi.edgecase.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.ccd.sdk.type.ListValue;
import uk.gov.hmcts.reform.cosapi.model.DssDocumentInfo;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DssUploadedDocument {
    private List<ListValue<DssDocumentInfo>> dssDocuments;

    private String dssAdditionalCaseInformation;

    private String dssCaseUpdatedBy;
    
}
