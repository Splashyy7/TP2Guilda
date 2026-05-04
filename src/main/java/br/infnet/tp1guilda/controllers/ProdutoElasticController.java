package br.infnet.tp1guilda.controllers;

import br.infnet.tp1guilda.dto.elastic.ContagemCampoAggregation;
import br.infnet.tp1guilda.dto.elastic.FaixaPreco;
import br.infnet.tp1guilda.dto.elastic.PrecoMedioAggregation;
import br.infnet.tp1guilda.dto.elastic.ProdutoResponse;
import br.infnet.tp1guilda.mapper.elastic.ProdutoDocumentMapper;
import br.infnet.tp1guilda.service.elastic.ProdutoDocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/produtos")
@RequiredArgsConstructor
@Tag(name = "Produtos (Elasticsearch)", description = "Busca textual e agregações no índice guilda_loja")
public class ProdutoElasticController {

    private final ProdutoDocumentService produtoDocumentService;

    @GetMapping("/busca/nome")
    @Operation(summary = "Busca por nome (match)")
    public List<ProdutoResponse> buscarPorNome(@RequestParam("termo") String termo) {
        return produtoDocumentService.buscarPorNome(termo).stream()
                .map(ProdutoDocumentMapper::toResponse)
                .toList();
    }

    @GetMapping("/busca/descricao")
    @Operation(summary = "Busca por descrição (match)")
    public List<ProdutoResponse> buscarPorDescricao(@RequestParam("termo") String termo) {
        return produtoDocumentService.buscarPorDescricao(termo).stream()
                .map(ProdutoDocumentMapper::toResponse)
                .toList();
    }

    @GetMapping("/busca/frase")
    @Operation(summary = "Busca por frase exata (match phrase)")
    public List<ProdutoResponse> buscarPorFrase(@RequestParam("termo") String termo) {
        return produtoDocumentService.buscarPorFraseExata(termo).stream()
                .map(ProdutoDocumentMapper::toResponse)
                .toList();
    }

    @GetMapping("/busca/fuzzy")
    @Operation(summary = "Busca fuzzy", description = "Tolerante a typos (fuzzy AUTO no campo nome)")
    public List<ProdutoResponse> buscarPorFuzzy(@RequestParam("termo") String termo) {
        return produtoDocumentService.buscarPorNomeComTolerancia(termo).stream()
                .map(ProdutoDocumentMapper::toResponse)
                .toList();
    }

    @GetMapping("/busca/multicampos")
    @Operation(summary = "Busca multi-campos", description = "Multi-match em nome e descricao")
    public List<ProdutoResponse> buscarMulticampos(@RequestParam("termo") String termo) {
        return produtoDocumentService.buscarPorNomeEDescricao(termo).stream()
                .map(ProdutoDocumentMapper::toResponse)
                .toList();
    }

    @GetMapping("/busca/com-filtro")
    @Operation(summary = "Busca com filtro", description = "Match em descricao com filtro por categoria")
    public List<ProdutoResponse> buscarComFiltro(
            @RequestParam("termo") String termo,
            @RequestParam("categoria") String categoria
    ) {
        return produtoDocumentService.buscarPorDescricaoECategoria(termo, categoria).stream()
                .map(ProdutoDocumentMapper::toResponse)
                .toList();
    }

    @GetMapping("/busca/faixa-preco")
    @Operation(summary = "Busca por faixa de preço (range)")
    public List<ProdutoResponse> buscarPorFaixaPreco(
            @RequestParam("min") double min,
            @RequestParam("max") double max
    ) {
        return produtoDocumentService.buscarPorFaixaPreco(min, max).stream()
                .map(ProdutoDocumentMapper::toResponse)
                .toList();
    }

    @GetMapping("/busca/avancada")
    @Operation(summary = "Busca avançada (booleana combinada)", description = "Filtra simultaneamente por categoria, raridade e faixa de preço")
    public List<ProdutoResponse> buscaAvancada(
            @RequestParam("categoria") String categoria,
            @RequestParam("raridade") String raridade,
            @RequestParam("min") double min,
            @RequestParam("max") double max
    ) {
        return produtoDocumentService.buscaCombinada(categoria, raridade, min, max).stream()
                .map(ProdutoDocumentMapper::toResponse)
                .toList();
    }

    @GetMapping("/agregacoes/por-categoria")
    @Operation(summary = "Agregação: contagem por categoria")
    public List<ContagemCampoAggregation> porCategoria() {
        return produtoDocumentService.quantidadeProdutosPorCampo("categoria");
    }

    @GetMapping("/agregacoes/por-raridade")
    @Operation(summary = "Agregação: contagem por raridade")
    public List<ContagemCampoAggregation> porRaridade() {
        return produtoDocumentService.quantidadeProdutosPorCampo("raridade");
    }

    @GetMapping("/agregacoes/preco-medio")
    @Operation(summary = "Agregação: preço médio")
    public PrecoMedioAggregation precoMedio() {
        return produtoDocumentService.precoMedioProdutos();
    }

    @GetMapping("/agregacoes/faixas-preco")
    @Operation(summary = "Agregação: faixas de preço", description = "Buckets: <100, 100–300, 300–700, >700")
    public List<FaixaPreco> faixasPreco() {
        return produtoDocumentService.agruparEmFaixaPreco();
    }
}
