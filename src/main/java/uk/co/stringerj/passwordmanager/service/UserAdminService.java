package uk.co.stringerj.passwordmanager.service;

import static uk.co.stringerj.passwordmanager.service.SecretGenerator.Encoding.BASE32_SECRET;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import uk.co.stringerj.passwordmanager.dao.UserDao;
import uk.co.stringerj.passwordmanager.dao.model.User;
import uk.co.stringerj.passwordmanager.model.UserDetails;
import uk.co.stringerj.passwordmanager.model.UserPassword;

@Service
public class UserAdminService {

  @Autowired private UserDao userDao;
  @Autowired private PasswordEncoder passwordEncoder;
  @Autowired private SecretGenerator secretGenerator;
  @Autowired private QRCodeService qrCodeService;

  public List<UserDetails> getAllUsers() {
    List<UserDetails> users = new ArrayList<>();
    userDao
        .findAll()
        .forEach(user -> users.add(new UserDetails(user.getUsername(), user.getSecret())));
    return users;
  }

  public void addUser(UserPassword user) {
    userDao.save(
        new User(
            user.getUsername(),
            passwordEncoder.encode(user.getPassword()),
            secretGenerator.generateSecret(BASE32_SECRET, 20),
            "USER"));
  }

  public BufferedImage getQrCode(String secret) {
    return qrCodeService.getQrCode(secret, 100, 100);
  }
}
