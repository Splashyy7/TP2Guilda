package br.infnet.tp1guilda.dto.missao;

import br.infnet.tp1guilda.domain.aventura.enums.NivelPerigo;
import br.infnet.tp1guilda.domain.aventura.enums.StatusMissao;

import java.time.OffsetDateTime;

public record FilterRequestMissao(
        StatusMissao status,
        NivelPerigo nivelPerigo,
        OffsetDateTime dataInicio,
        OffsetDateTime dataFim
) {
}