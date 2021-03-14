package uk.co.stringerj.passwordmanager.controller;

import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.ServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import uk.co.stringerj.passwordmanager.model.UserDetails;
import uk.co.stringerj.passwordmanager.model.UserPassword;
import uk.co.stringerj.passwordmanager.service.UserAdminService;

@RestController
public class AdminApiController {

  @Autowired private UserAdminService userAdminService;

  @GetMapping(path = "/admin/api/users", produces = "application/json")
  public List<UserDetails> getAllUsers() {
    return userAdminService.getAllUsers();
  }

  @PostMapping(path = "/admin/api/user", consumes = "application/json")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void addUser(@RequestBody UserPassword user) {
    userAdminService.addUser(user);
  }

  @GetMapping("/admin/api/qr/{secret}")
  public void qr(@PathVariable String secret, ServletResponse response) throws IOException {
    response.setContentType("image/jpeg");
    ImageIO.write(userAdminService.getQrCode(secret), "jpg", response.getOutputStream());
  }
}
