package uk.gov.hmcts.reform.cosapi.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.hmcts.reform.cosapi.common.config.AppsConfig;
import uk.gov.hmcts.reform.cosapi.exception.DocumentUploadOrDeleteException;
import uk.gov.hmcts.reform.cosapi.model.DocumentInfo;
import uk.gov.hmcts.reform.cosapi.model.DocumentResponse;
import uk.gov.hmcts.reform.cosapi.services.cdam.CaseDocumentApiService;
import uk.gov.hmcts.reform.cosapi.util.AppsUtil;

@Service
@Slf4j
public class DocumentManagementService {

    @Autowired
    CaseDocumentApiService caseDocumentApiService;

    @Autowired
    AppsConfig appsConfig;

    public DocumentResponse uploadDocument(String authorization, String caseTypeOfApplication, MultipartFile file) {
        try {
            DocumentInfo document = caseDocumentApiService.uploadDocument(authorization, file, AppsUtil
                .getExactAppsDetails(appsConfig, caseTypeOfApplication));
            log.info("Stored Doc Detail: " + document.toString());
            return DocumentResponse.builder().status("Success").document(document).build();

        } catch (Exception e) {
            log.error("Error while uploading document ." + e.getMessage());
            throw new DocumentUploadOrDeleteException("Failing while uploading the document. The error message is "
                                                           + e.getMessage(), e);
        }
    }

    public DocumentResponse deleteDocument(String authorization, String documentId) {
        try {
            caseDocumentApiService.deleteDocument(authorization, documentId);
            log.info("document deleted successfully..");
            return DocumentResponse.builder().status("Success").build();

        } catch (Exception e) {
            log.error("Error while deleting  document ." + e.getMessage());
            throw new DocumentUploadOrDeleteException("Failing while deleting the document. The error message is "
                                                          + e.getMessage(), e);
        }
    }

    public DocumentResponse uploadDocumentForDssUpdate(String authorization, 
            String caseTypeId, String jurisdiction, MultipartFile file) {
        try {
            DocumentInfo document = caseDocumentApiService.uploadDocumentForDssUpdate(authorization, file,
                 caseTypeId, jurisdiction);
            log.info("Stored Doc Detail: " + document.toString());
            return DocumentResponse.builder().status("Success").document(document).build();

        } catch (Exception e) {
            log.error("Error while uploading document ." + e.getMessage());
            throw new DocumentUploadOrDeleteException("Failing while uploading the document. The error message is "
                                                           + e.getMessage(), e);
        }
    }
}
