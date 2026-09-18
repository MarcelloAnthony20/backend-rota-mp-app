# Caso de Teste - Cadastrar localidade vinculando cidade inativa - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-LOC-045 |
| **Nome do Caso de Teste** | Cadastrar localidade vinculando cidade inativa |
| **Funcionalidade Testada** | Cadastro de Localidade / Integridade Operacional e Pipeline de Cidades |
| **Prioridade** | Crítica |
| **Tipo de Teste** | Regra de Negócio / Teste Negativo / Integração |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que o sistema rejeita o cadastro de localidade vinculada a uma cidade inativa (Cidade B: Dourados, ID `c1d00002-0000-0000-0000-000000000002`, inativada/deletada com HTTP 204 no CT-CID-011/022), retornando status HTTP 400 Bad Request ou 422 Unprocessable Entity sem persistir dados no banco. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado no sistema com perfil de Gestor. |
| 2 | Cidade B previamente inativada/deletada logicamente (ID: `c1d00002-0000-0000-0000-000000000002`, Nome: `Dourados`, `ativo = false`). |
| 3 | Tipo de localidade existente e ativo cadastrado (ID: `t1p00001-0000-0000-0000-000000000001`, Nome: `Promotoria de Justiça`, `ativo = true`). |
| 4 | Mecanismo de integridade referencial e validação de entidades ativas operacional na API. |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Preparar a requisição `POST /api/v1/localidades` com cabeçalho `Authorization: Bearer <token_gestor>`. |
| 2 | Informar o payload associando a localidade à Cidade B inativa (`c1d00002-0000-0000-0000-000000000002`) e preencher todos os demais atributos cadastrais. |
| 3 | Enviar a requisição para a API. |
| 4 | Consultar o código de status HTTP retornado e o corpo da resposta em formato RFC 7807 (ProblemDetail). |
| 5 | Consultar a base de dados para certificar que nenhum registro foi persistido na tabela de localidades. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| ID da Localidade | `null` (Gerado automaticamente pelo backend) |
| Nome da Localidade | `Promotoria de Justiça de Dourados` |
| Cidade (FK) | `c1d00002-0000-0000-0000-000000000002` (Dourados - Inativa / Deletada) |
| Tipo de Localidade (FK) | `t1p00001-0000-0000-0000-000000000001` (Promotoria de Justiça - Ativo) |
| CEP | `79800-000` |
| Logradouro | `Rua Presidente Vargas` |
| Número | `450` |
| Bairro | `Vila Progresso` |
| Complemento | `Bloco C` |
| Local Sigiloso | `false` |
| Status / Ativo | `true` |
| Cabeçalho Authorization | `Bearer <token_gestor>` |
| Perfil do Usuário | Gestor |
| Método HTTP | `POST /api/v1/localidades` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Requisição rejeitada retornando código de status HTTP `400 Bad Request` ou `422 Unprocessable Entity`. |
| 2 | Corpo de resposta formatado como RFC 7807 (ProblemDetail) contendo mensagem clara informando que a cidade selecionada deve estar obrigatoriamente ativa. |
| 3 | Nenhuma inserção é realizada na tabela de localidades do banco de dados. |
| 4 | Comprovado o bloqueio de vinculação com a Cidade B inativada/deletada na linhagem de execução do pipeline. |

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
| Observações | Atendimento ao critério de integridade operacional de HU-10.1 e RN-1: cidade deve ser selecionada a partir de lista de registros válidos e ativos. Integração direta com o estado da Cidade B (`c1d00002-0000-0000-0000-000000000002`) deletada/inativada com HTTP 204 no CT-CID-011/022. |
