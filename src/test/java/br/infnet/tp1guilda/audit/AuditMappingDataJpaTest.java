package br.infnet.tp1guilda.audit;

import br.infnet.tp1guilda.domain.audit.Role;
import br.infnet.tp1guilda.domain.audit.User;
import br.infnet.tp1guilda.repository.audit.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "spring.cache.type=none")
@Transactional
class AuditMappingDataJpaTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void deveCarregarUsuarioRolesOrganizationEPermissions() {
        User usuario = userRepository.findAll().stream()
                .findFirst()
                .orElseThrow();

        assertThat(usuario.getOrganizacao()).isNotNull();
        assertThat(usuario.getRoles()).isNotNull();
        assertThat(usuario.getRoles()).isNotEmpty();

        boolean algumaRoleTemPermissions = usuario.getRoles().stream()
                .map(Role::getPermissions)
                .anyMatch(permissoes -> permissoes != null && !permissoes.isEmpty());

        assertThat(algumaRoleTemPermissions).isTrue();
    }
}