package uk.co.stringerj.passwordmanager.model;

import java.io.Serializable;

public class Credentials implements Serializable {
  private static final long serialVersionUID = -65113509409248867L;
  private final String service;
  private final String username;
  private final String password;

  public Credentials(String service, String username, String password) {
    this.service = service;
    this.username = username;
    this.password = password;
  }

  public String getService() {
    return service;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }
}
