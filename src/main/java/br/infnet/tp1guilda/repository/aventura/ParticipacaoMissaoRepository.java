package br.infnet.tp1guilda.repository.aventura;

import br.infnet.tp1guilda.domain.aventura.ParticipacaoMissao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface ParticipacaoMissaoRepository extends JpaRepository<ParticipacaoMissao, Long> {

    @Query("SELECT COUNT(p) FROM ParticipacaoMissao p WHERE p.aventureiro.id = :aventureiroId")
    long countByAventureiroId(@Param("aventureiroId") Long aventureiroId);

    @Query("SELECT p FROM ParticipacaoMissao p JOIN FETCH p.missao WHERE p.aventureiro.id = :aventureiroId ORDER BY p.createdAt DESC LIMIT 1")
    Optional<ParticipacaoMissao> findUltimaByAventureiroId(@Param("aventureiroId") Long aventureiroId);

    @Query("SELECT p.aventureiro.id, p.aventureiro.nome, COUNT(p), COALESCE(SUM(p.recompensaOuro), 0), SUM(CASE WHEN p.destaque = true THEN 1 ELSE 0 END) "
            + "FROM ParticipacaoMissao p "
            + "WHERE p.createdAt >= :dataInicio "
            + "AND p.createdAt <= :dataFim "
            + "AND (:statusMissao IS NULL OR p.missao.status = :statusMissao) "
            + "GROUP BY p.aventureiro.id, p.aventureiro.nome "
            + "ORDER BY COUNT(p) DESC")
    List<Object[]> findRanking(@Param("dataInicio") OffsetDateTime dataInicio,
                               @Param("dataFim") OffsetDateTime dataFim,
                               @Param("statusMissao") br.infnet.tp1guilda.domain.aventura.enums.StatusMissao statusMissao);

    @Query("SELECT m.id, m.titulo, m.status, m.nivelPerigo, COUNT(p), COALESCE(SUM(p.recompensaOuro), 0) "
            + "FROM Missao m LEFT JOIN m.participacoes p "
            + "WHERE m.createdAt >= :dataInicio "
            + "AND m.createdAt <= :dataFim "
            + "GROUP BY m.id, m.titulo, m.status, m.nivelPerigo")
    List<Object[]> findRelatorioMissoes(@Param("dataInicio") OffsetDateTime dataInicio,
                                        @Param("dataFim") OffsetDateTime dataFim);
}