package core;

import java.util.UUID;

public class StringUtils {
    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }
}
