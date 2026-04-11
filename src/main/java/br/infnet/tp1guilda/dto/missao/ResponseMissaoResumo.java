package br.infnet.tp1guilda.dto.missao;

import br.infnet.tp1guilda.domain.aventura.enums.NivelPerigo;
import br.infnet.tp1guilda.domain.aventura.enums.StatusMissao;

public record ResponseMissaoResumo(
        Long id,
        String titulo,
        StatusMissao status,
        NivelPerigo nivelPerigo
) {
}