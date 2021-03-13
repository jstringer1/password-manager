package uk.co.stringerj.passwordmanager.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import uk.co.stringerj.passwordmanager.dao.UserDao;
import uk.co.stringerj.passwordmanager.dao.model.User;
import uk.co.stringerj.passwordmanager.model.UserPassword;

@Service
public class UserAdminService {

  @Autowired private UserDao userDao;
  @Autowired private PasswordEncoder passwordEncoder;

  public List<String> getAllUsers() {
    List<String> users = new ArrayList<>();
    userDao.findAll().forEach(user -> users.add(user.getUsername()));
    return users;
  }

  public void addUser(UserPassword user) {
    userDao.save(
        new User(
            user.getUsername(),
            passwordEncoder.encode(user.getPassword()),
            "qwertyuiopasdfghjklzxcvbnm",
            "USER"));
  }
}
