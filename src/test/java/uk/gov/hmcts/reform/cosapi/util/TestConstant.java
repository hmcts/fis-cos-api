package uk.gov.hmcts.reform.cosapi.util;

import java.time.LocalDateTime;
import java.util.UUID;

public final class TestConstant {

    public static final String ENGLISH_TEMPLATE_ID = "edgecaseapplication";
    public static final String WELSH_TEMPLATE_ID = "EDG-C100-WEL-00001.docx";
    public static final String CASE_DATA_FILE_C100 = "C100CaseData.json";
    public static final String CASE_TEST_AUTHORIZATION = "testAuth";
    public static final String CASE_DATA_C100_ID = "C100";
    public static final String JSON_FILE_TYPE = "json";
    public static final String JSON_CONTENT_TYPE = "application/json";
    public static final String RESPONSE_STATUS_SUCCESS = "Success";
    public static final String TEST_URL = "TestUrl";
    public static final String TEST_USER = "TestUser";
    public static final String TEST_AUTHORIZATION_TOKEN = "TestToken";
    public static final UUID TEST_CASE_DATA_FILE_UUID = UUID.randomUUID();
    public static final LocalDateTime LOCAL_DATE_TIME = LocalDateTime.of(2022, 2, 22, 16, 21);

    private TestConstant() {

    }
}
