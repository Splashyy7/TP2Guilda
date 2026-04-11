package br.infnet.tp1guilda.mapper;

import br.infnet.tp1guilda.domain.aventura.Missao;
import br.infnet.tp1guilda.domain.aventura.ParticipacaoMissao;
import br.infnet.tp1guilda.dto.missao.ResponseMissao;
import br.infnet.tp1guilda.dto.missao.ResponseMissaoDetalhada;
import br.infnet.tp1guilda.dto.missao.ResponseMissaoResumo;
import br.infnet.tp1guilda.dto.missao.ResponseParticipante;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MissaoMapper {

    public ResponseMissao toResponse(Missao missao) {
        return new ResponseMissao(
                missao.getId(),
                missao.getTitulo(),
                missao.getStatus(),
                missao.getNivelPerigo(),
                missao.getCreatedAt(),
                missao.getDataInicio(),
                missao.getDataTermino()
        );
    }

    public ResponseMissaoResumo toResumo(Missao missao) {
        return new ResponseMissaoResumo(
                missao.getId(),
                missao.getTitulo(),
                missao.getStatus(),
                missao.getNivelPerigo()
        );
    }

    public ResponseMissaoDetalhada toDetalhada(Missao missao) {
        List<ResponseParticipante> participantes = missao.getParticipacoes()
                .stream()
                .map(this::toParticipante)
                .toList();

        return new ResponseMissaoDetalhada(
                missao.getId(),
                missao.getTitulo(),
                missao.getStatus(),
                missao.getNivelPerigo(),
                missao.getCreatedAt(),
                missao.getDataInicio(),
                missao.getDataTermino(),
                participantes
        );
    }

    private ResponseParticipante toParticipante(ParticipacaoMissao p) {
        return new ResponseParticipante(
                p.getAventureiro().getId(),
                p.getAventureiro().getNome(),
                p.getPapel(),
                p.getRecompensaOuro(),
                p.getDestaque()
        );
    }
}