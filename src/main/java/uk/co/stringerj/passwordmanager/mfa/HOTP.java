package uk.co.stringerj.passwordmanager.mfa;

import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Calculates Hmac One Time Passwords, only currently supports 6 digit HMAC-SHA1 OTPs since that's
 * what google authenticator uses.
 */
public class HOTP {
  private HOTP() {}

  public static String calculate(byte[] secret, long counter) {
    return toCodeString(hash(secret, longToBytes(counter)));
  }

  private static byte[] longToBytes(long counter) {
    return ByteBuffer.allocate(8).putLong(counter).array();
  }

  private static byte[] hash(byte[] key, byte[] value) {
    try {
      Mac mac = Mac.getInstance("HmacSHA1");
      mac.init(new SecretKeySpec(key, "RAW"));
      return mac.doFinal(value);
    } catch (InvalidKeyException | NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }

  private static String toCodeString(byte[] hash) {
    int offset = hash[hash.length - 1] & 0xf;
    int code =
        ((hash[offset] & 0x7f) << 24)
            | ((hash[offset + 1] & 0xff) << 16)
            | ((hash[offset + 2] & 0xff) << 8)
            | (hash[offset + 3] & 0xff);
    StringBuilder codeString = new StringBuilder(String.valueOf(code % 1000000));
    while (codeString.length() < 6) codeString.insert(0, "0");
    return codeString.toString();
  }
}
