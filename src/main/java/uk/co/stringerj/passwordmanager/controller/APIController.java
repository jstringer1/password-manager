package uk.co.stringerj.passwordmanager.controller;

import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.ServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
