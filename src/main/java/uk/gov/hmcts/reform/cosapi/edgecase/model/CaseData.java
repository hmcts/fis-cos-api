package uk.gov.hmcts.reform.cosapi.edgecase.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.ccd.sdk.type.ListValue;
import uk.gov.hmcts.reform.cosapi.common.MappableObject;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class CaseData implements MappableObject {

    private String caseTypeOfApplication;

    private List<ListValue<DssUploadedDocument>> uploadedDssDocuments;

}
