package uk.co.stringerj.passwordmanager.mfa;

import java.time.Clock;
import java.time.Duration;

import org.apache.commons.codec.binary.Base32;

/**
 * Calculates Time-Based One Time Passwords, currently only supports 6 digit HMAC-SHA1 TOTP with a
 * period of 30 seconds as thats what google authenticator uses.
 */
public class TOTP {
  private TOTP() {}

  public static TOTPCode calculate(String base32Secret) {
    return calculate(new Base32().decode(base32Secret));
  }

  public static TOTPCode calculate(byte[] secret) {
    return calculate(secret, Clock.systemUTC(), Duration.ofSeconds(30));
  }

  public static TOTPCode calculate(byte[] secret, Clock clock, Duration tick) {
    long counter = calculateCounter(clock, tick);
    return new TOTPCode(
        HOTP.calculate(secret, counter - 1),
        HOTP.calculate(secret, counter),
        HOTP.calculate(secret, counter + 1));
  }

  private static long calculateCounter(Clock clock, Duration tick) {
    return Math.floorDiv(clock.instant().toEpochMilli(), tick.toMillis());
  }

  public static class TOTPCode {
    private final String previous;
    private final String current;
    private final String next;

    public TOTPCode(String previous, String current, String next) {
      this.previous = previous;
      this.current = current;
      this.next = next;
    }

    public String getPrevious() {
      return previous;
    }

    public String getCurrent() {
      return current;
    }

    public String getNext() {
      return next;
    }

    public boolean matches(String code) {
      return previous.equals(code) || current.equals(code) || next.equals(code);
    }
  }
}
