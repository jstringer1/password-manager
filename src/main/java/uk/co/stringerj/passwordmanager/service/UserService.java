package uk.co.stringerj.passwordmanager.service;

import java.awt.image.BufferedImage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import uk.co.stringerj.passwordmanager.dao.UserDao;
import uk.co.stringerj.passwordmanager.model.UserDetails;
import uk.co.stringerj.passwordmanager.service.SecretGenerator.Encoding;

@Service
public class UserService {

  @Autowired private UserDao userDao;
  @Autowired private SecretGenerator secretGenerator;
  @Autowired private QRCodeService qrCodeService;

  public UserDetails getUserDetails() {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    return userDao
        .findById(username)
        .map(user -> new UserDetails(username, user.getSecret()))
        .orElse(new UserDetails("", ""));
  }

  public BufferedImage getQRCode() {
    return qrCodeService.getQrCode(getUserDetails().getSecret(), 200, 200);
  }

  public void resetSecret() {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    userDao
        .findById(username)
        .ifPresent(
            user -> {
              user.setSecret(secretGenerator.generateSecret(Encoding.BASE32_SECRET, 20));
              userDao.save(user);
            });
  }
}
