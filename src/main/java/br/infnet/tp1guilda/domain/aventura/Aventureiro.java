package br.infnet.tp1guilda.domain.aventura;

import br.infnet.tp1guilda.domain.audit.Organization;
import br.infnet.tp1guilda.domain.audit.User;
import br.infnet.tp1guilda.enums.Classe;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "aventureiros", schema = "aventura")
public class Aventureiro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organizacao_id", nullable = false, foreignKey = @ForeignKey(name = "fk_aventureiros_org"))
    private Organization organizacao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cadastrado_por_id", nullable = false, foreignKey = @ForeignKey(name = "fk_aventureiros_usuario"))
    private User cadastradoPor;

    @Column(name = "nome", nullable = false, length = 120)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "classe", nullable = false, length = 30)
    private Classe classe;

    @Column(name = "nivel", nullable = false)
    private int nivel;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo;

    @Embedded
    private Companheiro companheiro;

    @OneToMany(mappedBy = "aventureiro", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ParticipacaoMissao> participacoes = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public Aventureiro(Organization organizacao, User cadastradoPor, String nome, Classe classe, int nivel) {
        this.organizacao = organizacao;
        this.cadastradoPor = cadastradoPor;
        this.nome = nome;
        this.classe = classe;
        this.nivel = nivel;
        this.ativo = true;
    }

    public void alterarNome(String nome) {
        this.nome = nome;
    }

    public void alterarClasse(Classe classe) {
        this.classe = classe;
    }

    public void alterarNivel(int nivel) {
        this.nivel = nivel;
    }

    public void encerrarVinculo() {
        this.ativo = false;
    }

    public void recrutar() {
        this.ativo = true;
    }

    public void definirCompanheiro(Companheiro companheiro) {
        this.companheiro = companheiro;
    }

    public void removerCompanheiro() {
        this.companheiro = null;
    }

    @PrePersist
    public void prePersist() {
        OffsetDateTime agora = OffsetDateTime.now();
        this.createdAt = agora;
        this.updatedAt = agora;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }
}