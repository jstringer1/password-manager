package uk.co.stringerj.passwordmanager.service;

import java.awt.image.BufferedImage;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import uk.co.stringerj.passwordmanager.dao.UserDao;
import uk.co.stringerj.passwordmanager.dao.model.User;
import uk.co.stringerj.passwordmanager.mfa.TwoFactorAuthToken;
import uk.co.stringerj.passwordmanager.model.Credentials;
import uk.co.stringerj.passwordmanager.model.UserDetails;
import uk.co.stringerj.passwordmanager.service.SecretGenerator.Encoding;

@Service
public class UserService {

  @Autowired private UserDao userDao;
  @Autowired private CredentialsService credentialsService;
  @Autowired private SecretGenerator secretGenerator;
  @Autowired private QRCodeService qrCodeService;

  public UserDetails getUserDetails() {
    return userDao
        .findById(getUsername())
        .map(user -> new UserDetails(user.getUsername(), user.getSecret()))
        .orElse(new UserDetails("", ""));
  }

  public BufferedImage getQRCode() {
    return qrCodeService.getQrCode(getUserDetails().getSecret(), 100, 100);
  }

  public void saveCredentials(Credentials credentials) {
    credentialsService.saveCredentials(credentials, getUsername(), getKey());
  }

  public List<Credentials> getCredentials() {
    return credentialsService.getCredentials(getUsername(), getKey());
  }

  public void resetSecret() {
    userDao.findById(getUsername()).ifPresent(this::resetSecret);
  }

  private void resetSecret(User user) {
    user.setSecret(secretGenerator.generateSecret(Encoding.BASE32_SECRET, 20));
    userDao.save(user);
  }

  private String getUsername() {
    return SecurityContextHolder.getContext().getAuthentication().getName();
  }

  private byte[] getKey() {
    return ((TwoFactorAuthToken.Credentials)
            SecurityContextHolder.getContext().getAuthentication().getCredentials())
        .getKey();
  }
}
