package uk.gov.hmcts.reform.cosapi.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.cosapi.controllers.CaseManagementController;
import uk.gov.hmcts.reform.cosapi.edgecase.model.CaseData;
import uk.gov.hmcts.reform.cosapi.services.CaseManagementService;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.cosapi.util.TestConstant.CASE_DATA_FILE_C100;
import static uk.gov.hmcts.reform.cosapi.util.TestFileUtil.loadJson;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class CaseCreateOrUpdateExceptionTest {
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @InjectMocks
    private CaseManagementController caseManagementController;

    @Mock
    private CaseManagementService caseManagementService;

    @Before
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateCaseDataCaseCreateUpdateException() throws Exception {
        String createCaseTestAuth = "testAuth";
        String caseDataJson = loadJson(CASE_DATA_FILE_C100);
        CaseData caseData = mapper.readValue(caseDataJson, CaseData.class);

        when(caseManagementService.createCase(createCaseTestAuth, caseData))
            .thenThrow(new CaseCreateOrUpdateException("Failing while creating the case", new Throwable()));

        Exception exception = assertThrows(Exception.class, () -> {
            caseManagementController.createCase(createCaseTestAuth, caseData);
        });

        assertTrue(exception.getMessage().contains("Failing while creating the case"), String.valueOf(true));
    }

    @Test
    void testUpdateCaseDataCaseCreateUpdateException() throws Exception {
        String updateCaseTestAuth = "testAuth";
        String caseDataJson = loadJson(CASE_DATA_FILE_C100);
        CaseData caseData = mapper.readValue(caseDataJson, CaseData.class);

        when(caseManagementService.createCase(updateCaseTestAuth, caseData))
            .thenThrow(new CaseCreateOrUpdateException("Failing while updating the case", new Throwable()));

        Exception exception = assertThrows(Exception.class, () -> {
            caseManagementController.createCase(updateCaseTestAuth, caseData);
        });

        assertTrue(exception.getMessage().contains("Failing while updating the case"), String.valueOf(true));
    }
}
