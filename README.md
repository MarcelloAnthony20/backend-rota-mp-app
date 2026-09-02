# Template Backend Spring Boot — MPMS/DID

Repositorio template para novos microsservicos Java Spring Boot da Divisao de
Desenvolvimento (DID/DESIN) do MPMS.

## Stack

| Area | Tecnologia |
|------|-----------|
| Linguagem | Java 17 (Temurin) |
| Build | Spring Boot (via MicroServiceParent 0.0.10) |
| Seguranca | Keycloak / MicroServiceSidecar + permissionamento |
| Dados | Spring Data JPA + PostgreSQL (ou Oracle/MySQL) |
| Validacao | Jakarta Validation |
| Produtividade | Lombok + MapStruct |
| Documentacao | SpringDoc OpenAPI (Swagger) |
| Resiliencia | Resilience4j |
| Testes | JUnit 5 + Mockito |
| Infraestrutura | Docker + OpenShift + JFrog Artifactory |
| Pipeline | Azure DevOps (SonarQube + Trivy) |

> O `MicroServiceParent 0.0.10` traz o Spring Boot 4.x. Dependencias e
> anotacoes de teste seguem o padrao do Boot 4 (ex.: `@MockitoBean` no lugar
> de `@MockBean`, slice `@WebMvcTest` via `spring-boot-starter-webmvc-test`).

---

## Checklist de Inicializacao do Projeto

Use este template como ponto de partida. Siga os passos na ordem:

1. **Clonar** este repositorio
2. **Renomear** pacotes: `br.mp.mpms.exemploservico` → `br.mp.mpms.[seu-servico]`
3. **Renomear** a classe principal `ExemploServicoApplication`
4. **Ajustar** `pom.xml` (`artifactId`, `name`, `description`)
5. **Ajustar** `application.yml` (`context-path`, `datasource`, `keycloak`)
6. **Renomear** a pasta `.harness/` para o nome da ferramenta de codificacao
   agentica utilizada pelo time (ex: `.opencode` para OpenCode, `.claude` para
   Claude Code, `.codex` para OpenAI Codex, `.kiro` para Kiro, etc.)
7. **Configurar** o arquivo `.env` com as variaveis de ambiente (ver secao
   Configuracao)
8. **Build e validacao**: `mvn clean package && mvn test`

---

## Pre-Requisitos

| Ferramenta | Versao |
|-----------|--------|
| JDK | 17 (Temurin) |
| Maven | 3.9+ |
| Docker | Ultima estavel (Desktop com Linux containers) |
| Git | 2.40+ |

---

## Passo a Passo — Rodar do Zero

Este passo a passo foi validado de ponta a ponta (health `UP`, endpoint seguro
retornando `401`). Escolha **uma** das trilhas: A) local com Maven, ou B) Docker.

### Pre-requisito comum: credenciais do Artifactory

O build baixa dependencias do Artifactory institucional. Defina as variaveis
(peca o token ao time de infra) — sem elas o `mvn`/`docker build` nao resolve o
parent POM:

```bash
export ARTIFACTORY_URL=https://artifactory.mpms.mp.br/artifactory
export ARTIFACTORY_USER=seu.usuario
export ARTIFACTORY_PASSWORD=seu-token
```

> Fora da rede do MPMS, o acesso ao Artifactory/Maven Central passa pelo WAF.
> No build Docker isso ja e tratado importando o `FAC.crt` (ver Trilha B).
> Para o `mvn` local, o CA do WAF precisa estar no truststore do seu JDK.

---

### Trilha A — Rodar local com Maven

**1. Subir um PostgreSQL local**

```bash
docker run -d --name postgres-local \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=exemplo_db \
  -p 5432:5432 \
  postgres:16-alpine

# conferir que aceita conexoes
docker exec postgres-local pg_isready -U postgres
```

**2. Definir as variaveis de ambiente da aplicacao**

