package br.infnet.tp1guilda.dto.relatorio;

import br.infnet.tp1guilda.domain.aventura.enums.NivelPerigo;
import br.infnet.tp1guilda.domain.aventura.enums.StatusMissao;

public record RelatorioMissao(
        Long missaoId,
        String titulo,
        StatusMissao status,
        NivelPerigo nivelPerigo,
        long totalParticipantes,
        double totalRecompensas
) {
}