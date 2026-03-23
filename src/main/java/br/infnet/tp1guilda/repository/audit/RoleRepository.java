package br.infnet.tp1guilda.repository.audit;

import br.infnet.tp1guilda.domain.audit.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Long> {
    List<Role> findByOrganizacaoId(Long organizacaoId);
}