package org.oaky.cuke4duke;

class Check {
    
    public static boolean hasText(String text) {
        return text != null && text.trim().length() > 0;
    }

    public static void assertNotNull(Object arg, String message) {
        if (arg == null) {
            throw new IllegalArgumentException(message);
        }
    }
}
