package br.infnet.tp1guilda.dto.relatorio;

public record RankingAventureiro(
        Long aventureiroId,
        String nomeAventureiro,
        long totalParticipacoes,
        double somaRecompensas,
        long totalDestaques
) {
}