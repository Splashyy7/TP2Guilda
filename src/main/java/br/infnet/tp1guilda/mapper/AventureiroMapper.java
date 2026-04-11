package br.infnet.tp1guilda.mapper;

import br.infnet.tp1guilda.domain.audit.Organization;
import br.infnet.tp1guilda.domain.audit.User;
import br.infnet.tp1guilda.domain.aventura.Aventureiro;
import br.infnet.tp1guilda.domain.aventura.Missao;
import br.infnet.tp1guilda.dto.aventureiro.CriarAventureiro;
import br.infnet.tp1guilda.dto.aventureiro.ResponseAventureiro;
import br.infnet.tp1guilda.dto.companheiro.ResponseCompanheiro;
import br.infnet.tp1guilda.dto.missao.ResponseMissaoResumo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AventureiroMapper {

    private final CompanheiroMapper companheiroMapper;
    private final MissaoMapper missaoMapper;

    public Aventureiro toEntity(CriarAventureiro dto, Organization organizacao, User usuario) {
        return new Aventureiro(
                organizacao,
                usuario,
                dto.nome(),
                dto.classe(),
                dto.nivel()
        );
    }

    public ResponseAventureiro toResponse(Aventureiro aventureiro) {
        return new ResponseAventureiro(
                aventureiro.getId(),
                aventureiro.getOrganizacao().getId(),
                aventureiro.getNome(),
                aventureiro.getClasse(),
                aventureiro.getNivel(),
                aventureiro.getAtivo(),
                null,
                0,
                null
        );
    }

    public ResponseAventureiro toResponseCompleto(Aventureiro aventureiro, long totalParticipacoes, Missao ultimaMissao) {
        ResponseCompanheiro companheiro = companheiroMapper.toResponse(aventureiro.getId(), aventureiro.getCompanheiro());
        ResponseMissaoResumo missaoResumo = ultimaMissao != null ? missaoMapper.toResumo(ultimaMissao) : null;

        return new ResponseAventureiro(
                aventureiro.getId(),
                aventureiro.getOrganizacao().getId(),
                aventureiro.getNome(),
                aventureiro.getClasse(),
                aventureiro.getNivel(),
                aventureiro.getAtivo(),
                companheiro,
                totalParticipacoes,
                missaoResumo
        );
    }
}