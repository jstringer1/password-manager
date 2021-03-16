package uk.co.stringerj.passwordmanager.model;

import java.io.Serializable;

public class Credentials implements Serializable {
  private static final long serialVersionUID = -65113509409248867L;
  private String service;
  private String username;
  private String password;

  public Credentials() {}

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

  public void setService(String service) {
    this.service = service;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
