package uk.co.stringerj.passwordmanager.mfa;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import uk.co.stringerj.passwordmanager.dao.UserDao;
import uk.co.stringerj.passwordmanager.dao.model.User;
import uk.co.stringerj.passwordmanager.mfa.TwoFactorAuthToken.Credentials;

/** Validates {@link TwoFactorAuthToken}'s against details stored in the DB. */
public class TwoFactorAuthManager implements AuthenticationManager {

  private final UserDao userDao;

  public TwoFactorAuthManager(UserDao userDao) {
    this.userDao = userDao;
  }

  @Override
  public Authentication authenticate(Authentication auth) throws AuthenticationException {
    if (!(auth instanceof TwoFactorAuthToken)) {
      return auth;
    }
    TwoFactorAuthToken tfa = (TwoFactorAuthToken) auth;
    String username = tfa.getPrincipal().toString();
    return userDao.findById(username).map(user -> authenticate(tfa, user)).orElse(tfa);
  }

  private Authentication authenticate(TwoFactorAuthToken auth, User user) {
    Credentials creds = (Credentials) auth.getCredentials();
    TwoFactorAuthToken result = new TwoFactorAuthToken(user.getUsername(), user.getRole());
    result.setAuthenticated(passwordMatches(creds, user) && codeMatches(creds, user));
    return result;
  }

  private boolean passwordMatches(Credentials creds, User user) {
    return user.getPassword().equals(creds.getPassword());
  }

  private boolean codeMatches(Credentials creds, User user) {
    return TOTP.calculate(user.getSecret()).matches(creds.getCode());
  }
}
