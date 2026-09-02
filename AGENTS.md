# AGENTS.md — Backend Spring Boot (MPMS/DID) + SDD

Este é um microsserviço Java Spring Boot da Divisão de Desenvolvimento do MPMS.
Siga rigorosamente estas regras ao gerar ou modificar código neste repositório.

***

## 1. Objetivo e prioridades

Construir um microsserviço corporativo, seguro, modular e orientado por especificações (SDD — Specification-Driven Development), com Java 17 + Spring Boot, Keycloak/Sidecar e Spring Data JPA.

Prioridades, nesta ordem:

1. Segurança, autorização efetiva via Keycloak/Sidecar e isolamento de dados.
2. Aderência às specs, aos critérios de aceite e contratos de API.
3. Clareza arquitetural, consistência e manutenibilidade.
4. Tipagem forte e validação Jakarta.
5. Performance, rastreabilidade e observabilidade.

***

## 2. Princípios obrigatórios

* Trate a documentação SDD em `docs/specs/` como fonte de verdade funcional do sistema.
* Não invente regras de negócio, campos, permissões, fluxos, integrações ou requisitos ausentes nas specs, requisitos globais, ADRs ou solicitação explícita.
* Antes de implementar, localize a feature, suas dependências, critérios de aceite e permissões.
* Faça mudanças pequenas, coesas, rastreáveis e estritamente relacionadas à tarefa. Não refatore áreas não relacionadas sem solicitação explícita.
* Não introduza dependências sem justificativa técnica clara. Prefira as bibliotecas padronizadas neste documento.
* Não use `@SneakyThrows`, imports wildcard ou classes/métodos com mais de 30 linhas.
* Não exponha segredos, credenciais, tokens, PII desnecessária ou chaves privilegiadas em logs, testes ou arquivos versionados.
* Toda operação de escrita deve ter validação, tratamento de erro e constraints no banco.
* Autenticação exclusivamente via Keycloak/Sidecar. Autorização via microsserviço `_git/permissionamento`.
* Quando houver uma ambiguidade que altere comportamento de negócio, PARE e pergunte. Não assuma silenciosamente.

***

## 3. Hierarquia de decisão

Ao implementar qualquer funcionalidade, respeite a seguinte ordem de precedência:

1. Solicitação explícita atual do usuário.
2. Requisitos globais em `docs/specs/REQUIREMENTS-CATALOG.md`.
3. Decisões aceitas em `docs/adr/`.
4. `docs/specs/ROADMAP.md`, para fases, prioridades macro e dependências globais.
5. `docs/specs/backend/ROADMAP.md`, para a ordem de implementação da camada backend.
6. `docs/specs/backend/<numero>-<feature>/spec.md`, para domínio, dados, regras de negócio, segurança, integrações e contratos de backend.
7. `docs/specs/frontend/<numero>-<feature>/spec.md`, **somente leitura**, para entender o contrato de API, permissões e comportamento esperado pelo frontend.
8. Este `AGENTS.md`, para padrões técnicos, arquitetura, qualidade e processo de implementação.

`docs/specs/OPEN-QUESTIONS.md` não substitui uma decisão aceita. É o registro de dúvidas e bloqueios: se uma questão aberta afetar a tarefa, interrompa a decisão correspondente, informe o bloqueio e solicite esclarecimento.

### Conflitos entre documentos

* Regras de negócio, integridade, autorização e segurança seguem a spec de backend e ADRs aceitos.
* Backend e frontend usam o mesmo número de identificação quando tratam da mesma funcionalidade, mas nem toda spec de backend tem correspondente no frontend e vice-versa.
* A spec de frontend define o comportamento funcional e os contratos de API esperados; a spec de backend é a fonte de verdade para domínio e dados.
* Se backend e frontend divergem em requisito funcional, não invente uma conciliação: registre em `OPEN-QUESTIONS.md` e solicite decisão.
* Uma solicitação atual do usuário pode alterar documentação e código, mas mudanças arquiteturais recorrentes devem ser registradas em ADR, conforme o caso.

***

## 4. Estrutura de documentação SDD

