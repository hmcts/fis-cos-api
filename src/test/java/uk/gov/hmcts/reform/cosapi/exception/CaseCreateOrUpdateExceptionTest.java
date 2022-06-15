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
import static uk.gov.hmcts.reform.cosapi.util.TestFileUtil.loadJson;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@SuppressWarnings("PMD")
public class CaseCreateOrUpdateExceptionTest {
    private final String caseTestAuth = "testAuth";
    private ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @InjectMocks
    private CaseManagementController caseManagementController;

    @Mock
    CaseManagementService caseManagementService;


    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateCaseDataCaseCreateUpdateException() throws Exception {
        String caseDataJson = loadJson("C100CaseData.json");
        CaseData caseData = mapper.readValue(caseDataJson, CaseData.class);

        when(caseManagementService.createCase(caseTestAuth, caseData))
            .thenThrow(new CaseCreateOrUpdateException("Failing while creating the case", new Throwable()));

        Exception exception = assertThrows(Exception.class, () -> {
            caseManagementController.createCase(caseTestAuth, caseData);
        });
        assertTrue(exception.getMessage().contains("Failing while creating the case"));
    }

    @Test
    public void testUpdateCaseDataCaseCreateUpdateException() throws Exception {
        String caseDataJson = loadJson("C100CaseData.json");
        CaseData caseData = mapper.readValue(caseDataJson, CaseData.class);

        when(caseManagementService.createCase(caseTestAuth, caseData))
            .thenThrow(new CaseCreateOrUpdateException("Failing while updating the case", new Throwable()));

        Exception exception = assertThrows(Exception.class, () -> {
            caseManagementController.createCase(caseTestAuth, caseData);
        });
        assertTrue(exception.getMessage().contains("Failing while updating the case"));
    }
}
