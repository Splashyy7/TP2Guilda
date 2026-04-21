package br.infnet.tp1guilda.service;

import br.infnet.tp1guilda.domain.operacoes.PainelTaticoMissaoMV;
import br.infnet.tp1guilda.repository.operacoes.PainelTaticoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PainelTaticoService {

    private final PainelTaticoRepository painelTaticoRepository;

    @Cacheable("topMissoes15dias")
    public List<PainelTaticoMissaoMV> buscarMissoesRelevantes() {
        OffsetDateTime dataLimite = OffsetDateTime.now().minusDays(15);
        return painelTaticoRepository.findTopByUltimaAtualizacaoAfter(dataLimite, PageRequest.of(0, 10));
    }
}