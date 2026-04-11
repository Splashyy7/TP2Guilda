package br.infnet.tp1guilda.service;

import br.infnet.tp1guilda.domain.aventura.Missao;
import br.infnet.tp1guilda.dto.PaginatedView;
import br.infnet.tp1guilda.dto.missao.FilterRequestMissao;
import br.infnet.tp1guilda.exceptions.BusinessException;
import br.infnet.tp1guilda.repository.aventura.MissaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class MissaoService {

    private final MissaoRepository missaoRepository;

    public PaginatedView<Missao> listar(FilterRequestMissao filtro, Pageable pageable) {
        OffsetDateTime dataDe = filtro.dataInicio() != null ? filtro.dataInicio()
                : OffsetDateTime.parse("1900-01-01T00:00:00Z");
        OffsetDateTime dataAte = filtro.dataFim() != null ? filtro.dataFim()
                : OffsetDateTime.now();

        Page<Missao> resultado = missaoRepository.findWithFilter(
                filtro.status(), filtro.nivelPerigo(), dataDe, dataAte, pageable
        );
        return new PaginatedView<>(pageable.getPageNumber(), pageable.getPageSize(), (int) resultado.getTotalElements(), resultado.getContent());
    }

    public Missao buscarPorId(Long id) {
        return missaoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Missão não encontrada com id: " + id));
    }
}