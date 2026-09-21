# Caso de Teste - Cadastrar localidade não sigilosa sem endereço completo - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-LOC-047 |
| **Nome do Caso de Teste** | Cadastrar localidade não sigilosa sem endereço completo |
| **Funcionalidade Testada** | Validação de Sigilo e Endereço Obrigatório (RN-7 / HU-10.1) |
| **Prioridade** | Alta |
| **Tipo de Teste** | Regra de Negócio / Teste Negativo / Validação de Dados |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que o sistema exige o preenchimento obrigatório do endereço completo quando a localidade não for sinalizada como sigilosa (`localSigiloso = false`), rejeitando a tentativa de cadastro com campos de endereço vazios com status HTTP 400 Bad Request ou 422 Unprocessable Entity. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil de Gestor. |
| 2 | Cidade A ativa cadastrada no sistema (ID: `c1d00001-0000-0000-0000-000000000001` - Campo Grande Alterada). |
| 3 | Tipo de localidade ativo cadastrado no sistema (ID: `b1000001-0000-0000-0000-000000000001` - Promotoria de Justiça). |
| 4 | Regra de Negócio RN-7 ativa no validador da camada de domínio. |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Preparar a requisição `POST /api/v1/localidades` com cabeçalho `Authorization: Bearer <token_gestor>`. |
| 2 | Informar o payload `CriarLocalidadeRequest` composto por 10 campos (`nome`, `cidadeId`, `tipoLocalidadeId`, `cep`, `logradouro`, `numero`, `bairro`, `complemento`, `localSigiloso`, `ativo`), com `localSigiloso = false`, cidade e tipo válidos (`b1000001-0000-0000-0000-000000000001`), mas com os campos de endereço preenchidos vazios (`""`). |
| 3 | Enviar a requisição para a API. |
| 4 | Consultar o código de status HTTP retornado e o objeto de erro RFC 7807 (ProblemDetail). |
| 5 | Verificar na base de dados se a persistência foi impedida. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| ID da Localidade | Gerado automaticamente pelo sistema (não enviado no payload de criação) |
| Nome da Localidade | `Núcleo de Atendimento Comunitário` |
| Cidade (FK) | `c1d00001-0000-0000-0000-000000000001` (Campo Grande Alterada - Ativa) |
| Tipo de Localidade (FK) | `b1000001-0000-0000-0000-000000000001` (Promotoria de Justiça - Ativo) |
| CEP | `""` (Vazio) |
| Logradouro | `""` (Vazio) |
| Número | `""` (Vazio) |
| Bairro | `""` (Vazio) |
| Complemento | `""` (Vazio) |
| Local Sigiloso | `false` |
| Status / Ativo | `true` |
| Cabeçalho Authorization | `Bearer <token_gestor>` |
| Perfil do Usuário | Gestor |
| Método HTTP | `POST /api/v1/localidades` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Operação rejeitada retornando código de status HTTP `400 Bad Request` ou `422 Unprocessable Entity`. |
| 2 | Resposta estruturada em ProblemDetail (RFC 7807) indicando a violação de campos obrigatórios de endereço (CEP, logradouro, número e bairro) para localidades com `localSigiloso = false`. |
| 3 | Nenhuma inserção é persistida no banco de dados. |

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
| Observações | Atendimento estrito à Regra de Negócio RN-7 e especificação de HU-10.1: exigir o preenchimento do endereço completo caso a localidade cadastrada não seja sinalizada como sigilosa. |
