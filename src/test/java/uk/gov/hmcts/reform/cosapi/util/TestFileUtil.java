package uk.gov.hmcts.reform.cosapi.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

@SuppressWarnings("PMD")
public class TestFileUtil {

    public static String loadJson(final String filePath) throws Exception {
        return new String(loadResource(filePath), Charset.forName("utf-8"));
    }

    public static byte[] loadResource(final String filePath) throws Exception {
        URL url = TestFileUtil.class.getClassLoader().getResource(filePath);

        if (url == null) {
            throw new IllegalArgumentException(String.format("Could not find resource in path %s", filePath));
        }

        return Files.readAllBytes(Paths.get(url.toURI()));
    }

    public static <T> T loadJsonToObject(String filePath, Class<T> type) {
        try {
            return new ObjectMapper().readValue(loadJson(filePath), type);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> String objectToJson(T object) {

        try {

            return new ObjectMapper().writeValueAsString(object);

        } catch (Exception e) {

            throw new RuntimeException(e);

        }

    }

}
