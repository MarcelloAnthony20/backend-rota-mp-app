# Caso de Teste - Consultar localidade sigilosa sem perfil permitido - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-LOC-043 |
| **Nome do Caso de Teste** | Consultar localidade sigilosa sem perfil permitido |
| **Funcionalidade Testada** | Consulta de Localidade / Tratamento de Sigilo (RN-2) |
| **Prioridade** | Crítica |
| **Tipo de Teste** | Segurança / Proteção de Dados Sigilosos |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que a consulta de localidade marcada como sigilosa (localSigiloso = true) por usuário sem perfil ou privilégio de sigilo (ex.: perfil Operador) aplica mascaramento estrito aos dados sensíveis de endereço (logradouro, número, bairro, CEP, complemento) conforme a regra RN-2 ou bloqueia o acesso com status HTTP 403 Forbidden. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil Operador (`ROLE_OPERADOR`), sem permissão de visualização de endereços sigilosos. |
| 2 | Localidade A existente cadastrada com flag `localSigiloso = true`, status ativo e ID `d1000001-0000-0000-0000-000000000001`. |
| 3 | Regra de negócio de proteção de dados sensíveis (RN-2) implementada no serviço de consulta. |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Enviar requisição `GET /api/v1/localidades/d1000001-0000-0000-0000-000000000001` utilizando as credenciais de autenticação do perfil Operador. |
| 2 | Consultar o código de status HTTP retornado na resposta da requisição. |
| 3 | Inspecionar a estrutura do payload JSON retornado na resposta. |
| 4 | Validar se os campos sensíveis de endereço (logradouro, número, bairro, complemento, CEP) foram omitidos ou mascarados com marcador seguro (ex.: `***` ou `[DADO SIGILOSO]`). |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| ID da Localidade | `d1000001-0000-0000-0000-000000000001` |
| Nome da Localidade | `Promotoria de Justiça da Cidadania` |
| Cidade (FK) | `c1d00001-0000-0000-0000-000000000001 - Campo Grande Alterada` |
| Tipo de Localidade (FK) | `b1000001-0000-0000-0000-000000000001 - Promotoria de Justiça` |
| CEP | `79002-000` (Dado Sensível) |
| Logradouro | `Rua da Paz` (Dado Sensível) |
| Número | `140` (Dado Sensível) |
| Bairro | `Jardim dos Estados` (Dado Sensível) |
| Complemento | `Edifício Sede` (Dado Sensível) |
| Local Sigiloso | `true` |
| Status / Ativo | `true` |
| Cabeçalho Authorization | `Bearer <token_jwt_operador_valido>` |
| Perfil do Usuário | `Operador (Sem permissão de sigilo)` |
| Método HTTP | `GET` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Requisição processada retornando status HTTP `200 OK` (com mascaramento dos dados sensíveis) ou `403 Forbidden` (caso a política de segurança restrinja integralmente a visualização do registro sigiloso). |
| 2 | No retorno com status `200 OK`: campos `logradouro`, `numero`, `bairro`, `cep` e `complemento` são apresentados mascarados (ex.: `***` ou `[SIGILOSO]`) ou omitidos do DTO de resposta. |
| 3 | Campo `localSigiloso = true` é explicitado no DTO. |
| 4 | Garantia absoluta de sigilo e não exposição de endereços confidenciais para perfis não autorizados conforme RN-2. |

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
| Observações | Atendimento estrito à Regra de Negócio RN-2 e critérios de proteção da informação confidencial de HU-10.2 e HU-10.6. |
