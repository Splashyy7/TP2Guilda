package br.infnet.tp1guilda.controllers;

import br.infnet.tp1guilda.domain.aventura.enums.StatusMissao;
import br.infnet.tp1guilda.dto.relatorio.RankingAventureiro;
import br.infnet.tp1guilda.dto.relatorio.RelatorioMissao;
import br.infnet.tp1guilda.service.RelatorioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/relatorios")
@RequiredArgsConstructor
public class RelatorioController {

    private final RelatorioService relatorioService;

    @GetMapping("/ranking")
    public ResponseEntity<List<RankingAventureiro>> ranking(
            @RequestParam(required = false) OffsetDateTime dataInicio,
            @RequestParam(required = false) OffsetDateTime dataFim,
            @RequestParam(required = false) StatusMissao statusMissao
    ) {
        return ResponseEntity.ok(relatorioService.ranking(dataInicio, dataFim, statusMissao));
    }

    @GetMapping("/missoes")
    public ResponseEntity<List<RelatorioMissao>> relatorioMissoes(
            @RequestParam(required = false) OffsetDateTime dataInicio,
            @RequestParam(required = false) OffsetDateTime dataFim
    ) {
        return ResponseEntity.ok(relatorioService.relatorioMissoes(dataInicio, dataFim));
    }
}