package uk.co.stringerj.passwordmanager.mfa;

import java.util.ArrayList;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

/** An auth token consisting of a username, password and TOTP */
public class TwoFactorAuthToken extends AbstractAuthenticationToken {
  private static final long serialVersionUID = 7208190188895406673L;

  private final String username;
  private final Credentials credentials;

  public TwoFactorAuthToken(String username, String password, String code) {
    super(new ArrayList<GrantedAuthority>());
    this.username = username;
    this.credentials = new Credentials(password, code);
  }

  @Override
  public Object getCredentials() {
    return credentials;
  }

  @Override
  public Object getPrincipal() {
    return username;
  }

  public static class Credentials {
    private final String password;
    private final String code;

    public Credentials(String password, String code) {
      this.password = password;
      this.code = code;
    }

    public String getPassword() {
      return password;
    }

    public String getCode() {
      return code;
    }
  }
}