```text
docs/
├── specs/
│   ├── backend/
│   │   ├── 0001-autenticacao/
│   │   │   └── spec.md
│   │   ├── 0002-consultas/
│   │   │   └── spec.md
│   │   ├── 0003-cadastros/
│   │   │   └── spec.md
│   │   └── ROADMAP.md
│   ├── frontend/
│   │   ├── 0001-autenticacao/
│   │   │   ├── spec.md
│   │   │   └── mockup.html
│   │   ├── 0003-cadastros/
│   │   │   ├── spec.md
│   │   │   └── mockup.html
│   │   └── ROADMAP.md
│   ├── OPEN-QUESTIONS.md
│   ├── REQUIREMENTS-CATALOG.md
│   └── ROADMAP.md
├── adr/
│   ├── 0001-usar-spring-boot-como-stack.md
│   └── 0002-usar-keycloak-sidecar.md
└── DESIGN.md
```

### Papel de cada artefato

| Artefato                         | Finalidade                                                        |
| -------------------------------- | ----------------------------------------------------------------- |
| `docs/specs/ROADMAP.md`          | Ordem macro, fases, dependências e prioridades do projeto         |
| `docs/specs/backend/ROADMAP.md`  | Ordem de implementação das specs backend                          |
| `docs/specs/frontend/ROADMAP.md` | Ordem de implementação das specs frontend                         |
| `backend/*/spec.md`              | Dados, domínio, regras, segurança, integrações e backend          |
| `frontend/*/spec.md`             | Fluxos de UI, rotas, ações, campos, validações e aceite funcional |
| `DESIGN.md`                      | Design system global obrigatório para o frontend                  |
| `frontend/*/mockup.html`         | Referência visual específica; não é código de produção            |
| `REQUIREMENTS-CATALOG.md`        | Requisitos transversais e regras globais                          |
| `OPEN-QUESTIONS.md`              | Dúvidas, premissas pendentes e decisões bloqueantes               |
| `docs/adr/*.md`                  | Decisões arquiteturais: contexto, alternativas e consequências    |

### Regra de fronteira cross-layer

> **O diretório** **`docs/specs/frontend/`** **é somente leitura para este agente.** Leia specs de frontend para entender contratos de API, permissões e comportamento esperado pela UI, mas **nunca crie, edite ou remova** arquivos nesse diretório. A propriedade das specs de frontend é do agente de frontend.

### Correlação entre specs

* Backend e frontend compartilham o mesmo número de identificação quando tratam da mesma funcionalidade (ex: `0001-autenticacao` existe em ambos os diretórios).
* Uma spec pode existir apenas no backend quando a funcionalidade não requer interface (ex: `0002-consultas`).
* Uma spec pode existir apenas no frontend quando a funcionalidade é puramente de apresentação ou navegação.
* Trate specs com mesmo identificador ou objetivo de negócio entre `frontend/` e `backend/` como uma unidade correlata de entrega.
* Não presuma que números iguais são equivalentes se títulos/objetivos divergirem; use nome, escopo e dependências documentadas.
* Se existir somente spec backend, implemente apenas o backend definido, sem inventar funcionalidades de UI.
* Se existir somente spec frontend dependente de dados/operações inexistentes, não invente contratos: registre bloqueio em `OPEN-QUESTIONS.md`.

### Regra para arquivos de backlog

Arquivos `backlog.csv` podem existir dentro de `docs/specs/` ou em suas subpastas. Esses arquivos são registros de planejamento/priorização externos e **não fazem parte da documentação SDD**. O agente deve:

* **Não ler** arquivos `backlog.csv` durante o fluxo SDD.
* **Não usar** `backlog.csv` como fonte de requisitos, escopo, prioridade ou ordem de implementação.
* **Não inferir** tarefas, dependências ou decisões a partir de `backlog.csv`.
* A fonte de verdade para ordem e escopo são exclusivamente os arquivos `ROADMAP.md`, `spec.md` e `REQUIREMENTS-CATALOG.md`.

***

## 5. Fluxo SDD obrigatório do agente

Ao receber uma tarefa, siga esta sequência sem pular etapas:

