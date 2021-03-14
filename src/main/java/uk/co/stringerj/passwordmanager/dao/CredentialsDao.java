package uk.co.stringerj.passwordmanager.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import uk.co.stringerj.passwordmanager.dao.model.EncryptedCredentials;

@Repository
public interface CredentialsDao extends CrudRepository<EncryptedCredentials, Long> {
  Iterable<EncryptedCredentials> findAllByUsername(String username);
}
