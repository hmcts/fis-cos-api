package uk.gov.hmcts.reform.cosapi.services.cdam;

import org.junit.Before;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.ccd.document.am.feign.CaseDocumentClient;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class CaseDocumentApiServiceTest {

    @InjectMocks
    CaseDocumentApiService caseDocumentApiService;

    @Mock
    CaseDocumentClient caseDocumentClient;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }


}