1. Leia `docs/specs/ROADMAP.md` para fase, prioridade e dependências globais.
2. Leia `docs/specs/backend/ROADMAP.md` para a ordem de implementação da camada backend.
3. Identifique a próxima spec pendente que respeita ordem e dependências. Não pule dependência sem solicitação explícita.
4. Leia `docs/specs/REQUIREMENTS-CATALOG.md` e `docs/specs/OPEN-QUESTIONS.md`.
5. Leia a `spec.md` da feature backend em `docs/specs/backend/<id>/spec.md`.
6. Quando existir, leia a spec de frontend correlata em `docs/specs/frontend/` (**somente leitura**) para entender os contratos de API e permissões esperados.
7. Leia ADRs pertinentes em `docs/adr/`, especialmente stack, autorização, dados, integrações e observabilidade.
8. Estabeleça um plano curto: objetivo, arquivos afetados, impacto em entidades/repositórios/services/controllers, migrations, testes e dúvidas/riscos.
9. Implemente nos limites corretos da arquitetura de pacotes, reutilizando padrões já existentes.
10. Valide todos os critérios de aceite e execute lint, testes e build.
11. Atualize roadmap, questões abertas ou documentação apenas se isso fizer parte do processo autorizado. Nunca marque spec como concluída sem validação completa.
12. Na entrega final, informe: o que mudou, migrations/entidades criadas, testes executados e pendências conhecidas.

***

## 6. Roadmaps e planejamento

Roadmaps devem permitir identificar objetivamente a próxima unidade válida de trabalho.

```md
# Roadmap

- [x] 0001 — Autenticação
- [ ] 0002 — Consultas
- [ ] 0003 — Cadastros

## Dependências
- 0002 depende de 0001.
- 0003 depende de 0001 e 0002.

## Regra de execução
Implementar na ordem apresentada, salvo dependência resolvida ou solicitação
explícita de alteração de prioridade.
```

* Considere `[x]` concluído e `[ ]` pendente, salvo convenção documentada diferente.
* Respeite dependências explícitas, mesmo que item posterior pareça mais simples.
* Um item só é concluído após critérios de aceite, testes e validações obrigatórias.
* Não altere prioridade, escopo ou sequência por conveniência de implementação.

### Metadados recomendados em specs

```md
---
id: 0003
titulo: Gestão de cadastros
status: ready
prioridade: alta
dependencias:
  - 0001
entidades:
  - cadastros
operacoes:
  - CRUD cadastros
requer_permissionamento: true
requer_sidecar: false
---
```

Inclua sempre `entidades`, `operacoes`, `requer_permissionamento` e `requer_sidecar`.

***

## 7. ADRs: decisões arquiteturais

Mantenha `docs/adr/` como repositório de decisões técnicas relevantes e duradouras. ADR não substitui spec funcional: ele registra contexto, decisão, alternativas e consequências.

Crie ADR para decisões que impactem múltiplas features, segurança, custo, operação, manutenção ou evolução futura. Exemplos: uso de Spring Boot, Keycloak/Sidecar, Spring Data JPA, Resilience4j, SpringDoc OpenAPI.

Não reescreva ADR aceito para mudar sua história. Se uma decisão for substituída, crie novo ADR referenciando o anterior.

***

## 8. Stack Obrigatória

| Área           | Tecnologia                                         | Uso definido                                         |
| -------------- | -------------------------------------------------- | ---------------------------------------------------- |
| Linguagem      | Java 17                                            | Todo código de aplicação                             |
| Build          | Spring Boot (herdado do MicroServiceParent 0.0.10) | Aplicação stateless, configuração e inicialização    |
| Segurança      | MicroServiceSidecar                                | Autenticação Keycloak, autorização, permissionamento |
| Dados          | Spring Data JPA + PostgreSQL (ou Oracle/MySQL)     | ORM, queries, transações                             |
| Validação      | Jakarta Validation                                 | Validação de inputs                                  |
| Produtividade  | Lombok + MapStruct                                 | Redução de boilerplate, mapeamento de DTOs           |
| Documentação   | SpringDoc OpenAPI                                  | Documentação de endpoints REST                       |
| Resiliência    | Resilience4j                                       | Circuit breaker, retry, rate limiter                 |
| Testes         | JUnit 5 + Mockito                                  | Testes unitários e de integração                     |
| Infraestrutura | Docker + OpenShift                                 | Containers, orquestração, JFrog Artifactory          |
| Pipeline       | Azure DevOps                                       | CI/CD, SAST (SonarQube + Trivy), Quality Gates       |

***

## 9. Dependências PROIBIDAS

* Spring Security customizado que burle o Sidecar/Keycloak
* JJWT ou nimbus-jose avulso (fora do Sidecar)
* Apache HttpClient (usar WebClient ou RestTemplate)
* Qualquer ORM que não seja Spring Data JPA/Hibernate
* WebFlux sem aprovação explícita
* Lombok `@SneakyThrows`
* Qualquer dependência SNAPSHOT em branch main/master

