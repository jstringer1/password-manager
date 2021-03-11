package uk.co.stringerj.passwordmanager.model;

public class UserDetails {
  private final String username;
  private final String secret;

  public UserDetails(String username, String secret) {
    this.username = username;
    this.secret = secret;
  }

  public String getUsername() {
    return username;
  }

  public String getSecret() {
    return secret;
  }
}