```bash
export DB_URL=jdbc:postgresql://localhost:5432/exemplo_db
export DB_USERNAME=postgres
export DB_PASSWORD=postgres
export DB_SCHEMA=public
export KEYCLOAK_ISSUER_URI=https://keycloak-dev.mpms.mp.br/realms/mpms
export KEYCLOAK_JWKS_URI=https://keycloak-dev.mpms.mp.br/realms/mpms/protocol/openid-connect/certs
export SERVER_PORT=8080
```

(ou coloque tudo em um arquivo `.env` e rode `set -a && source .env && set +a`)

**3. Compilar**

```bash
mvn clean package -DskipTests
```

**4. Executar**

Como o projeto usa `ddl-auto=validate` e o banco esta vazio, gere o schema com
`update` na primeira execucao:

```bash
mvn spring-boot:run \
  -Dspring-boot.run.jvmArguments="-Dspring.jpa.hibernate.ddl-auto=update"
```

**5. Validar**

```bash
# deve responder {"status":"UP"}
curl http://localhost:8080/api/exemplo-servico/actuator/health

# endpoint de negocio sem token deve retornar 401
curl -i http://localhost:8080/api/exemplo-servico/api/v1/pessoas

# Swagger UI e contrato OpenAPI
open http://localhost:8080/api/exemplo-servico/swagger-ui/index.html
curl http://localhost:8080/api/exemplo-servico/v3/api-docs
```

**6. Parar**

`Ctrl+C` no terminal do `mvn`. Para remover o banco: `docker rm -f postgres-local`.

---

### Trilha B — Rodar com Docker

**1. Subir o PostgreSQL** (igual ao passo A.1).

**2. Build da imagem** (multi-arch; funciona em Mac e Windows):

```bash
docker build \
  --build-arg ARTIFACTORY_USER=$ARTIFACTORY_USER \
  --build-arg ARTIFACTORY_PASSWORD=$ARTIFACTORY_PASSWORD \
  -t exemplo-servico:1.0.0 .
```

**3. Executar o container**

No Docker Desktop (Mac/Windows) use `host.docker.internal` para alcancar o
Postgres do host. `DB_USERNAME`/`DB_PASSWORD` devem bater com o banco alvo.

```bash
docker run -d --name exemplo-servico -p 8080:8080 \
  -e DB_URL="jdbc:postgresql://host.docker.internal:5432/exemplo_db" \
  -e DB_USERNAME=postgres \
  -e DB_PASSWORD=postgres \
  -e DB_SCHEMA=public \
  -e KEYCLOAK_ISSUER_URI=https://keycloak-dev.mpms.mp.br/realms/mpms \
  -e KEYCLOAK_JWKS_URI=https://keycloak-dev.mpms.mp.br/realms/mpms/protocol/openid-connect/certs \
  -e SPRING_JPA_HIBERNATE_DDL_AUTO=update \
  exemplo-servico:1.0.0
```

**4. Validar**

```bash
docker logs -f exemplo-servico   # aguardar "Started ExemploServicoApplication"
curl http://localhost:8080/api/exemplo-servico/actuator/health
```

**5. Parar**

```bash
docker rm -f exemplo-servico
docker rm -f postgres-local   # se quiser remover o banco tambem
```

> Se algo falhar, consulte a secao **Solucao de Problemas** no final deste
> documento (porta em uso, database inexistente, senha incorreta, etc.).

---

## Configuracao

### 1. Variaveis de ambiente

Crie um arquivo `.env` na raiz (ou configure no VS Code Run/Debug):

```bash
DB_URL=jdbc:postgresql://localhost:5432/exemplo_db
DB_USERNAME=postgres
DB_PASSWORD=postgres
DB_SCHEMA=public
KEYCLOAK_ISSUER_URI=https://keycloak-dev.mpms.mp.br/realms/mpms
KEYCLOAK_JWKS_URI=https://keycloak-dev.mpms.mp.br/realms/mpms/protocol/openid-connect/certs
EUREKA_URL=http://localhost:8761/eureka
SERVER_PORT=8080
ARTIFACTORY_USER=seu.usuario
ARTIFACTORY_PASSWORD=seu-token
ARTIFACTORY_URL=https://artifactory.mpms.mp.br/artifactory
```

