package br.infnet.tp1guilda.dto.missao;

import br.infnet.tp1guilda.domain.aventura.enums.PapelMissao;

public record ResponseParticipante(
        Long aventureiroId,
        String nomeAventureiro,
        PapelMissao papel,
        Double recompensaOuro,
        boolean destaque
) {
}