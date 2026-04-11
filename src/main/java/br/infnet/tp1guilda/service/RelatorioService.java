package br.infnet.tp1guilda.service;

import br.infnet.tp1guilda.domain.aventura.enums.NivelPerigo;
import br.infnet.tp1guilda.domain.aventura.enums.StatusMissao;
import br.infnet.tp1guilda.dto.relatorio.RankingAventureiro;
import br.infnet.tp1guilda.dto.relatorio.RelatorioMissao;
import br.infnet.tp1guilda.repository.aventura.ParticipacaoMissaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RelatorioService {

    private final ParticipacaoMissaoRepository participacaoMissaoRepository;

    public List<RankingAventureiro> ranking(OffsetDateTime dataInicio, OffsetDateTime dataFim, StatusMissao statusMissao) {
        OffsetDateTime dataDe = dataInicio != null ? dataInicio : OffsetDateTime.parse("1900-01-01T00:00:00Z");
        OffsetDateTime dataAte = dataFim != null ? dataFim : OffsetDateTime.now();

        return participacaoMissaoRepository.findRanking(dataDe, dataAte, statusMissao)
                .stream()
                .map(r -> new RankingAventureiro(
                        (Long) r[0],
                        (String) r[1],
                        (long) r[2],
                        ((Number) r[3]).doubleValue(),
                        (long) r[4]
                ))
                .toList();
    }

    public List<RelatorioMissao> relatorioMissoes(OffsetDateTime dataInicio, OffsetDateTime dataFim) {
        OffsetDateTime dataDe = dataInicio != null ? dataInicio : OffsetDateTime.parse("1900-01-01T00:00:00Z");
        OffsetDateTime dataAte = dataFim != null ? dataFim : OffsetDateTime.now();

        return participacaoMissaoRepository.findRelatorioMissoes(dataDe, dataAte)
                .stream()
                .map(r -> new RelatorioMissao(
                        (Long) r[0],
                        (String) r[1],
                        (StatusMissao) r[2],
                        (NivelPerigo) r[3],
                        (long) r[4],
                        ((Number) r[5]).doubleValue()
                ))
                .toList();
    }
}