***

## 10. Arquitetura de Pacotes

```
br.mp.mpms.[servico]/
├── [Servico]Application.java        ← Única classe na raiz
├── v1/
│   ├── controller/                   ← Exposição REST, Jakarta Validation
│   ├── service/                      ← Regras de negócio
│   ├── repository/                   ← Spring Data JPA
│   └── model/
│       ├── entity/                   ← Entidades JPA
│       └── dto/                      ← Records Request/Response
└── infra/
    ├── config/                       ← Security, OpenAPI, CORS
    ├── exception/                    ← GlobalExceptionHandler, exceções custom
    └── interceptor/                  ← Filtros do Sidecar
```

***

## 11. Regras Inquebráveis

### Camadas

* Controller NUNCA acessa Repository diretamente.
* Service NUNCA retorna entidades JPA — sempre DTOs.
* Repository NUNCA contém lógica de negócio.

### Segurança

* Autenticação EXCLUSIVAMENTE via Keycloak/Sidecar. NUNCA implementar login próprio.
* Autorização via microsserviço `_git/permissionamento`. NUNCA roles hardcoded.
* Queries SEMPRE parametrizadas (`@Query` com `:param` ou Spring Data methods). NUNCA concatenação SQL.
* Logs NUNCA contêm CPF, senhas, tokens JWT, refresh tokens, chaves de API, IPs internos ou dados PII. CPF em logs deve usar mascaramento: `***.***.***-XX`.
* Campos sensíveis em DTOs de resposta: usar `@JsonIgnore` ou excluir do DTO.
* Respostas de erro NUNCA expõem stack traces, nomes de tabelas, versões de framework.
* CORS apenas para domínios `*.mpms.mp.br`. NUNCA `Access-Control-Allow-Origin: *`.
* Headers de segurança obrigatórios: `X-Content-Type-Options: nosniff`, `X-Frame-Options: DENY`, `Strict-Transport-Security: max-age=31536000`, `Content-Security-Policy: default-src 'self'`.
* `spring.jpa.hibernate.ddl-auto=validate` em homologação/produção. NUNCA `update`/`create`.

### Credenciais

* TODAS via variáveis de ambiente (`${ENV_VAR}` no `application.yml`).
* Zero hardcode de URLs, senhas, tokens ou chaves.
* `settings.xml` do Maven usa `${env.ARTIFACTORY_USER}` e `${env.ARTIFACTORY_PASSWORD}`.

### Exceções

* `GlobalExceptionHandler` com `@ControllerAdvice` OBRIGATÓRIO.
* Respostas de erro no formato RFC 7807 (`ProblemDetail`).
* Hierarquia: `BusinessException` → `ResourceNotFoundException`, `ConflictException`, etc.
* NUNCA catch genérico (`Exception e`) sem tratamento ou re-throw específico.

### Padrão de Código

* Nomenclatura: PascalCase classes, camelCase métodos, `UPPER_SNAKE` constantes.
* DTOs: sufixo `Request`/`Response` (`CriarPessoaRequest`, `PessoaResponse`).
* Records para DTOs imutáveis.
* Max 30 linhas por método, max 4 parâmetros, max 3 níveis de aninhamento.
* Imports sem wildcard (`*`), ordenados.

### Testes

* Nome: `deve_[comportamento]_quando_[condicao]`
* Service: JUnit 5 + Mockito (`@ExtendWith MockitoExtension`)
* Controller: `@WebMvcTest` + MockMvc
* Cobertura mínima do agente: 70% services, 50% controllers (o pipeline impõe piso de 60% via Quality Gates do SonarQube)

***

## 12. Testes e definição de pronto

### Cobertura por tipo de mudança

| Mudança                    | Cobertura mínima esperada                                |
| -------------------------- | -------------------------------------------------------- |
| Service (regra de negócio) | Teste unitário JUnit 5 + Mockito                         |
| Validação Jakarta          | Casos válidos, inválidos e limites relevantes            |
| DTO / MapStruct            | Mapeamento correto de todos os campos                    |
| Controller                 | `@WebMvcTest` + MockMvc — input inválido, sucesso e erro |
| Repository                 | Consulta parametrizada, retorno esperado                 |
| Circuit Breaker            | Cenário de fallback e recuperação                        |
| Jornada crítica            | Teste de integração ponta a ponta                        |

