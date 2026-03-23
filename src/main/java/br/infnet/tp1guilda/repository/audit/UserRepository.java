package br.infnet.tp1guilda.repository.audit;

import br.infnet.tp1guilda.domain.audit.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}