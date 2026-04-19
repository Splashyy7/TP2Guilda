package br.infnet.tp1guilda.configuration;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

    // eu apliquei cache na camada de service para evitar executar várias vezes a mesma
    // consulta pesada em sequência. Criei esta classe CacheConfig para centralizar a configuração
    // do caffeine em um único lugar, deixando o projeto organizado.
    // Com expiração de 60 segundos, o resultado do endpoint é reaproveitado nesse intervalo e
    // depois atualizado automaticamente. Dessa forma, o endpoint continua com a mesma regra de
    // negócio (top 10 dos últimos 15 dias ordenado por índice de prontidão), mas com menos carga
    // no banco e resposta mais rápida.
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("topMissoes15dias");
        cacheManager.setCaffeine(
                Caffeine.newBuilder()
                        .expireAfterWrite(60, TimeUnit.SECONDS)
                        .maximumSize(100)
        );
        return cacheManager;
    }
}