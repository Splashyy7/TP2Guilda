# API-Guilda

API REST de gestão de uma **Guilda de Aventureiros** — controle de aventureiros, missões, companheiros, relatórios analíticos e busca avançada de produtos da loja da guilda via Elasticsearch.

---

## Sumário

- [Escopo & Domínio](#escopo--domínio)
- [Tecnologias](#tecnologias)
- [Arquitetura & Estrutura do Projeto](#arquitetura--estrutura-do-projeto)
- [Modelo de Dados](#modelo-de-dados)
- [Endpoints](#endpoints)
- [Como Rodar](#como-rodar)
- [Configuração](#configuração)
- [Testes](#testes)

---

## Escopo & Domínio

A guilda gerencia três subdomínios:

1. **Aventura** — cadastro de aventureiros (com classe, nível, companheiro animal/mágico opcional), criação e acompanhamento de missões, e participação dos aventureiros nessas missões com papel, recompensa em ouro e marcação de destaque (MVP).
2. **Auditoria/Identidade** — usuários, organizações, papéis (roles), permissões, API keys e trilhas de auditoria (`AuditEntry`). Toda entidade de aventura é vinculada a uma `Organization` e a um `User` que a cadastrou.
3. **Operações & Relatórios** — visão tática consolidada das missões (materialized view `vw_painel_tatico_missao`), ranking de aventureiros por participação/recompensa/destaque e relatórios agregados de missões.
4. **Loja da Guilda (Elasticsearch)** — catálogo de produtos (`guilda_loja`) com busca por nome, descrição, frase exata, fuzzy, multi-campos, faixa de preço, busca avançada combinada e agregações (contagem por categoria/raridade, preço médio, faixas de preço).

### Regras de negócio relevantes

- Aventureiro pode ser **encerrado** (vínculo) e **recrutado novamente**.
- Companheiro é um Value Object (`@Embeddable`) com nome, espécie e lealdade — pode ser definido e removido.
- Status de missão: `PLANEJADA`, `EM_ANDAMENTO`, `CONCLUIDA`, `CANCELADA`.
- Nível de perigo: `BAIXO`, `MEDIO`, `ALTO`, `EXTREMO`.
- Classes: `GUERREIRO`, `MAGO`, `ARQUEIRO`, `CLERIGO`, `LADINO`.
- Espécies de companheiro: `LOBO`, `CORUJA`, `GOLEM`, `DRAGAO_MINIATURA`.
- Painel tático das **top 15 missões** mais relevantes dos últimos 15 dias é cacheado por **24h** via Caffeine.

---

## Tecnologias

| Categoria         | Stack                                                                 |
|-------------------|-----------------------------------------------------------------------|
| Linguagem         | Java 21 (com `--enable-preview`)                                      |
| Framework         | Spring Boot 4.0.3                                                     |
| Web               | Spring Web MVC + Bean Validation (Jakarta)                            |
| Persistência      | Spring Data JPA + Hibernate                                           |
| Banco de Dados    | PostgreSQL (multi-schema: `aventura`, `audit`, `operacoes`)           |
| Busca/Indexação   | Spring Data Elasticsearch + cliente oficial `elasticsearch-java`      |
| Cache             | Spring Cache + Caffeine                                               |
| Build             | Maven (Maven Wrapper incluso)                                         |
| Utilitários       | Lombok                                                                |
| Testes            | Spring Boot Starter Test (JUnit 5)                                    |

---

## Arquitetura & Estrutura do Projeto

Arquitetura em camadas clássica (Controller → Service → Repository → Domain), com mappers dedicados entre DTOs e entidades, tratamento global de exceções e separação por subdomínio.

```
src/main/java/br/infnet/tp1guilda
├── Tp1GuildaApplication.java         # bootstrap (@EnableCaching)
│
├── configuration/
│   └── ConfigCache.java              # CacheManager Caffeine (TTL 1d, max 100)
│
├── controllers/
│   ├── AventureiroController.java    # /aventureiros
│   ├── MissaoController.java         # /missoes
│   ├── ProdutoElasticController.java # /produtos (Elasticsearch)
│   └── RelatorioController.java      # /relatorios
│
├── domain/
│   ├── aventura/                     # Aventureiro, Missao, ParticipacaoMissao, Companheiro (+ enums)
│   ├── audit/                        # User, Organization, Role, Permission, ApiKey, AuditEntry (+ enums)
│   ├── operacoes/                    # PainelTaticoMissaoMV (materialized view)
│   └── elastic/                      # ProdutoLoja (índice guilda_loja)
│
├── dto/
│   ├── aventureiro/                  # CriarAventureiro, AtualizarAventureiro, FilterRequest..., Response...
│   ├── companheiro/                  # DefinirCompanheiro, ResponseCompanheiro
│   ├── missao/                       # FilterRequest..., Response..., Detalhada, Resumo, Participante
│   ├── relatorio/                    # RankingAventureiro, RelatorioMissao
│   ├── elastic/                      # ProdutoResponse, ContagemCampoAggregation, PrecoMedioAggregation, FaixaPreco
│   ├── PaginatedView.java            # envelope paginado genérico
│   └── ErrorResponse.java
│
├── enums/                            # Classe, Especie
│
├── exceptions/
│   ├── GlobalExceptionHandler.java   # @RestControllerAdvice
│   ├── BusinessException.java
│   ├── AventureiroNotFoundException.java
│   └── elastic/ElasticsearchComunicacaoException.java
│
├── mapper/                           # Aventureiro/Companheiro/Missao + elastic/ProdutoDocumentMapper
│
├── repository/
│   ├── aventura/                     # AventureiroRepository, MissaoRepository, ParticipacaoMissaoRepository
│   ├── audit/                        # User, Organization, Role, Permission, ApiKey, AuditEntry
│   └── operacoes/                    # PainelTaticoRepository
│
└── service/
    ├── AventureiroService.java
    ├── MissaoService.java
    ├── PainelTaticoService.java      # @Cacheable("topMissoes15dias")
    ├── RelatorioService.java
    └── elastic/ProdutoDocumentService.java
```

---

## Modelo de Dados

### Schema `aventura`
- **`aventureiros`** — id, organizacao_id, cadastrado_por_id, nome, classe, nivel, ativo, **companheiro_*** (embedded), createdAt, updatedAt.
- **`missoes`** — id, organizacao_id, titulo, nivel_perigo, status, data_inicio, data_termino, createdAt.
- **`participacoes_missao`** — id, missao_id, aventureiro_id, papel, recompensa_ouro, destaque, createdAt. Constraint única `(missao_id, aventureiro_id)`.

### Schema `audit`
- **`organizacoes`**, **`usuarios`** (com `uq_usuarios_email_por_org`), **`roles`**, **`permissions`**, **`user_roles`**, **`api_keys`**, **`audit_entries`**.

### Schema `operacoes`
- **`vw_painel_tatico_missao`** — view materializada com agregados por missão: total de participantes, nível médio da equipe, recompensa total, total de MVPs, índice de prontidão etc. (deve ser criada no banco — DDL não está versionado).

### Índice Elasticsearch `guilda_loja`
- **`ProdutoLoja`** — id, nome (multi-field text + keyword), categoria (keyword), descricao (text), preco (float), raridade (keyword). `createIndex = false` — o índice deve ser criado/populado externamente.

---

## Endpoints

> Convenção: respostas paginadas trazem os headers `X-Total-Count`, `X-Page`, `X-Size`, `X-Total-Pages`.

### `/aventureiros`
| Método  | Path                                   | Descrição                                   |
|---------|----------------------------------------|---------------------------------------------|
| POST    | `/aventureiros`                        | Registrar aventureiro                       |
| GET     | `/aventureiros`                        | Listar com filtros (`classe`, `ativo`, `nivelMinimo`) e paginação |
| GET     | `/aventureiros/busca?nome=`            | Busca por nome (LIKE case-insensitive)      |
| GET     | `/aventureiros/{id}`                   | Buscar por id                               |
| GET     | `/aventureiros/{id}/completo`          | Visão completa (totais + última missão)     |
| PATCH   | `/aventureiros/{id}`                   | Atualização parcial (nome / classe / nível) |
| PATCH   | `/aventureiros/{id}/encerrar-vinculo`  | Inativar                                    |
| PATCH   | `/aventureiros/{id}/recrutar`          | Reativar                                    |
| PUT     | `/aventureiros/{id}/companheiro`       | Definir companheiro                         |
| PATCH   | `/aventureiros/{id}/remover-companheiro` | Remover companheiro                       |

### `/missoes`
| Método | Path                          | Descrição                                                                 |
|--------|-------------------------------|---------------------------------------------------------------------------|
| GET    | `/missoes`                    | Listar com filtros (`status`, `nivelPerigo`, `dataInicio`, `dataFim`)     |
| GET    | `/missoes/{id}`               | Buscar detalhado por id                                                   |
| GET    | `/missoes/top15dias`          | Top missões mais relevantes dos últimos 15 dias (cacheado 24h)            |

### `/relatorios`
| Método | Path                  | Descrição                                          |
|--------|-----------------------|----------------------------------------------------|
| GET    | `/relatorios/ranking` | Ranking de aventureiros (filtros opcionais de data e status de missão) |
| GET    | `/relatorios/missoes` | Relatório agregado de missões por período          |

### `/produtos` (Elasticsearch)
| Método | Path                                  | Descrição                                            |
|--------|---------------------------------------|------------------------------------------------------|
| GET    | `/produtos/busca/nome?termo=`         | Match no campo `nome`                                |
| GET    | `/produtos/busca/descricao?termo=`    | Match em `descricao`                                 |
| GET    | `/produtos/busca/frase?termo=`        | Match phrase exata em `descricao`                    |
| GET    | `/produtos/busca/fuzzy?termo=`        | Fuzzy AUTO em `nome`                                 |
| GET    | `/produtos/busca/multicampos?termo=`  | Multi-match em `nome` e `descricao`                  |
| GET    | `/produtos/busca/com-filtro?termo=&categoria=` | Match em descricao + filtro por categoria  |
| GET    | `/produtos/busca/faixa-preco?min=&max=` | Range em `preco`                                   |
| GET    | `/produtos/busca/avancada?categoria=&raridade=&min=&max=` | Booleana combinada               |
| GET    | `/produtos/agregacoes/por-categoria`  | Contagem de docs por categoria                       |
| GET    | `/produtos/agregacoes/por-raridade`   | Contagem de docs por raridade                        |
| GET    | `/produtos/agregacoes/preco-medio`    | Avg do campo `preco`                                 |
| GET    | `/produtos/agregacoes/faixas-preco`   | Range agregado (<100, 100–300, 300–700, >700)        |

### Tratamento de erros (formato padrão)
```json
{
  "status": 400,
  "message": "...",
  "timestamp": "2026-05-03T10:00:00"
}
```
| Exception                          | HTTP                  |
|------------------------------------|-----------------------|
| `AventureiroNotFoundException`     | 404 Not Found         |
| `BusinessException`                | 400 Bad Request       |
| `MethodArgumentNotValidException`  | 400 Bad Request       |
| `HttpMessageNotReadableException`  | 400 Bad Request       |
| `ElasticsearchComunicacaoException`| 503 Service Unavailable |
| `Exception` (genérico)             | 500 Internal Server Error |

---

## Como Rodar

### Pré-requisitos

- **JDK 21**
- **Maven 3.9+** (ou usar o wrapper `./mvnw`)
- **PostgreSQL** rodando em `localhost:5432`
- **Elasticsearch 8.x** rodando em `localhost:9200`

### 1. Subir o PostgreSQL (imagem do TP)

Este projeto usa a imagem PostgreSQL pré-configurada disponibilizada para o TP2:

```powershell
docker pull leogloriainfnet/postgres-tp2-spring:2.0-win

docker run -d --name api-guilda-postgres `
  -e POSTGRES_PASSWORD=jpguild `
  -p 5432:5432 `
  leogloriainfnet/postgres-tp2-spring:2.0-win
```

> A senha `jpguild` casa com o `spring.datasource.password` em `application.properties`. Se você usar outra senha aqui, ajuste o `application.properties` (ou exporte via env var) também.

### 2. Subir o Elasticsearch (imagem do TP)

```powershell
docker pull leogloriainfnet/elastic-tp2-spring:1.0-alternativo

docker run -d --name api-guilda-elastic `
  -e discovery.type=single-node `
  -e xpack.security.enabled=false `
  -p 9200:9200 `
  leogloriainfnet/elastic-tp2-spring:1.0-alternativo
```

O índice `guilda_loja` é declarado com `createIndex = false` em `ProdutoLoja.java`. A imagem do TP já vem com o índice e os documentos populados — não precisa criar/popular manualmente.

### 2b. Alternativa: subir tudo via docker-compose

Existe um `docker-compose.yml` pronto em `src/main/docker/` que sobe Postgres + Elasticsearch com volumes persistentes e healthchecks:

```powershell
docker compose -f src/docker/docker-compose.yml up -d              # sobe tudo
docker compose -f src/docker/docker-compose.yml ps                 # status / health
docker compose -f src/docker/docker-compose.yml logs -f postgres   # logs de um servico
docker compose -f src/docker/docker-compose.yml down               # para tudo (mantem volumes)
docker compose -f src/docker/docker-compose.yml down -v            # para e APAGA os volumes
```

> **Atenção sobre os volumes:** na primeira subida, o Docker copia os dados da imagem (já populados pelo TP) para dentro do volume nomeado, então o conteúdo do Elastic e o seed do Postgres são preservados em restarts. Se você fizer `down -v`, perde tudo e a próxima subida volta para o estado original da imagem.

### 3. Preparar o banco

Os schemas `aventura`, `audit` e `operacoes` precisam existir antes do primeiro start (o Hibernate cria as tabelas com `ddl-auto=update`, mas **não cria schemas**):

```sql
CREATE SCHEMA IF NOT EXISTS aventura;
CREATE SCHEMA IF NOT EXISTS audit;
CREATE SCHEMA IF NOT EXISTS operacoes;
```

A view materializada `operacoes.vw_painel_tatico_missao` deve ser criada manualmente — o DDL **não está versionado** neste repositório.

### 4. Popular o banco com dados de exemplo (seed.sql)

Depois de rodar a aplicação **pelo menos uma vez** (para o `ddl-auto=update` criar as tabelas e sequences), execute o script `src/main/resources/seed.sql` no console SQL do PostgreSQL (psql, DataGrip, IntelliJ Database Tools, etc).

O seed insere:

- 3 organizações, 7 permissions, 6 roles + vínculos
- 6 usuários com roles atribuídas
- 2 API keys
- 8 missões cobrindo todos os `StatusMissao` e `NivelPerigo`
- 14 aventureiros (todas as `Classe` e `Especie` representadas, com companheiros)
- 21 participações em missão (incluindo MVPs para o ranking)
- 7 entradas de auditoria

Características do script:

- IDs começam em **1001** para conviver com qualquer dado pré-existente.
- `audit.permissions` usa `ON CONFLICT (code) DO NOTHING` (idempotente — codes são unique global).
- `audit.role_permissions` resolve `permission_id` por `code`, então o vínculo sai certo mesmo que as permissions já existissem com IDs diferentes.
- Ao final, ajusta as sequences via `setval(..., GREATEST(MAX(id), 1100))` para o próximo INSERT da aplicação não colidir.
- Tudo dentro de um único `BEGIN/COMMIT`.

Se quiser começar do zero antes de rodar o seed:

```sql
TRUNCATE
  aventura.participacoes_missao, aventura.aventureiros, aventura.missoes,
  audit.audit_entries, audit.user_roles, audit.role_permissions,
  audit.api_keys, audit.usuarios, audit.roles, audit.permissions, audit.organizacoes
RESTART IDENTITY CASCADE;
```

### 5. Rodar a aplicação

```powershell
# Windows (PowerShell)
.\mvnw.cmd spring-boot:run

# Linux/macOS
./mvnw spring-boot:run
```

Ou empacotando:

```powershell
.\mvnw.cmd clean package
java --enable-preview -jar target\TP2Guilda-0.0.1-SNAPSHOT.jar
```

A API sobe em **http://localhost:8080**.

---

## Configuração

`src/main/resources/application.properties`:

```properties
spring.application.name=TP1Guilda

spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=jpguild
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

spring.elasticsearch.uris=http://localhost:9200
```

> **Atenção:** as credenciais default estão no arquivo apenas para facilitar o desenvolvimento local. Para qualquer ambiente que não seja a sua máquina, externalize via variáveis de ambiente / `application-{profile}.properties`.

---

## Testes

```powershell
.\mvnw.cmd test
```

Testes presentes:
- `Tp1GuildaApplicationTests` — smoke test de carga do contexto.
- `audit/AuditMappingDataJpaTest` — `@DataJpaTest` validando o mapeamento das entidades de auditoria.