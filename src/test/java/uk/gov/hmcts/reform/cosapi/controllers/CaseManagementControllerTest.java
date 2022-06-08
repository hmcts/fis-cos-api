package uk.gov.hmcts.reform.cosapi.controllers;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.cosapi.edgecase.model.CaseData;
import uk.gov.hmcts.reform.cosapi.model.CaseResponse;
import uk.gov.hmcts.reform.cosapi.services.CaseManagementService;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class CaseManagementControllerTest {

    private final String caseTestAuth = "testAuth";

    @InjectMocks
    private CaseManagementController caseManagementController;

    @Mock
    CaseManagementService caseManagementService;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

    }

    @Test
    public void testCreateCaseData() {
        CaseData caseData = CaseData.builder()
            .namedApplicant("Applicant1").build();

        Map<String, Object> caseDataMap = new HashMap<>();

        CaseResponse caseResponse = CaseResponse.builder().caseData(caseDataMap).build();

        when (caseManagementService.createCase(caseTestAuth, caseData)).thenReturn(caseResponse);

        ResponseEntity<?> aCase = caseManagementController.createCase(caseTestAuth, caseData);

        CaseResponse testResponse = (CaseResponse) aCase.getBody();

        assertNotNull(testResponse);
        assertEquals(HttpStatus.OK, testResponse.getStatus());
    }

    @Test
    public void testUpdateCaseData() {
        CaseData caseData = CaseData.builder()
            .namedApplicant("Adetola").build();

        Map<String, Object> caseDataMap = new HashMap<>();

        CaseResponse caseResponse = CaseResponse.builder().caseData(caseDataMap).build();

        when (caseManagementService.updateCase(caseTestAuth, caseData, 123L)).thenReturn(caseResponse);

        ResponseEntity<?> uCase =  caseManagementController.updateCase(123L, caseTestAuth, caseData);

        HttpStatus testResponse = uCase.getStatusCode();
        System.out.println(caseResponse.getStatus());
        assertNotNull(testResponse);
        assertEquals(HttpStatus.OK, testResponse);
    }
}

