# Caso de Teste - Reativar cidade usando método HTTP não suportado - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-CID-021 |
| **Nome do Caso de Teste** | Reativar cidade usando método HTTP não suportado |
| **Funcionalidade Testada** | Reativação de cidade e método HTTP |
| **Prioridade** | Média |
| **Tipo de Teste** | Teste de Contrato / Método HTTP / Teste Negativo |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que o endpoint de reativação de cidades rejeita métodos HTTP não suportados (ex.: DELETE na rota `/ativar` informando a Seed Cidade D), retornando status HTTP 405 Method Not Allowed e preservando intacto o estado inativo do registro. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil de Gestor ou superior. |
| 2 | Seed Cidade D (Inativa) cadastrada na base de dados com ID `c1d00004-0000-0000-0000-000000000004`, Nome: `"Corumbá"`, Código: `"5003207"` e status inativo (`ativo = false`). |
| 3 | Endpoint de reativação de cidades disponível e operacional. |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Acessar o endpoint de reativação informando o ID da Seed Cidade D (`/api/v1/cidades/c1d00004-0000-0000-0000-000000000004/ativar`). |
| 2 | Enviar requisição utilizando o método HTTP `DELETE` acompanhada do token de autorização de Gestor. |
| 3 | Consultar o código de status HTTP e os cabeçalhos retornados na resposta. |
| 4 | Verificar se o status e os dados da Seed Cidade D permanecem inalterados no banco de dados. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| Entidade / Seed | Seed Cidade D (Inativa) |
| ID da Cidade | `c1d00004-0000-0000-0000-000000000004` |
| Nome da Cidade | `Corumbá` |
| Código da Cidade | `5003207` |
| Status / Ativo | `Inativo (false)` |
| Método HTTP | `DELETE` (na rota `/api/v1/cidades/{id}/ativar`) |
| Cabeçalho Authorization | `Bearer <token_gestor_valido>` |
| Perfil do Usuário | `Gestor` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Requisição rejeitada pelo servidor retornando status HTTP `405 Method Not Allowed`. |
| 2 | Cabeçalho `Allow` presente na resposta indicando o método permitido para reativação (`PATCH`). |
| 3 | O estado da Seed Cidade D no banco de dados permanece inalterado (`ativo = false`). |
| 4 | Nenhuma falha de infraestrutura interna (HTTP 500) é gerada. |

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
| Observações | Validação de restrição de verbos HTTP e integridade de rotas de alteração de estado conforme HU-08.4. Utiliza explicitamente a Seed Cidade D (`c1d00004-0000-0000-0000-000000000004`), reservada para fluxos de reativação. |
