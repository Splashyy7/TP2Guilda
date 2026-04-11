package br.infnet.tp1guilda.service;

import br.infnet.tp1guilda.domain.audit.Organization;
import br.infnet.tp1guilda.domain.audit.User;
import br.infnet.tp1guilda.domain.aventura.Aventureiro;
import br.infnet.tp1guilda.domain.aventura.Companheiro;
import br.infnet.tp1guilda.domain.aventura.Missao;
import br.infnet.tp1guilda.domain.aventura.ParticipacaoMissao;
import br.infnet.tp1guilda.dto.PaginatedView;
import br.infnet.tp1guilda.dto.aventureiro.AtualizarAventureiro;
import br.infnet.tp1guilda.dto.aventureiro.CriarAventureiro;
import br.infnet.tp1guilda.dto.aventureiro.FilterRequestAventureiro;
import br.infnet.tp1guilda.dto.aventureiro.ResponseAventureiro;
import br.infnet.tp1guilda.dto.companheiro.DefinirCompanheiro;
import br.infnet.tp1guilda.exceptions.AventureiroNotFoundException;
import br.infnet.tp1guilda.exceptions.BusinessException;
import br.infnet.tp1guilda.mapper.AventureiroMapper;
import br.infnet.tp1guilda.repository.audit.OrganizationRepository;
import br.infnet.tp1guilda.repository.audit.UserRepository;
import br.infnet.tp1guilda.repository.aventura.AventureiroRepository;
import br.infnet.tp1guilda.repository.aventura.ParticipacaoMissaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AventureiroService {

    private final AventureiroRepository repositoryAventureiro;
    private final ParticipacaoMissaoRepository participacaoMissaoRepository;
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final AventureiroMapper mapperAventureiro;

    //Criar aventureiro

    public Aventureiro criar(CriarAventureiro dto) {
        Organization organizacao = organizationRepository.findById(dto.organizacaoId())
                .orElseThrow(() -> new BusinessException("Organização não encontrada com id: " + dto.organizacaoId()));

        User usuario = userRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new BusinessException("Usuário não encontrado com id: " + dto.usuarioId()));

        Aventureiro aventureiro = mapperAventureiro.toEntity(dto, organizacao, usuario);
        return repositoryAventureiro.save(aventureiro);
    }

    public Aventureiro buscarPorId(Long id) {
        return repositoryAventureiro.findById(id)
                .orElseThrow(() -> new AventureiroNotFoundException(id));
    }

    //update aventureiro

    public Aventureiro atualizar(Long id, AtualizarAventureiro update) {

        Aventureiro aventureiro = buscarPorId(id);

        if (update.nome() != null) {
            if (update.nome().isBlank()) {
                throw new BusinessException("O nome do aventureiro não pode ser vazio.");
            }
            aventureiro.alterarNome(update.nome());
        }

        if (update.classe() != null) {
            aventureiro.alterarClasse(update.classe());
        }

        if (update.nivel() != null) {
            aventureiro.alterarNivel(update.nivel());
        }

        return repositoryAventureiro.save(aventureiro);
    }

    //encerrar vinculo de aventureiro

    public Aventureiro encerrarVinculo(Long id) {
        Aventureiro aventureiro = buscarPorId(id);

        if (!aventureiro.getAtivo()) {
            throw new BusinessException("O aventureiro já está inativo.");
        }

        aventureiro.encerrarVinculo();
        return repositoryAventureiro.save(aventureiro);
    }

    //recrutar aventureiro novamente

    public Aventureiro recrutarNovamente(Long id) {
        Aventureiro aventureiro = buscarPorId(id);

        if (aventureiro.getAtivo()) {
            throw new BusinessException("O aventureiro já está ativo.");
        }

        aventureiro.recrutar();
        return repositoryAventureiro.save(aventureiro);
    }

    //remover companheiro

    public Aventureiro removerCompanheiro(Long id) {
        Aventureiro aventureiro = buscarPorId(id);

        if (aventureiro.getCompanheiro() == null) {
            throw new BusinessException("O aventureiro não possui companheiro para remover.");
        }

        aventureiro.removerCompanheiro();
        return repositoryAventureiro.save(aventureiro);
    }


    //listar aventureiros

    public PaginatedView<Aventureiro> listar(FilterRequestAventureiro filtro, Pageable pageable) {
        Page<Aventureiro> resultado = repositoryAventureiro.findWithFilter(
                filtro.classe(), filtro.ativo(), filtro.nivelMinimo(), pageable
        );
        return new PaginatedView<>(pageable.getPageNumber(), pageable.getPageSize(), (int) resultado.getTotalElements(), resultado.getContent());
    }

    //buscar por nome

    public PaginatedView<Aventureiro> buscarPorNome(String nome, Pageable pageable) {
        Page<Aventureiro> resultado = repositoryAventureiro.findByNomeContaining(nome, pageable);
        return new PaginatedView<>(pageable.getPageNumber(), pageable.getPageSize(), (int) resultado.getTotalElements(), resultado.getContent());
    }

    //visualização completa

    public ResponseAventureiro buscarCompleto(Long id) {
        Aventureiro aventureiro = buscarPorId(id);
        long totalParticipacoes = participacaoMissaoRepository.countByAventureiroId(id);
        Missao ultimaMissao = participacaoMissaoRepository.findUltimaByAventureiroId(id)
                .map(ParticipacaoMissao::getMissao)
                .orElse(null);
        return mapperAventureiro.toResponseCompleto(aventureiro, totalParticipacoes, ultimaMissao);
    }

    //definir companheiro

    public Aventureiro definirCompanheiro(Long id, DefinirCompanheiro dto) {
        Aventureiro aventureiro = buscarPorId(id);

        Companheiro companheiro = new Companheiro(
                dto.nome(),
                dto.especie(),
                dto.lealdade()
        );

        aventureiro.definirCompanheiro(companheiro);

        return repositoryAventureiro.save(aventureiro);
    }
}