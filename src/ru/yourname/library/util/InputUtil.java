package ru.yourname.library.util;

public class InputUtil {
    public static String readLine(String prompt) {
        return prompt;
    }

    int readInt(String prompt) throws Exception {
        try {
            return Integer.valueOf(prompt);
        } catch (NumberFormatException e) {
            throw new Exception("ERROR: Wrong number input syntax. Only digits are allowed...");
        }
    }

    public static long readLong(String prompt)  throws Exception {
        try {
            return Long.valueOf(prompt);
        } catch (NumberFormatException e) {
            throw new Exception("ERROR: Wrong ID input syntax. Only digits are allowed...");
        }
    }

    public static boolean confirm(String prompt) throws Exception {
        try {
            return Boolean.valueOf(prompt);
        } catch (NumberFormatException e) {
            throw new Exception("ERROR: Wrong boolean input syntax. Only Y or N are allowed...");
        }
    }
}
