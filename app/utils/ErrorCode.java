package utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Created by ShenTeng on 2014/12/11.
 */
public class ErrorCode {
    private static Properties properties;

    static {
        properties = new Properties();

        try (InputStreamReader isr = new InputStreamReader(ErrorCode.class.getClassLoader().getResourceAsStream("errorcode.properties"), "utf-8")) {
            properties.load(isr);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getErrorMsg(String errorKey) {
        return properties.getProperty(errorKey).split(":")[1];
    }

    public static String getErrorMsg(String errorKey, String extMsg) {
        return properties.getProperty(errorKey).split(":")[1] + "." + extMsg;
    }

    public static String getErrorCode(String errorKey) {
        return properties.getProperty(errorKey).split(":")[0];
    }

}
