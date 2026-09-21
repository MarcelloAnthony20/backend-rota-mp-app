# Caso de Teste - Cadastrar localidade vinculando tipo de localidade inativo - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-LOC-046 |
| **Nome do Caso de Teste** | Cadastrar localidade vinculando tipo de localidade inativo |
| **Funcionalidade Testada** | Cadastro de Localidade / Integridade Referencial de Tipo |
| **Prioridade** | Crítica |
| **Tipo de Teste** | Regra de Negócio / Teste Negativo / Integridade |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que o cadastro de localidade é rejeitado caso o tipo de localidade selecionado esteja com status inativo (`b1000009-0000-0000-0000-000000000009` - Posto Avançado Desativado), retornando código HTTP 400 Bad Request ou 422 Unprocessable Entity sem gravar dados no banco. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado no sistema com perfil de Gestor. |
| 2 | Cidade A ativa cadastrada no sistema (ID: `c1d00001-0000-0000-0000-000000000001`, Nome: `Campo Grande Alterada`, `ativo = true`). |
| 3 | Tipo de localidade previamente inativado no sistema (ID: `b1000009-0000-0000-0000-000000000009`, Nome: `Posto Avançado Desativado`, `ativo = false`). |
| 4 | Mecanismo de validação referencial de tipos de localidade ativos operacional na API. |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Preparar a requisição `POST /api/v1/localidades` com cabeçalho `Authorization: Bearer <token_gestor>`. |
| 2 | Informar o payload `CriarLocalidadeRequest` composto por 10 campos (`nome`, `cidadeId`, `tipoLocalidadeId`, `cep`, `logradouro`, `numero`, `bairro`, `complemento`, `localSigiloso`, `ativo`), associando a Cidade A ativa e o Tipo de Localidade inativo (`b1000009-0000-0000-0000-000000000009`), preenchendo todos os atributos da entidade. |
| 3 | Enviar a requisição para o endpoint de cadastro de localidades. |
| 4 | Consultar o código de status HTTP retornado e o corpo de resposta RFC 7807 (ProblemDetail). |
| 5 | Verificar no banco de dados se a inserção da localidade foi bloqueada. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| ID da Localidade | Gerado automaticamente pelo sistema (não enviado no payload de criação) |
| Nome da Localidade | `Posto Avançado Sul` |
| Cidade (FK) | `c1d00001-0000-0000-0000-000000000001` (Campo Grande Alterada - Ativa) |
| Tipo de Localidade (FK) | `b1000009-0000-0000-0000-000000000009` (Posto Avançado Desativado - Inativo) |
| CEP | `79002-000` |
| Logradouro | `Rua da Paz` |
| Número | `200` |
| Bairro | `Jardim dos Estados` |
| Complemento | `Sala 10` |
| Local Sigiloso | `false` |
| Status / Ativo | `true` |
| Cabeçalho Authorization | `Bearer <token_gestor>` |
| Perfil do Usuário | Gestor |
| Método HTTP | `POST /api/v1/localidades` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Operação rejeitada retornando status HTTP `400 Bad Request` ou `422 Unprocessable Entity`. |
| 2 | Objeto ProblemDetail (RFC 7807) retornado com mensagem explícita informando que o tipo de localidade selecionado deve estar obrigatoriamente ativo. |
| 3 | Nenhum registro é gravado na tabela de localidades do banco de dados. |

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
| Observações | Atendimento ao critério de integridade referencial de HU-10.1 e RN-1: o tipo de localidade deve ser selecionado obrigatoriamente a partir de registros válidos e ativos. |
