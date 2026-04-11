package br.infnet.tp1guilda.dto.missao;

import br.infnet.tp1guilda.domain.aventura.enums.NivelPerigo;
import br.infnet.tp1guilda.domain.aventura.enums.StatusMissao;

import java.time.OffsetDateTime;
import java.util.List;

public record ResponseMissaoDetalhada(
        Long id,
        String titulo,
        StatusMissao status,
        NivelPerigo nivelPerigo,
        OffsetDateTime createdAt,
        OffsetDateTime dataInicio,
        OffsetDateTime dataTermino,
        List<ResponseParticipante> participantes
) {
}