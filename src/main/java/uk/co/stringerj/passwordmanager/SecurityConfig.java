package uk.co.stringerj.passwordmanager;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import uk.co.stringerj.passwordmanager.dao.UserDao;
import uk.co.stringerj.passwordmanager.dao.model.User;
import uk.co.stringerj.passwordmanager.mfa.AutoLoginDefaultAdminFilter;
import uk.co.stringerj.passwordmanager.mfa.TwoFactorAuthFilter;
import uk.co.stringerj.passwordmanager.mfa.TwoFactorAuthManager;
import uk.co.stringerj.passwordmanager.service.SecretGenerator;
import uk.co.stringerj.passwordmanager.service.SecretGenerator.Encoding;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  private static final Logger LOGGER = Logger.getLogger(SecurityConfig.class.getSimpleName());

  @Autowired private UserDao userDao;
  @Autowired private SecretGenerator secretGenerator;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
        .antMatchers("/login", "/static/**")
        .permitAll()
        .antMatchers("/admin/**")
        .hasRole("ADMIN")
        .anyRequest()
        .authenticated()
        .and()
        .csrf()
        .disable()
        .formLogin()
        .loginPage("/login")
        .and()
        .addFilterBefore(twoFactorAuthFilter(), UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(
            new AutoLoginDefaultAdminFilter(userDao), UsernamePasswordAuthenticationFilter.class);
  }

  private Filter twoFactorAuthFilter() {
    TwoFactorAuthFilter filter = new TwoFactorAuthFilter();
    filter.setAuthenticationManager(new TwoFactorAuthManager(userDao, passwordEncoder()));
    filter.setAuthenticationSuccessHandler(new SavedRequestAwareAuthenticationSuccessHandler());
    filter.setAuthenticationFailureHandler(new SimpleUrlAuthenticationFailureHandler("/login"));
    filter.setAuthenticationSuccessHandler(this::onAuthenticationSuccess);
    return filter;
  }

  @Bean
  public User defaultAdmin() {
    String password = secretGenerator.generateSecret(Encoding.PASSWORD, 20);
    String secret = secretGenerator.generateSecret(Encoding.BASE32_SECRET, 20);
    LOGGER.warning(
        String.format(
            "\n*****\tSETUP USER HAS INITIAL PASSWORD: %s AND SECRET: %s\n", password, secret));
    return userDao.save(new User("setup", passwordEncoder().encode(password), secret, "ADMIN"));
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  private void onAuthenticationSuccess(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication)
      throws IOException, ServletException {
    if (authentication
        .getAuthorities()
        .stream()
        .map(GrantedAuthority::getAuthority)
        .filter(role -> "ROLE_ADMIN".equals(role))
        .findAny()
        .isPresent()) {
      response.sendRedirect("/admin");
    } else {
      response.sendRedirect("/");
    }
  }
}
