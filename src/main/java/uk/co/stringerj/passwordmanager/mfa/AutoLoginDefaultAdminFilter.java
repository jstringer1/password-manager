package uk.co.stringerj.passwordmanager.mfa;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import uk.co.stringerj.passwordmanager.dao.UserDao;
import uk.co.stringerj.passwordmanager.dao.model.User;

/**
 * On first request if we have a clean database with no user data automatically login the default
 * admin user to setup user details.
 */
public class AutoLoginDefaultAdminFilter extends GenericFilterBean {

  private final UserDao userDao;
  private final AtomicBoolean firstRequest = new AtomicBoolean(true);

  public AutoLoginDefaultAdminFilter(UserDao userDao) {
    this.userDao = userDao;
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    if (!firstRequest.compareAndSet(true, false) || userDao.count() > 1) {
      chain.doFilter(request, response);
      return;
    }
    Iterator<User> it = userDao.findAll().iterator();
    if (!it.hasNext()) {
      chain.doFilter(request, response);
      return;
    }
    User defaultAdmin = it.next();
    TwoFactorAuthToken auth =
        new TwoFactorAuthToken(defaultAdmin.getUsername(), defaultAdmin.getRole());
    auth.setAuthenticated(true);
    SecurityContextHolder.getContext().setAuthentication(auth);
    if (response instanceof HttpServletResponse) {
      ((HttpServletResponse) response).sendRedirect("/admin");
    }
  }
}
