# Caso de Teste - Consultar localidades sem permissão de leitura - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-LOC-052 |
| **Nome do Caso de Teste** | Consultar localidades sem permissão de leitura |
| **Funcionalidade Testada** | Consulta de Localidades / Controle de Acesso (RBAC) |
| **Prioridade** | Alta |
| **Tipo de Teste** | Segurança / Controle de Acesso (RBAC) / Teste Negativo |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que usuário autenticado no sistema sem permissão de leitura para o módulo de localidades (ex.: perfil Externo ou Visitante sem a role correspondente) recebe erro HTTP 403 Forbidden ao tentar listar ou consultar localidades. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com credenciais válidas, porém sem privilégio de leitura no módulo de localidades (`LOCALIDADE_READ` ausente). |
| 2 | Localidades cadastradas no sistema, incluindo a Localidade A (`l0c00001-0000-0000-0000-000000000001`). |
| 3 | Módulo de autorização RBAC ativo no gateway e controllers da API. |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Preparar a requisição `GET /api/v1/localidades` com cabeçalho `Authorization: Bearer <token_sem_leitura>`. |
| 2 | Enviar a requisição para a API. |
| 3 | Consultar o código de status HTTP retornado e o corpo da resposta. |
| 4 | Tentar consultar individualmente a localidade `GET /api/v1/localidades/l0c00001-0000-0000-0000-000000000001` com o mesmo token. |
| 5 | Confirmar que ambas as tentativas são rejeitadas. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| ID da Localidade | `l0c00001-0000-0000-0000-000000000001` (Alvo de consulta individual) |
| Nome da Localidade | `Promotoria de Justiça da Cidadania` |
| Cidade (FK) | `c1d00001-0000-0000-0000-000000000001` (Campo Grande Alterada - Ativa) |
| Tipo de Localidade (FK) | `t1p00001-0000-0000-0000-000000000001` (Promotoria de Justiça - Ativo) |
| CEP | `79002-000` |
| Logradouro | `Rua da Paz` |
| Número | `140` |
| Bairro | `Jardim dos Estados` |
| Complemento | `Edifício Sede` |
| Local Sigiloso | `false` |
| Status / Ativo | `true` |
| Cabeçalho Authorization | `Bearer <token_sem_leitura>` |
| Perfil do Usuário | Usuário sem permissão de leitura no módulo de localidades |
| Método HTTP | `GET /api/v1/localidades` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Operação bloqueada retornando código de status HTTP `403 Forbidden`. |
| 2 | Resposta RFC 7807 (ProblemDetail) informando que o usuário não possui permissão de leitura para a funcionalidade requerida. |
| 3 | Nenhum dado cadastral de localidades é exposto ao cliente. |

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
| Observações | Atendimento ao critério de controle de acesso de HU-10.2: o usuário sem permissão de leitura de localidades deve receber erro HTTP 403 Forbidden. |
