package br.infnet.tp1guilda.repository.audit;

import br.infnet.tp1guilda.domain.audit.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
}