> **Ambientes**: separe as configuracoes por ambiente (local, desenvolvimento,
> homologacao, producao). Nao reutilize dados produtivos localmente.

### 2. Maven (JFrog Artifactory)

O arquivo `.mvn/settings.xml` (versionado no projeto) configura os repositorios
do Artifactory e le as credenciais das variaveis de ambiente `ARTIFACTORY_USER`,
`ARTIFACTORY_PASSWORD` e `ARTIFACTORY_URL`. Nenhum segredo fica no arquivo.

Ele tambem inclui o **Maven Central como fallback** para artefatos que ainda
nao estao disponiveis no Artifactory (ex.: `log4j-bom`, transitiva do Spring
Boot 4.x). O acesso ao Central passa pelo WAF institucional — veja a secao de
Docker sobre o certificado `FAC.crt`.

### 3. Banco de dados local

```bash
docker run -d \
  --name postgres-local \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=exemplo_db \
  -p 5432:5432 \
  postgres:16-alpine
```

---

## Execucao

```bash
# Build
mvn clean install

# Executar
mvn spring-boot:run

# Acessar
http://localhost:8080/api/exemplo-servico/actuator/health
http://localhost:8080/api/exemplo-servico/swagger-ui/index.html
http://localhost:8080/api/exemplo-servico/v3/api-docs
```

> **Schema do banco:** o projeto usa `spring.jpa.hibernate.ddl-auto=validate`,
> ou seja, o schema deve existir previamente. Para desenvolvimento local com um
> banco vazio, gere o schema com:
>
> ```bash
> mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.jpa.hibernate.ddl-auto=update"
> ```

---

## Testes

```bash
# Testes unitarios
mvn test

# Testes com cobertura
mvn test jacoco:report
# Relatorio em: target/site/jacoco/index.html
```

### Convencoes de teste

- **Nomenclatura obrigatoria**: `deve_[comportamento]_quando_[condicao]`
- **Service**: JUnit 5 + Mockito (`@ExtendWith(MockitoExtension.class)`)
- **Controller**: `@WebMvcTest` + `MockMvc`

### Cobertura minima esperada

| Tipo de mudanca | Cobertura |
|----------------|-----------|
| Service (regra de negocio) | 70% |
| Controller | 50% |
| Pipeline (SonarQube Quality Gate) | >= 60% |
| DTO / MapStruct | Mapeamento de todos os campos |
| Validacao Jakarta | Casos validos, invalidos e limites |
| Repository | Consulta parametrizada, retorno esperado |

### Checklist de pronto

Antes de abrir PR, verifique:

- [ ] Implementacao cobre os criterios de aceite da spec de backend
- [ ] Spec de frontend correlata foi lida (somente leitura) quando existir
- [ ] Sem imports wildcard (`*`) ou `@SneakyThrows`
- [ ] Controller nao acessa Repository diretamente
- [ ] Service retorna apenas DTOs, nunca entidades JPA
- [ ] Autenticacao via Keycloak/Sidecar; autorizacao via permissionamento
- [ ] Nenhum segredo, token ou PII em codigo/logs
- [ ] `application.yml` usa apenas variaveis de ambiente, sem hardcode
- [ ] `GlobalExceptionHandler` presente; erros no formato RFC 7807 (`ProblemDetail`)
- [ ] Testes passam (`mvn test`)
- [ ] Nenhuma alteracao fora do escopo solicitado
- [ ] Nenhum arquivo em `docs/specs/frontend/` foi alterado

---

## Build Docker

O `Dockerfile` usa multi-stage com imagens **multi-arch** (`maven:3.9-eclipse-temurin-17`
e `eclipse-temurin:17-jre`), funcionando tanto em **Mac (Apple Silicon/Intel)**
quanto em **Windows** (Docker Desktop em modo Linux containers).

