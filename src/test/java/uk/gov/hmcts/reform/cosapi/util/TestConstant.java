package uk.gov.hmcts.reform.cosapi.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public final class TestConstant {

    public static final String ENGLISH_TEMPLATE_ID = "edgecaseapplication";
    public static final String WELSH_TEMPLATE_ID = "EDG-C100-WEL-00001.docx";
    public static final String CASE_DATA_FILE_C100 = "C100CaseData.json";
    public static final String CASE_TEST_AUTHORIZATION = "testAuth";
    public static final String CASE_DATA_C100_ID = "C100CaseData";

    private TestConstant() {

    }
}
