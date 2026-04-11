package br.infnet.tp1guilda.repository.aventura;

import br.infnet.tp1guilda.domain.aventura.Aventureiro;
import br.infnet.tp1guilda.enums.Classe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface AventureiroRepository extends JpaRepository<Aventureiro, Long>, JpaSpecificationExecutor<Aventureiro> {

    @Query("SELECT a FROM Aventureiro a WHERE "
            + "(:classe IS NULL OR a.classe = :classe) AND "
            + "(:ativo IS NULL OR a.ativo = :ativo) AND "
            + "(:nivelMinimo IS NULL OR a.nivel >= :nivelMinimo)")
    Page<Aventureiro> findWithFilter(@Param("classe") Classe classe,
                                     @Param("ativo") Boolean ativo,
                                     @Param("nivelMinimo") Integer nivelMinimo,
                                     Pageable pageable);

    @Query("SELECT a FROM Aventureiro a WHERE LOWER(a.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    Page<Aventureiro> findByNomeContaining(@Param("nome") String nome, Pageable pageable);
}