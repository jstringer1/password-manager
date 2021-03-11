package uk.co.stringerj.passwordmanager;

import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import uk.co.stringerj.passwordmanager.dao.UserDao;
import uk.co.stringerj.passwordmanager.dao.model.User;
import uk.co.stringerj.passwordmanager.mfa.TwoFactorAuthFilter;
import uk.co.stringerj.passwordmanager.mfa.TwoFactorAuthManager;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired private UserDao userDao;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
        .antMatchers("/login", "/static/**")
        .permitAll()
        .antMatchers("/admin")
        .hasRole("ADMIN")
        .anyRequest()
        .authenticated()
        .and()
        .csrf()
        .disable()
        .formLogin()
        .loginPage("/login")
        .and()
        .addFilterBefore(twoFactorAuthFilter(), UsernamePasswordAuthenticationFilter.class);
  }

  private Filter twoFactorAuthFilter() {
    // TODO
    userDao.save(new User("john", "password", "qwertyuiopasdfghjklzxcvbnm"));
    TwoFactorAuthFilter filter = new TwoFactorAuthFilter();
    filter.setAuthenticationManager(new TwoFactorAuthManager(userDao));
    filter.setAuthenticationSuccessHandler(new SavedRequestAwareAuthenticationSuccessHandler());
    filter.setAuthenticationFailureHandler(new SimpleUrlAuthenticationFailureHandler("/login"));
    return filter;
  }
}
