package br.infnet.tp1guilda.dto.aventureiro;

import br.infnet.tp1guilda.enums.Classe;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CriarAventureiro(
        @NotNull(message = "A organização é obrigatória")
        Long organizacaoId,
        @NotNull(message = "O usuário responsável é obrigatório")
        Long usuarioId,
        @NotBlank(message = "Tem que haver um nome")
        String nome,
        @NotNull(message = "Tem que haver uma classe")
        Classe classe,
        @Min(value = 1, message = "O nível deve ser maior ou igual a 1")
        int nivel
) {
}