# Caso de Teste - Adicionar localidade sem complemento - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-LOC-004 |
| **Nome do Caso de Teste** | Adicionar localidade sem complemento |
| **Funcionalidade Testada** | Cadastro de Localidade |
| **Prioridade** | Média |
| **Tipo de Teste** | Teste Positivo / Omissão de Opcional |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que o campo complemento pode ser enviado vazio ("") ou omitido na requisição sem ocasionar erros de validação ou de persistência no sistema. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil de Gestor ou Administrador (token JWT válido). |
| 2 | Cidade ativa previamente cadastrada no sistema (ID `c1d00001-0000-0000-0000-000000000001` - Campo Grande Alterada). |
| 3 | Tipo de localidade ativo previamente cadastrado no sistema (ID `t1p00001-0000-0000-0000-000000000001` - Promotoria de Justiça). |
| 4 | Serviço de busca de CEP operacional. |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Acessar o endpoint `POST /api/v1/localidades`. |
| 2 | Informar o payload com os dados completos e válidos, definindo o atributo complemento como `"" (Vazio)`. |
| 3 | Enviar a requisição com cabeçalho de autenticação de Gestor. |
| 4 | Consultar o código de status HTTP retornado e a integridade da resposta. |
| 5 | Verificar na base de dados a persistência da localidade e do endereço com complemento nulo. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| ID da Localidade | Auto-gerado pelo sistema (ex.: `l0c00001-0000-0000-0000-000000000001`) |
| Nome da Localidade | `Promotoria de Justiça da Cidadania` |
| Cidade (FK) | `c1d00001-0000-0000-0000-000000000001` (Campo Grande Alterada) |
| Tipo de Localidade (FK) | `t1p00001-0000-0000-0000-000000000001` (Promotoria de Justiça) |
| CEP | `79002-000` |
| Logradouro | `Rua da Paz` |
| Número | `140` |
| Bairro | `Jardim dos Estados` |
| Complemento | `"" (Vazio)` |
| Local Sigiloso | `false` |
| Status / Ativo | `Ativo (true)` |
| Cabeçalho Authorization | `Bearer <token_gestor>` |
| Método HTTP / Endpoint | `POST /api/v1/localidades` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Cadastro aceito com status HTTP `201 Created`. |
| 2 | Resposta estruturada conforme DTO com ID gerado e relacionamentos consistentes. |
| 3 | Registro de endereço gravado no banco de dados com `complemento = null`. |
| 4 | Localidade gravada com status ativo (`ativo = true`). |

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
| Observações | Confirmação de não obrigatoriedade do atributo complemento conforme DER e HU-10.1. |