O CA do WAF institucional (`FAC.crt`, na raiz do projeto) e importado no
truststore do JDK durante o build, permitindo acessar o Maven Central atras do
WAF. As credenciais do Artifactory sao passadas como build-args.

```bash
docker build \
  --build-arg ARTIFACTORY_USER=seu.usuario \
  --build-arg ARTIFACTORY_PASSWORD=seu-token \
  -t exemplo-servico:1.0.0 .
```

Executar (no Docker Desktop use `host.docker.internal` para alcancar o Postgres
do host; no Linux puro adicione `--add-host=host.docker.internal:host-gateway`):

```bash
docker run -p 8080:8080 --env-file .env \
  -e DB_URL="jdbc:postgresql://host.docker.internal:5432/exemplo_db" \
  -e DB_USERNAME=postgres \
  -e DB_PASSWORD=<a-senha-real-do-seu-postgres> \
  -e SPRING_JPA_HIBERNATE_DDL_AUTO=update \
  exemplo-servico:1.0.0
```

> **Banco de dados (atencao):** `DB_URL`, `DB_USERNAME` e `DB_PASSWORD` precisam
> corresponder EXATAMENTE ao Postgres alvo (nome do database, usuario e senha).
> Erros comuns ao rodar:
> - `FATAL: database "exemplo_db" does not exist` → o database nao existe. Crie:
>   ```bash
>   docker exec -it <container-postgres> psql -U postgres -c "CREATE DATABASE exemplo_db;"
>   ```
> - `FATAL: password authentication failed for user "postgres"` → a senha nao
>   confere. Descubra a senha configurada no seu Postgres:
>   ```bash
>   docker exec <container-postgres> sh -c 'echo $POSTGRES_PASSWORD'
>   ```
> - Como o projeto usa `ddl-auto=validate`, em banco vazio passe
>   `-e SPRING_JPA_HIBERNATE_DDL_AUTO=update` para o Hibernate criar a tabela.
> - No Docker Desktop, `localhost` dentro do container e o proprio container;
>   use `host.docker.internal` para alcancar o Postgres do host.

> **Seguranca:** build-args ficam no historico da imagem. Em pipelines de
> producao, prefira BuildKit secrets (`RUN --mount=type=secret,...`) em vez de
> passar o token do Artifactory por `--build-arg`.

### Regras de container

- Container **NUNCA** roda como root — o `Dockerfile` deve definir `USER` sem privilegios
- Multi-stage build obrigatorio (estagio de build separado do estagio de producao)
- `HEALTHCHECK` obrigatorio no `Dockerfile`
- `.dockerignore` obrigatorio (excluir `.git`, `.env`, `target/`)
- Imagens tagueadas com versao semantica, nunca `:latest` em producao
- Registry exclusivo: JFrog Artifactory. Proibido pull direto do Docker Hub

---

## Estrutura de Pacotes

```
br.mp.mpms.exemploservico/
├── ExemploServicoApplication.java
├── v1/
│   ├── controller/    ← Endpoints REST
│   ├── service/       ← Regras de negocio
│   ├── repository/    ← Acesso a dados (JPA)
│   └── model/
│       ├── entity/    ← Entidades JPA
│       └── dto/       ← Records Request/Response
└── infra/
    ├── config/        ← Security, OpenAPI, CORS
    ├── exception/     ← GlobalExceptionHandler, excecoes custom
    └── interceptor/   ← Filtros do Sidecar
```

---

## Endpoints Disponiveis

| Metodo | Path | Descricao |
|--------|------|-----------|
| POST | /api/v1/pessoas | Criar pessoa |
| GET | /api/v1/pessoas/{id} | Buscar por ID |
| GET | /api/v1/pessoas?nome=X | Listar/buscar por nome |

Todos os endpoints de negocio exigem token JWT (Keycloak). Liberados sem auth:
`/actuator/health`, `/actuator/info`, `/swagger-ui/**`, `/v3/api-docs/**`.

