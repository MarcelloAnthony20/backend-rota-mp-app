# Caso de Teste - Criar localidade com token expirado - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-LOC-036 |
| **Nome do Caso de Teste** | Criar localidade com token expirado |
| **Funcionalidade Testada** | Cadastro de Localidade / Autenticação JWT |
| **Prioridade** | Crítica |
| **Tipo de Teste** | Segurança / Autenticação |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que a tentativa de criar uma localidade utilizando um token JWT com tempo de expiração expirado (claim exp no passado) é rejeitada pelo sistema com status HTTP 401 Unauthorized, sem persistir dados no banco. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Token JWT gerado para usuário com perfil de Gestor com claim `exp` anterior ao momento atual da requisição. |
| 2 | Cidade A (`c1d00001-0000-0000-0000-000000000001 - Campo Grande Alterada`) e Tipo (`b1000001-0000-0000-0000-000000000001`) previamente cadastrados e ativos. |
| 3 | Serviço de autenticação e API de localidades operacionais. |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Enviar requisição `POST /api/v1/localidades` contendo payload (`CriarLocalidadeRequest` com 10 campos: `nome`, `cidadeId`, `tipoLocalidadeId`, `cep`, `logradouro`, `numero`, `bairro`, `complemento`, `localSigiloso`, `ativo`) com dados válidos de localidade e cabeçalho `Authorization: Bearer <token_jwt_expirado>`. |
| 2 | Consultar o código de status HTTP retornado na resposta da requisição. |
| 3 | Validar a mensagem de erro retornada indicando que a credencial/sessão expirou. |
| 4 | Consultar o banco de dados e certificar que nenhum novo registro de localidade foi inserido. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| ID da Localidade | Gerado automaticamente pelo sistema (não enviado no payload de criação) |
| Nome da Localidade | `Promotoria de Justiça da Cidadania` |
| Cidade (FK) | `c1d00001-0000-0000-0000-000000000001 - Campo Grande Alterada` |
| Tipo de Localidade (FK) | `b1000001-0000-0000-0000-000000000001 - Promotoria de Justiça` |
| CEP | `79002-000` |
| Logradouro | `Rua da Paz` |
| Número | `140` |
| Bairro | `Jardim dos Estados` |
| Complemento | `Edifício Sede` |
| Local Sigiloso | `false` |
| Status / Ativo | `true` |
| Cabeçalho Authorization | `Bearer <token_jwt_expirado>` |
| Perfil do Usuário | `Gestor (Sessão Expirada)` |
| Método HTTP | `POST` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Operação bloqueada retornando status HTTP `401 Unauthorized`. |
| 2 | Resposta estruturada conforme RFC 7807 (ProblemDetail) informando que o token de autenticação está expirado. |
| 3 | Nenhum registro é gravado ou persistido no banco de dados. |

## Resultado Obtido

| Campo | Valor |
|---------|---------|
| Resultado Obtido | A preencher durante a execução |

## Status

| Opções |
|---------|
| Aprovado |
| Reprovado |
| Bloqueado |
| Não Executado |

## Observações

| Campo | Descrição |
|---------|---------|
| Observações | Validação do ciclo de vida e tempo de expiração dos tokens de autenticação JWT conforme HU-10.1. |
