package br.infnet.tp1guilda.domain.aventura;

import br.infnet.tp1guilda.enums.Especie;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@Embeddable
public class Companheiro {

    @Column(name = "companheiro_nome", nullable = false, length = 120)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "companheiro_especie", nullable = false, length = 30)
    private Especie especie;

    @Column(name = "companheiro_lealdade", nullable = false)
    private Integer lealdade;

    public Companheiro(String nome, Especie especie, Integer lealdade) {
        this.nome = nome;
        this.especie = especie;
        this.lealdade = lealdade;
    }
}