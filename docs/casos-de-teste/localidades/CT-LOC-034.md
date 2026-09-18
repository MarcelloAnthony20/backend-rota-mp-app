# Caso de Teste - Alterar localidade sem autorização - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-LOC-034 |
| **Nome do Caso de Teste** | Alterar localidade sem autorização |
| **Funcionalidade Testada** | Alteração de Localidade / Autorização RBAC |
| **Prioridade** | Crítica |
| **Tipo de Teste** | Segurança / Controle de Acesso (RBAC) |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que a tentativa de alterar os dados de uma localidade por usuário autenticado sem perfil de Gestor (ex.: usuário com perfil Operador) é rejeitada com status HTTP 403 Forbidden, sem efetuar qualquer modificação no banco de dados. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil não autorizado para gestão (perfil Operador: `ROLE_OPERADOR`). |
| 2 | Localidade A existente e cadastrada com status ativo e ID `l0c00001-0000-0000-0000-000000000001`. |
| 3 | Serviço de controle de acesso RBAC ativo e operacional. |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Enviar requisição `PUT /api/v1/localidades/l0c00001-0000-0000-0000-000000000001` contendo payload com alterações cadastrais e cabeçalho de autenticação do perfil Operador. |
| 2 | Consultar o código de status HTTP retornado na resposta da requisição. |
| 3 | Validar a mensagem de erro retornada no corpo da resposta no formato RFC 7807 (ProblemDetail). |
| 4 | Consultar a base de dados para garantir que nenhum atributo da localidade foi alterado. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| ID da Localidade | `l0c00001-0000-0000-0000-000000000001` |
| Nome da Localidade | `Promotoria de Justiça da Cidadania Modificada` |
| Cidade (FK) | `c1d00001-0000-0000-0000-000000000001 - Campo Grande Alterada` |
| Tipo de Localidade (FK) | `t1p00001-0000-0000-0000-000000000001 - Promotoria de Justiça` |
| CEP | `79002-000` |
| Logradouro | `Rua da Paz` |
| Número | `140` |
| Bairro | `Jardim dos Estados` |
| Complemento | `Edifício Sede` |
| Local Sigiloso | `false` |
| Status / Ativo | `true` |
| Cabeçalho Authorization | `Bearer <token_jwt_operador_valido>` |
| Perfil do Usuário | `Operador` |
| Método HTTP | `PUT` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Requisição bloqueada pelo controle de acesso retornando status HTTP `403 Forbidden`. |
| 2 | Resposta estruturada conforme RFC 7807 (ProblemDetail) informando ausência de privilégios para executar a operação. |
| 3 | Nenhum dado da localidade é alterado na base de dados (atomicidade e segurança garantidas). |

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
| Observações | Atendimento ao critério de autorização RBAC de HU-10.3 garantindo que apenas o perfil Gestor possa alterar localidades. |