---

## Documentacao SDD

O projeto segue **Specification-Driven Development (SDD)**. A documentacao esta
organizada em:

| Artefato | Finalidade |
|----------|-----------|
| `docs/specs/ROADMAP.md` | Fases, prioridades e dependencias globais |
| `docs/specs/backend/ROADMAP.md` | Ordem de implementacao das specs de backend |
| `docs/specs/backend/<id>/spec.md` | Domínio, dados, regras, seguranca e contratos |
| `docs/specs/frontend/<id>/spec.md` | Fluxos de UI, rotas, campos (**somente leitura**) |
| `docs/specs/REQUIREMENTS-CATALOG.md` | Requisitos transversais e regras globais |
| `docs/specs/OPEN-QUESTIONS.md` | Duvidas e decisoes bloqueantes |
| `docs/adr/*.md` | Decisoes arquiteturais |
| `docs/DESIGN.md` | Design system do frontend |

> **Importante**: o diretorio `docs/specs/frontend/` e **somente leitura**.
> Nao crie, edite ou remova arquivos nesse diretorio.

### Hierarquia de decisao (ordem de precedencia)

1. Especificacao de backend (`docs/specs/backend/<id>/spec.md`)
2. ADRs aceitos (`docs/adr/`)
3. Requisitos globais (`docs/specs/REQUIREMENTS-CATALOG.md`)
4. Roadmaps (`docs/specs/ROADMAP.md`)
5. `AGENTS.md` (padroes tecnicos e regras do projeto)
6. Spec de frontend — somente leitura, para contratos de API

**Arquivos `backlog.csv` nao fazem parte da documentacao SDD.** Nao os use como
fonte de requisitos, escopo ou prioridade.

---

## Regras do Projeto

### Camadas

- Controller **NUNCA** acessa Repository diretamente
- Service **NUNCA** retorna entidades JPA — sempre DTOs
- Repository **NUNCA** contem logica de negocio

### Seguranca

- Autenticacao **exclusivamente** via Keycloak/Sidecar. NUNCA login proprio
- Autorizacao via microsservico `_git/permissionamento`. NUNCA roles hardcoded
- Queries **SEMPRE** parametrizadas (`@Query` com `:param` ou Spring Data). NUNCA concatenacao SQL
- Logs **NUNCA** contem CPF, senhas, tokens JWT, IPs ou PII. CPF mascarado: `***.***.***-XX`
- CORS apenas `*.mpms.mp.br`. NUNCA `Access-Control-Allow-Origin: *`
- Headers de seguranca obrigatorios: `X-Content-Type-Options: nosniff`, `X-Frame-Options: DENY`, `Strict-Transport-Security: max-age=31536000`, `Content-Security-Policy: default-src 'self'`
- `spring.jpa.hibernate.ddl-auto=validate` em homologacao/producao. NUNCA `update`/`create`
- Credenciais **TODAS** via variaveis de ambiente. Zero hardcode

### Excecoes

- `GlobalExceptionHandler` com `@ControllerAdvice` **OBRIGATORIO**
- Erros no formato RFC 7807 (`ProblemDetail`)
- Hierarquia: `BusinessException` → `ResourceNotFoundException`, `ConflictException`
- NUNCA `catch (Exception e)` generico sem re-throw especifico

### Padrao de codigo

- PascalCase classes, camelCase metodos, `UPPER_SNAKE` constantes
- DTOs: Records com sufixo `Request`/`Response` (ex: `CriarPessoaRequest`, `PessoaResponse`)
- Max 30 linhas por metodo, max 4 parametros, max 3 niveis de aninhamento
- Imports sem wildcard (`*`), ordenados
- NUNCA usar `@SneakyThrows`

### Dependencias PROIBIDAS

