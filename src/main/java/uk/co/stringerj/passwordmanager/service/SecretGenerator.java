package uk.co.stringerj.passwordmanager.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SecretGenerator {

  @Autowired private RNG rng;

  public String generateSecret(Encoding encoding, int length) {
    String secret = "";
    while (!isValid(secret = doGenerateSecret(encoding, length), encoding)) ;
    return secret;
  }

  private String doGenerateSecret(Encoding encoding, int length) {
    String charSet = encoding.getFullCharSet();
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < length; i++) {
      sb.append(charSet.charAt(rng.generateInt(charSet.length())));
    }
    return sb.toString();
  }

  private boolean isValid(String password, Encoding encoding) {
    for (String charSet : encoding.charSet) {
      if (!containsOneOf(password, charSet)) {
        return false;
      }
    }
    return true;
  }

  private boolean containsOneOf(String password, String charSet) {
    for (char c : charSet.toCharArray()) {
      for (char d : password.toCharArray()) {
        if (c == d) {
          return true;
        }
      }
    }
    return false;
  }

  public enum Encoding {
    BASE32_SECRET("ABCDEFGHIJKLMNOPQRSTUVWXYZ234567"),
    PASSWORD("abcdefghijklmnopqrstuvwxyz", "ABCDEFGHIJKLMNOPQRSTUVWXYZ", "0123456789", "!$%");
    private final List<String> charSet;

    private Encoding(String... charSet) {
      this.charSet = Arrays.asList(charSet);
    }

    public String getFullCharSet() {
      StringBuilder sb = new StringBuilder();
      charSet.forEach(sb::append);
      return sb.toString();
    }
  }
}
