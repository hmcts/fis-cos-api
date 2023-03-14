package uk.gov.hmcts.reform.cosapi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.ccd.sdk.type.ListValue;

import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class DssCaseRequest {
    private String dssAdditionalCaseInformation;
    private String dssCaseUpdatedBy;
    private List<ListValue<DssDocumentInfo>> dssDocumentInfoList;
}