### Checklist de pronto

* [ ] A implementação cobre os critérios de aceite da spec de backend.
* [ ] Foi lida a spec de frontend correlata quando existir (somente leitura).
* [ ] Tipos estão corretos, sem imports wildcard ou `@SneakyThrows`.
* [ ] Controller não acessa Repository diretamente.
* [ ] Service retorna apenas DTOs, nunca entidades JPA.
* [ ] Autenticação via Keycloak/Sidecar; autorização via permissionamento.
* [ ] Não há segredo, chave privilegiada, token ou PII em código/logs.
* [ ] `application.yml` usa variáveis de ambiente, sem hardcode.
* [ ] `GlobalExceptionHandler` presente; erros no formato RFC 7807.
* [ ] Testes pertinentes foram criados/ajustados e passam.
* [ ] Testes, lint e build passam.
* [ ] Não há alterações fora do escopo solicitado.
* [ ] Nenhum arquivo em `docs/specs/frontend/` foi alterado.
* [ ] Cobertura mínima de testes atingida (70% services, 50% controllers).

***

## 13. Ambiente e configuração

* Versione somente `.env.example` ou `application-example.yml`, com nomes de variáveis e valores fictícios.
* Arquivos `.env*` reais e `application-*.yml` com credenciais devem estar no `.gitignore`.
* Valide variáveis obrigatórias na inicialização da aplicação.
* Separe ambientes local, desenvolvimento, homologação e produção.
* Não reutilize dados produtivos localmente sem anonimização formal.

```yaml
# Exemplo de application.yml
spring:
  datasource:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: validate
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: ${KEYCLOAK_ISSUER_URI}

keycloak:
  realm: ${KEYCLOAK_REALM}
  resource: ${KEYCLOAK_CLIENT_ID}
```

***

## 14. Git

* Branch: `feature/PID-XXX-descricao`, `bugfix/PID-XXX-descricao`, `hotfix/PID-XXX-descricao`
* Commit: `tipo(escopo): descricao imperativa` (`feat`, `fix`, `refactor`, `docs`, `test`, `chore`)
* NUNCA commitar `.env`, `settings.xml` com credenciais, ou binários.

***

## 15. Containers e Infraestrutura (Docker / OpenShift)

* Use imagens base enxutas: Alpine ou distroless como base final.
* O processo no container NUNCA roda como root — defina `USER` sem privilégios no Dockerfile.
* Multi-stage build obrigatório: estágio de build separado do estágio de produção. Nenhuma dependência de dev na imagem final.
* Defina liveness probe e readiness probe em todo manifesto Kubernetes/OpenShift.
* Recursos (cpu/memory requests e limits) devem ser declarados em todo Deployment.
* Imagens devem ser tagueadas com versão semântica — nunca use `:latest` em produção.
* Registry interno obrigatório: **JFrog Artifactory** para imagens Docker. Proibido pull direto de Docker Hub em produção.
* `HEALTHCHECK` obrigatório no Dockerfile.
* Segredos NUNCA como `ENV` no Dockerfile — usar Secrets do Kubernetes/OpenShift.
* `.dockerignore` obrigatório para excluir `.git`, `.env`, `target/`.

### Padrão Dockerfile Backend (Spring Boot)

```dockerfile
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests -B

FROM eclipse-temurin:17-jre-alpine
RUN addgroup -S app && adduser -S app -G app
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
USER app
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD wget --spider -q http://localhost:8080/actuator/health || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]
```

***

## 16. Pipeline CI/CD (Azure DevOps)

* Todo pipeline deve ter stages sequenciais: **build → test → SAST → build-image → push → deploy**.
* Etapa de SAST obrigatória (**SonarQube** + **Trivy** para imagens) antes do push para registry.
* Segredos e credenciais SOMENTE via **Azure Key Vault** ou Variable Groups marcados como secretos — nunca em variáveis comuns do pipeline.
* Deploys em produção exigem aprovação manual (**Approval Gate**) do Scrum Master configurada no environment.
* Rollback automatizado deve ser configurado: se health checks falharem pós-deploy, reverter automaticamente.
* Artefatos de build (imagens, JARs) devem ser versionados e rastreáveis até o commit que os gerou.
* Pipeline deve **FALHAR** se detectar dependência SNAPSHOT em branch main/master.
* Tags de imagem Docker devem seguir formato: `{nome-servico}:{versao-semantica}-{short-sha}`.

