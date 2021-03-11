package uk.co.stringerj.passwordmanager.mfa;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Auth filter which looks for form data consisting of a username, password and a 6 digit HMAC-SHA1
 * TOTP and delegates to a {@link TwoFactorAuthManager} to validate the data.
 */
public class TwoFactorAuthFilter extends AbstractAuthenticationProcessingFilter {

  private static final String PARAM_USERNAME = "username";
  private static final String PARAM_PASSWORD = "password";
  private static final String PARAM_CODE = "code";

  public TwoFactorAuthFilter() {
    super(new AntPathRequestMatcher("/login", "POST"));
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse response)
      throws AuthenticationException, IOException, ServletException {
    return getAuthenticationManager()
        .authenticate(
            new TwoFactorAuthToken(
                getParam(req, PARAM_USERNAME),
                getParam(req, PARAM_PASSWORD),
                getParam(req, PARAM_CODE)));
  }

  private String getParam(HttpServletRequest req, String param) {
    return Optional.ofNullable(req.getParameter(param)).map(String::trim).orElse("");
  }
}
