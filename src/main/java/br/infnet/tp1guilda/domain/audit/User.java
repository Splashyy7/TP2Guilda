package br.infnet.tp1guilda.domain.audit;

import br.infnet.tp1guilda.domain.audit.enums.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "usuarios",
        schema = "audit",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_usuarios_email_por_org", columnNames = {"organizacao_id", "email"})
        }
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organizacao_id", nullable = false, foreignKey = @ForeignKey(name = "fk_usuarios_org"))
    private Organization organizacao;

    @NotBlank
    @Size(max = 120)
    @Column(name = "nome", nullable = false, length = 120)
    private String nome;

    @NotBlank
    @Email
    @Size(max = 180)
    @Column(name = "email", nullable = false, length = 180)
    private String email;

    @NotBlank
    @Size(max = 255)
    @Column(name = "senha_hash", nullable = false, length = 255)
    private String senhaHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private UserStatus status = UserStatus.PENDENTE;

    @Column(name = "ultimo_login_em")
    private OffsetDateTime ultimoLoginEm;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        OffsetDateTime agora = OffsetDateTime.now();
        this.createdAt = agora;
        this.updatedAt = agora;

        if (this.status == null) {
            this.status = UserStatus.PENDENTE;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }
}