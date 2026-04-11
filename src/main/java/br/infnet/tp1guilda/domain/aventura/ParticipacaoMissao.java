package br.infnet.tp1guilda.domain.aventura;

import br.infnet.tp1guilda.domain.aventura.enums.PapelMissao;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "participacoes_missao", schema = "aventura",
        uniqueConstraints = @UniqueConstraint(name = "uk_missao_aventureiro", columnNames = {"missao_id", "aventureiro_id"}))
public class ParticipacaoMissao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "missao_id", nullable = false, foreignKey = @ForeignKey(name = "fk_participacoes_missao"))
    private Missao missao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "aventureiro_id", nullable = false, foreignKey = @ForeignKey(name = "fk_participacoes_aventureiro"))
    private Aventureiro aventureiro;

    @Enumerated(EnumType.STRING)
    @Column(name = "papel", nullable = false, length = 30)
    private PapelMissao papel;

    @Column(name = "recompensa_ouro")
    private Double recompensaOuro;

    @Column(name = "destaque", nullable = false)
    private Boolean destaque;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = OffsetDateTime.now();
    }
}