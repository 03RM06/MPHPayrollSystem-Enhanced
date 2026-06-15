package Utility;
 
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
 
/**
 * Minimal BCrypt-compatible stub (salted SHA-256 under the hood).
 * NOTE: This is NOT real BCrypt. It exists only if you previously stored
 * stub hashes. For new accounts, use UserAccountDAO.hashPassword() instead.
 *
 * @deprecated Use UserAccountDAO.hashPassword() and checkPassword() directly.
 */
@Deprecated
public final class BCrypt {
 
    private static final SecureRandom RNG    = new SecureRandom();
    private static final String       PREFIX = "$stub$";
 
    private BCrypt() {}
 
    public static String gensalt(int logRounds) {
        byte[] salt = new byte[16];
        RNG.nextBytes(salt);
        return PREFIX + logRounds + "$" + Base64.getEncoder().encodeToString(salt);
    }
 
    public static String gensalt() { return gensalt(10); }
 
    public static String hashpw(String password, String salt) {
        if (password == null) password = "";
        if (salt     == null) salt     = gensalt();
        try {
            ParsedSalt ps = ParsedSalt.parse(salt);
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(ps.saltBytes);
            md.update(password.getBytes(StandardCharsets.UTF_8));
            byte[] digest = md.digest();
            return PREFIX + ps.logRounds + "$"
                 + Base64.getEncoder().encodeToString(ps.saltBytes) + "$"
                 + Base64.getEncoder().encodeToString(digest);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid salt", ex);
        }
    }
 
    public static boolean checkpw(String plaintext, String hashed) {
        if (hashed == null || !hashed.startsWith(PREFIX)) return false;
        ParsedHash ph = ParsedHash.parse(hashed);
        String recomputed = hashpw(plaintext,
                PREFIX + ph.logRounds + "$" + Base64.getEncoder().encodeToString(ph.saltBytes));
        return constantTimeEquals(recomputed, hashed);
    }
 
    private static boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null) return false;
        byte[] x = a.getBytes(StandardCharsets.UTF_8);
        byte[] y = b.getBytes(StandardCharsets.UTF_8);
        int diff = x.length ^ y.length;
        for (int i = 0; i < Math.min(x.length, y.length); i++) diff |= x[i] ^ y[i];
        return diff == 0;
    }
 
    private record ParsedSalt(int logRounds, byte[] saltBytes) {
        static ParsedSalt parse(String salt) {
            String[] p = salt.split("\\$");
            if (p.length < 4 || !"stub".equals(p[1]))
                throw new IllegalArgumentException("Bad salt: " + salt);
            return new ParsedSalt(Integer.parseInt(p[2]), Base64.getDecoder().decode(p[3]));
        }
    }
 
    private record ParsedHash(int logRounds, byte[] saltBytes) {
        static ParsedHash parse(String hashed) {
            String[] p = hashed.split("\\$");
            if (p.length < 5 || !"stub".equals(p[1]))
                throw new IllegalArgumentException("Bad hash: " + hashed);
            return new ParsedHash(Integer.parseInt(p[2]), Base64.getDecoder().decode(p[3]));
        }
    }
}