### Ambientes e Triggers

| Branch      | Ambiente        | Deploy     | Aprovação                    |
| ----------- | --------------- | ---------- | ---------------------------- |
| develop     | Desenvolvimento | Automático | Nenhuma                      |
| main/master | Homologação     | Automático | 1 reviewer no PR             |
| Tag em main | Produção        | Manual     | Scrum Master (Approval Gate) |

### Quality Gates (SonarQube)

* Cobertura de testes: >= 60%
* Bugs críticos: 0
* Vulnerabilidades HIGH/CRITICAL: 0
* Duplicação de código: < 5%

### Regras de Timeout

* Build: max 10 minutos
* Testes: max 15 minutos
* Deploy dev/homolog: max 5 minutos
* Aprovação produção: timeout 72 horas

***

## 17. Anti-Alucinação

Se faltar contexto sobre:

* Estrutura de tabelas do banco
* Nomes de endpoints de serviços internos
* Claims ou roles do Keycloak
* Regras de negócio específicas

**PARE e pergunte.** Nunca presuma padrões genéricos da web.

***

## 18. Proibições explícitas

* Não usar Spring Security customizado que burle o Sidecar/Keycloak.
* Não usar JJWT ou nimbus-jose avulso (fora do Sidecar).
* Não implementar login próprio.
* Não usar roles hardcoded.
* Não usar concatenação SQL.
* Não expor CPF, senhas, tokens, IPs internos ou PII em logs.
* Não expor stack traces, nomes de tabelas ou versões em respostas de erro.
* Não usar CORS fora de `*.mpms.mp.br`.
* Não usar `ddl-auto=update`/`create` em homologação/produção.
* Não hardcodar URLs, senhas, tokens ou chaves.
* Não rodar container como root.
* Não usar tag `:latest` em produção.
* Não fazer pull direto de Docker Hub em produção (usar JFrog Artifactory).
* Não usar catch genérico sem re-throw específico.
* Não usar imports wildcard (`*`).
* Não usar Lombok `@SneakyThrows`.
* Não usar dependências SNAPSHOT em main/master.
* Não commitar `.env`, `.dockerignore`, `settings.xml` com credenciais, ou binários.
* Não ignorar spec, roadmap, ADR ou questão aberta relevante para acelerar implementação.
* Não ler, processar ou basear-se em arquivos `backlog.csv` dentro de `docs/specs/`.
* Não presumir padrões genéricos da web quando faltar contexto.
* Controller NUNCA acessar Repository diretamente.
* Service NUNCA retornar entidades JPA.
* Repository NUNCA conter lógica de negócio.
* **Não criar, editar ou remover arquivos em** **`docs/specs/frontend/`.**

***

## 19. Critério de decisão rápido

| Pergunta                                           | Decisão                                                     |
| -------------------------------------------------- | ----------------------------------------------------------- |
| Envolve autenticação/autorização?                  | Keycloak/Sidecar + permissionamento                         |
| É regra de autorização ou isolamento de acesso?    | Permissionamento via microsserviço `_git/permissionamento`  |
| É alteração de entidade, constraint ou schema?     | Nova migration e validação com `ddl-auto=validate`          |
| É validação de dados no servidor?                  | Jakarta Validation + Spring Data JPA constraints            |
| É operação multi-tabela atômica?                   | `@Transactional` no Service                                 |
| É containerização ou infraestrutura?               | Ver seções 15 (Containers) e 16 (Pipeline)                  |
| Há dúvida aberta que muda negócio ou segurança?    | Consultar/registrar `OPEN-QUESTIONS.md` e solicitar decisão |
| Falta contexto sobre tabelas, endpoints ou regras? | PARAR e perguntar. Não assumir.                             |

***

## 20. Regra final

Quando houver conflito entre velocidade e segurança/consistência, escolha segurança e consistência. Quando uma decisão estiver ausente das specs, ADRs e deste arquivo, não assuma silenciosamente: documente a hipótese, adote a alternativa mais conservadora e solicite validação antes de introduzir comportamento novo. **Se faltar contexto, PARE e pergunte. Nunca presuma padrões genéricos da web.**
