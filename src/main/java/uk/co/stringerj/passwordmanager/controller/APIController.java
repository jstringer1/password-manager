package uk.co.stringerj.passwordmanager.controller;

import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.ServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import uk.co.stringerj.passwordmanager.model.Credentials;
import uk.co.stringerj.passwordmanager.model.UserDetails;
import uk.co.stringerj.passwordmanager.service.UserService;

@RestController
public class APIController {

  @Autowired private UserService userService;

  @GetMapping(path = "/api/user", produces = "application/json")
  public UserDetails userDetails() {
    return userService.getUserDetails();
  }

  @GetMapping("/api/user/qr")
  public void qr(ServletResponse response) throws IOException {
    response.setContentType("image/jpeg");
    ImageIO.write(userService.getQRCode(), "jpg", response.getOutputStream());
  }

  @PostMapping("/api/user/resetsecret")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void resetSecret() {
    userService.resetSecret();
  }

  @GetMapping(path = "/api/user/credentials", produces = "application/json")
  public List<Credentials> getCredentials() {
    return userService.getCredentials();
  }

  @PostMapping(path = "/api/user/credentials", consumes = "application/json")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void saveCredentials(@RequestBody Credentials credentials) {
    userService.saveCredentials(credentials);
  }
}
