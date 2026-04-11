package br.infnet.tp1guilda.controllers;

import br.infnet.tp1guilda.domain.aventura.Missao;
import br.infnet.tp1guilda.domain.aventura.enums.NivelPerigo;
import br.infnet.tp1guilda.domain.aventura.enums.StatusMissao;
import br.infnet.tp1guilda.dto.PaginatedView;
import br.infnet.tp1guilda.dto.missao.FilterRequestMissao;
import br.infnet.tp1guilda.dto.missao.ResponseMissao;
import br.infnet.tp1guilda.dto.missao.ResponseMissaoDetalhada;
import br.infnet.tp1guilda.mapper.MissaoMapper;
import br.infnet.tp1guilda.service.MissaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/missoes")
@RequiredArgsConstructor
public class MissaoController {

    private final MissaoService missaoService;
    private final MissaoMapper missaoMapper;

    @GetMapping
    public ResponseEntity<List<ResponseMissao>> listar(
            @RequestParam(required = false) StatusMissao status,
            @RequestParam(required = false) NivelPerigo nivelPerigo,
            @RequestParam(required = false) OffsetDateTime dataInicio,
            @RequestParam(required = false) OffsetDateTime dataFim,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Sort sortOrder = Sort.by(Sort.Direction.fromString(direction), sort);
        FilterRequestMissao filtro = new FilterRequestMissao(status, nivelPerigo, dataInicio, dataFim);
        PaginatedView<Missao> resultado = missaoService.listar(filtro, PageRequest.of(page, size, sortOrder));

        List<ResponseMissao> response = resultado.content()
                .stream()
                .map(missaoMapper::toResponse)
                .toList();

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", String.valueOf(resultado.total()));
        headers.add("X-Page", String.valueOf(resultado.page()));
        headers.add("X-Size", String.valueOf(resultado.size()));
        headers.add("X-Total-Pages", String.valueOf(resultado.totalPages()));

        return ResponseEntity.ok()
                .headers(headers)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseMissaoDetalhada> buscarPorId(@PathVariable Long id) {
        Missao missao = missaoService.buscarPorId(id);
        return ResponseEntity.ok(missaoMapper.toDetalhada(missao));
    }
}