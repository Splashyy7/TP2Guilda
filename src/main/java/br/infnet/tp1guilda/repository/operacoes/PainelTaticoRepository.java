package br.infnet.tp1guilda.repository.operacoes;

import br.infnet.tp1guilda.domain.operacoes.PainelTaticoMissaoMV;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

public interface PainelTaticoRepository extends JpaRepository<PainelTaticoMissaoMV, Long> {

    @Query("SELECT p FROM PainelTaticoMissaoMV p WHERE p.ultimaAtualizacao >= :dataLimite ORDER BY p.indiceProntidao DESC")
    List<PainelTaticoMissaoMV> findTopByUltimaAtualizacaoAfter(@Param("dataLimite") OffsetDateTime dataLimite, Pageable pageable);
}