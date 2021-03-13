package uk.co.stringerj.passwordmanager.mfa;

import java.util.ArrayList;
import java.util.Arrays;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

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

  public TwoFactorAuthToken(String username, String role) {
    super(Arrays.asList(new SimpleGrantedAuthority("ROLE_" + role)));
    this.username = username;
    this.credentials = new Credentials("", "");
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
