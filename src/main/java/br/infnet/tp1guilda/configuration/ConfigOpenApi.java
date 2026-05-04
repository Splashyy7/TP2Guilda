package br.infnet.tp1guilda.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ConfigOpenApi {

    @Bean
    public OpenAPI apiGuildaOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("API-Guilda")
                        .version("2.0.0")
                        .description("""
                                API REST de gestão de uma Guilda de Aventureiros.

                                Cobre cadastro de aventureiros, missões, companheiros, relatórios analíticos
                                (ranking, painel tático cacheado) e busca avançada de produtos da loja via Elasticsearch.
                                """)
                        .contact(new Contact()
                                .name("João Pedro Oliveira Gritz de Oliveira")
                                .email("jpedrooliveiragritz@gmail.com"))
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local")
                ));
    }
}