package uk.co.stringerj.passwordmanager.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import uk.co.stringerj.passwordmanager.dao.model.User;

@Repository
public interface UserDao extends CrudRepository<User, String> {}
