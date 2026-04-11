package br.infnet.tp1guilda.dto.aventureiro;

import br.infnet.tp1guilda.dto.companheiro.ResponseCompanheiro;
import br.infnet.tp1guilda.dto.missao.ResponseMissaoResumo;
import br.infnet.tp1guilda.enums.Classe;

public record ResponseAventureiro(
        Long id,
        Long organizacaoId,
        String nome,
        Classe classe,
        int nivel,
        boolean ativo,
        ResponseCompanheiro companheiro,
        long totalParticipacoes,
        ResponseMissaoResumo ultimaMissao
) {
}