package br.infnet.tp1guilda.controllers;

import br.infnet.tp1guilda.domain.aventura.enums.StatusMissao;
import br.infnet.tp1guilda.dto.relatorio.RankingAventureiro;
import br.infnet.tp1guilda.dto.relatorio.RelatorioMissao;
import br.infnet.tp1guilda.service.RelatorioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/relatorios")
@RequiredArgsConstructor
@Tag(name = "Relatórios", description = "Relatórios analíticos: ranking de aventureiros e agregados de missões")
public class RelatorioController {

    private final RelatorioService relatorioService;

    @GetMapping("/ranking")
    @Operation(summary = "Ranking de aventureiros", description = "Filtros opcionais por período e status de missão")
    public ResponseEntity<List<RankingAventureiro>> ranking(
            @RequestParam(required = false) OffsetDateTime dataInicio,
            @RequestParam(required = false) OffsetDateTime dataFim,
            @RequestParam(required = false) StatusMissao statusMissao
    ) {
        return ResponseEntity.ok(relatorioService.ranking(dataInicio, dataFim, statusMissao));
    }

    @GetMapping("/missoes")
    @Operation(summary = "Relatório agregado de missões", description = "Agregados por período")
    public ResponseEntity<List<RelatorioMissao>> relatorioMissoes(
            @RequestParam(required = false) OffsetDateTime dataInicio,
            @RequestParam(required = false) OffsetDateTime dataFim
    ) {
        return ResponseEntity.ok(relatorioService.relatorioMissoes(dataInicio, dataFim));
    }
}