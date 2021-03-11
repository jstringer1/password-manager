package uk.co.stringerj.passwordmanager.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UIController {

  @GetMapping("/")
  public String home() {
    return "home";
  }

  @GetMapping("/admin")
  public String admin() {
    return "admin";
  }

  @GetMapping("/login")
  public String login() {
    return "login";
  }

  @GetMapping("/logout")
  public String logout() {
    SecurityContextHolder.getContext().setAuthentication(null);
    return "redirect:/";
  }
}
