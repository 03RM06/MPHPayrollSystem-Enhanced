package Utility;
 
import java.math.BigDecimal;
import java.text.DecimalFormat;
 
/** Static helpers for formatting display values. */
public final class StringUtils {
 
    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("#,##0.00");
 
    private StringUtils() {}
 
    public static String formatCurrency(BigDecimal amount) {
        return (amount == null) ? "0.00" : CURRENCY_FORMAT.format(amount);
    }
 
    public static String formatCurrency(double amount) {
        return CURRENCY_FORMAT.format(amount);
    }
 
    public static String nullToEmpty(String str) {
        return str == null ? "" : str;
    }
}
