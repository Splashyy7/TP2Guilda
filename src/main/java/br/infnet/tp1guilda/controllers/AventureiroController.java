package br.infnet.tp1guilda.controllers;

import br.infnet.tp1guilda.dto.aventureiro.AtualizarAventureiro;
import br.infnet.tp1guilda.dto.aventureiro.CriarAventureiro;
import br.infnet.tp1guilda.dto.aventureiro.FilterRequestAventureiro;
import br.infnet.tp1guilda.dto.aventureiro.ResponseAventureiro;
import br.infnet.tp1guilda.dto.companheiro.DefinirCompanheiro;
import br.infnet.tp1guilda.service.AventureiroService;
import br.infnet.tp1guilda.dto.*;
import br.infnet.tp1guilda.mapper.*;
import br.infnet.tp1guilda.domain.aventura.Aventureiro;
import br.infnet.tp1guilda.enums.Classe;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/aventureiros")
@RequiredArgsConstructor
@Validated
@Tag(name = "Aventureiros", description = "Cadastro e ciclo de vida dos aventureiros (recrutar, encerrar, companheiro)")
public class AventureiroController {

    private final AventureiroMapper mapperAventureiro;
    private final AventureiroService serviceAventureiro;

    //post registrar aventureiro

    @PostMapping
    @Operation(summary = "Registrar aventureiro", description = "Cria um novo aventureiro vinculado a uma organização e usuário responsável")
    public ResponseEntity<ResponseAventureiro> registrarAventureiro(@RequestBody @Valid CriarAventureiro dto) {
        Aventureiro salvo = serviceAventureiro.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapperAventureiro.toResponse(salvo));
    }

    //get listar aventureiros

    @GetMapping
    @Operation(summary = "Listar aventureiros", description = "Lista aventureiros com filtros opcionais (classe, ativo, nivelMinimo) e paginação")
    public ResponseEntity<List<ResponseAventureiro>> listar(
            @RequestParam(required = false) Classe classe,
            @RequestParam(required = false) Boolean ativo,
            @RequestParam(required = false) @Min(value = 1, message = "O nível deve ser maior ou igual a 1") Integer nivelMinimo,
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "A página deve iniciar em 0") int page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "Size deve ser entre 1 e 50") @Max(value = 50, message = "Size deve ser entre 1 e 50") int size,
            @RequestParam(defaultValue = "nome") String sort,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        Sort sortOrder = Sort.by(Sort.Direction.fromString(direction), sort);
        FilterRequestAventureiro filtro = new FilterRequestAventureiro(classe, ativo, nivelMinimo);
        PaginatedView<Aventureiro> resultado = serviceAventureiro.listar(filtro, PageRequest.of(page, size, sortOrder));

        List<ResponseAventureiro> response = resultado.content()
                .stream()
                .map(mapperAventureiro::toResponse)
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

    //get buscar por nome

    @GetMapping("/busca")
    @Operation(summary = "Buscar aventureiros por nome", description = "Busca LIKE case-insensitive no campo nome")
    public ResponseEntity<List<ResponseAventureiro>> buscarPorNome(
            @RequestParam String nome,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nome") String sort,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        Sort sortOrder = Sort.by(Sort.Direction.fromString(direction), sort);
        PaginatedView<Aventureiro> resultado = serviceAventureiro.buscarPorNome(nome, PageRequest.of(page, size, sortOrder));

        List<ResponseAventureiro> response = resultado.content()
                .stream()
                .map(mapperAventureiro::toResponse)
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

    //get visualização completa

    @GetMapping("/{id}/completo")
    @Operation(summary = "Buscar aventureiro (visão completa)", description = "Inclui totais e dados da última missão participada")
    public ResponseEntity<ResponseAventureiro> buscarCompleto(@PathVariable Long id) {
        return ResponseEntity.ok(serviceAventureiro.buscarCompleto(id));
    }

    //get buscar por id

    @GetMapping("/{id}")
    @Operation(summary = "Buscar aventureiro por id")
    public ResponseEntity<ResponseAventureiro> buscarPorId(@PathVariable Long id) {
        Aventureiro aventureiro = serviceAventureiro.buscarPorId(id);
        return ResponseEntity.ok(mapperAventureiro.toResponse(aventureiro));
    }

    //atualizar aventureiro

    @PatchMapping("/{id}")
    @Operation(summary = "Atualizar aventureiro (parcial)", description = "Atualiza nome, classe e/ou nível — todos opcionais")
    public ResponseEntity<ResponseAventureiro> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody AtualizarAventureiro update
    ) {
        Aventureiro atualizado = serviceAventureiro.atualizar(id, update);
        return ResponseEntity.ok(mapperAventureiro.toResponse(atualizado));
    }

    //encerrar vinculo de aventureiro

    @PatchMapping("/{id}/encerrar-vinculo")
    @Operation(summary = "Encerrar vínculo do aventureiro", description = "Marca como inativo (ativo=false)")
    public ResponseEntity<ResponseAventureiro> encerrarVinculo(@PathVariable Long id) {
        Aventureiro aventureiro = serviceAventureiro.encerrarVinculo(id);
        return ResponseEntity.ok(mapperAventureiro.toResponse(aventureiro));
    }

    //recrutar aventureiro novamente

    @PatchMapping("/{id}/recrutar")
    @Operation(summary = "Recrutar aventureiro", description = "Reativa o vínculo (ativo=true)")
    public ResponseEntity<ResponseAventureiro> recrutarNovamente(@PathVariable Long id) {
        Aventureiro aventureiro = serviceAventureiro.recrutarNovamente(id);
        return ResponseEntity.ok(mapperAventureiro.toResponse(aventureiro));
    }

    //definir companheiro

    @PutMapping("/{id}/companheiro")
    @Operation(summary = "Definir companheiro", description = "Substitui o companheiro atual (se existir)")
    public ResponseEntity<ResponseAventureiro> definirCompanheiro(
            @PathVariable Long id,
            @Valid @RequestBody DefinirCompanheiro dto
    ) {
        Aventureiro aventureiro = serviceAventureiro.definirCompanheiro(id, dto);
        return ResponseEntity.ok(mapperAventureiro.toResponse(aventureiro));
    }

    //remover companheiro

    @PatchMapping("/{id}/remover-companheiro")
    @Operation(summary = "Remover companheiro do aventureiro")
    public ResponseEntity<ResponseAventureiro> removerCompanheiro(@PathVariable Long id) {
        Aventureiro aventureiro = serviceAventureiro.removerCompanheiro(id);
        return ResponseEntity.ok(mapperAventureiro.toResponse(aventureiro));
    }
}