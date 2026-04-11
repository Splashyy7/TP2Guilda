package br.infnet.tp1guilda.repository.aventura;

import br.infnet.tp1guilda.domain.aventura.Missao;
import br.infnet.tp1guilda.domain.aventura.enums.NivelPerigo;
import br.infnet.tp1guilda.domain.aventura.enums.StatusMissao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;

public interface MissaoRepository extends JpaRepository<Missao, Long> {

    @Query("SELECT m FROM Missao m WHERE "
            + "(:status IS NULL OR m.status = :status) AND "
            + "(:nivelPerigo IS NULL OR m.nivelPerigo = :nivelPerigo) AND "
            + "m.createdAt >= :dataInicio AND "
            + "m.createdAt <= :dataFim")
    Page<Missao> findWithFilter(@Param("status") StatusMissao status,
                                @Param("nivelPerigo") NivelPerigo nivelPerigo,
                                @Param("dataInicio") OffsetDateTime dataInicio,
                                @Param("dataFim") OffsetDateTime dataFim,
                                Pageable pageable);
}