- Spring Security customizado que burle o Sidecar/Keycloak
- JJWT ou nimbus-jose avulso (fora do Sidecar)
- Apache HttpClient (usar WebClient ou RestTemplate)
- Qualquer ORM que nao seja Spring Data JPA/Hibernate
- WebFlux sem aprovacao explicita
- Lombok `@SneakyThrows`
- Qualquer dependencia SNAPSHOT em branch main/master

### Git

- Branch: `feature/PID-XXX-descricao`, `bugfix/PID-XXX-descricao`, `hotfix/PID-XXX-descricao`
- Commit: `tipo(escopo): descricao imperativa` (`feat`, `fix`, `refactor`, `docs`, `test`, `chore`)
- NUNCA commitar `.env`, `settings.xml` com credenciais, ou binarios

### Anti-alucinacao

Se faltar contexto sobre estrutura de tabelas, endpoints internos, claims do
Keycloak ou regras de negocio especificas: **PARE e pergunte.** Nunca presuma
padroes genericos da web.

---

## Pipeline CI/CD (Azure DevOps)

| Stage | Descricao |
|-------|-----------|
| build | Compilacao e empacotamento |
| test | Testes unitarios e cobertura |
| SAST | SonarQube + Trivy (imagens) |
| build-image | Build da imagem Docker |
| push | Push para JFrog Artifactory |
| deploy | Deploy no OpenShift |

### Ambientes e triggers

| Branch | Ambiente | Deploy | Aprovacao |
|--------|----------|--------|-----------|
| develop | Desenvolvimento | Automatico | Nenhuma |
| main/master | Homologacao | Automatico | 1 reviewer no PR |
| Tag em main | Producao | Manual | Scrum Master (Approval Gate) |

### Quality Gates (SonarQube)

- Cobertura de testes: >= 60%
- Bugs criticos: 0
- Vulnerabilidades HIGH/CRITICAL: 0
- Duplicacao de codigo: < 5%

> Pipeline deve **FALHAR** se detectar dependencia SNAPSHOT em branch main/master.

---

## Solucao de Problemas

| Sintoma | Causa provavel | Acao |
|---------|----------------|------|
| `Process terminated with exit code: 1` no `spring-boot:run` | Porta 8080 ja em uso por outra instancia | Encerre a instancia anterior ou defina `SERVER_PORT` |
| `Schema-validation` / tabela nao encontrada no startup | `ddl-auto=validate` com banco vazio | Rode com `-Dspring.jpa.hibernate.ddl-auto=update` (dev) ou aplique o schema |
| `FATAL: database "..." does not exist` | Database inexistente no Postgres alvo | Crie: `psql -U postgres -c "CREATE DATABASE exemplo_db;"` |
| `FATAL: password authentication failed` | `DB_PASSWORD` diferente da senha real do Postgres | Ajuste `DB_PASSWORD` (veja `echo $POSTGRES_PASSWORD` no container do banco) |
| `Unable to determine Dialect without JDBC metadata` | Consequencia de falha de conexao com o banco (nome/usuario/senha/host) | Corrija a conexao; no Docker use `host.docker.internal` |
| `no match for platform in manifest` no Docker | Imagem base sem a arquitetura do host | Ja resolvido: imagens multi-arch no `Dockerfile` |
| `log4j-bom:...pom (absent)` durante o build | Artefato ausente no Artifactory | Fallback do Maven Central (via `.mvn/settings.xml`) + `FAC.crt` no truststore |
| `PKIX path building failed` ao baixar do Central | CA do WAF ausente no truststore | `FAC.crt` importado no build (ver secao Docker) |

---

## Ferramenta de Codificacao Agêntica

A pasta `.harness/` contem skills e configuracoes base para a ferramenta de
codificacao agentica do time. **Renomeie esta pasta** conforme a ferramenta
adotada — ex: `.opencode`, `.claude`, `.codex`, `.kiro`, etc. — e ajuste os
conteudos conforme necessario.

O arquivo `AGENTS.md` na raiz do projeto contem as regras completas que a
ferramenta agentica deve seguir durante o desenvolvimento.
