package Utility;
 
/**
 * Input validation helpers.
 * Applies Encapsulation: private constructor prevents instantiation.
 * Applies Abstraction: regex patterns are internal constants.
 */
public final class Validator {
 
    private static final String PHILHEALTH_PAGIBIG_PATTERN = "^\\d{12}$";
    private static final String PHONE_PATTERN              = "^\\s*\\d{3}-\\d{3}-\\d{4}\\s*$";
    private static final String SSS_PATTERN                = "^\\d{2}-\\d{7}-\\d{1}$";
    private static final String TIN_PATTERN                = "^\\d{3}-\\d{3}-\\d{3}-\\d{3}$";
 
    private Validator() {}
 
    /** True if the string is non-null, non-blank, and alphanumeric. */
    public static boolean isValidString(String input) {
        return input != null && !input.isBlank() && input.matches("^[a-zA-Z0-9 ]+$");
    }
 
    /** Accepts any non-null, non-blank address (no CSV restriction). */
    public static boolean isValidAddress(String input) {
        return input != null && !input.isBlank();
    }
 
    public static boolean isValidNumber(String value) {
        if (value == null) return false;
        try { Double.parseDouble(value); return true; }
        catch (NumberFormatException e) { return false; }
    }
 
    public static boolean isValidPhilhealthNumber(String v) {
        return v != null && v.matches(PHILHEALTH_PAGIBIG_PATTERN);
    }
 
    public static boolean isValidPagibigNumber(String v) {
        return v != null && v.matches(PHILHEALTH_PAGIBIG_PATTERN);
    }
 
    public static boolean isValidPhoneNumber(String v) {
        return v != null && v.matches(PHONE_PATTERN);
    }
 
    public static boolean isValidSssNumber(String v) {
        return v != null && v.matches(SSS_PATTERN);
    }
 
    public static boolean isValidTin(String v) {
        return v != null && v.matches(TIN_PATTERN);
    }
}
