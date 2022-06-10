package uk.gov.hmcts.reform.cosapi.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.gov.hmcts.reform.cosapi.util.TestFileUtil.loadResource;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InvalidResourceExceptionTest {
    @Test
    void testInvalidResourceExceptionThrownForNonExistentFile() throws Exception {
        String createCaseDataFileNotExist = "C100CaseDataNotExist.json";

        Exception exception = assertThrows(Exception.class, () -> {
            byte [] caseDataJson = loadResource(createCaseDataFileNotExist);
            assertNull(caseDataJson);
        });

        assertTrue(exception.getMessage().contains("Could not find resource in path"), String.valueOf(true));

    }
}