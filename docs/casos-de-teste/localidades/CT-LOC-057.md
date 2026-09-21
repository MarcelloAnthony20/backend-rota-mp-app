# Caso de Teste - Configurar sigilo da localidade com sucesso - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-LOC-057 |
| **Nome do Caso de Teste** | Configurar sigilo da localidade com sucesso |
| **Funcionalidade Testada** | Configurar Sigilo de Localidade (HU-10.6) |
| **Prioridade** | Crítica |
| **Tipo de Teste** | Teste Positivo / Segurança e Sigilo |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que o usuário com perfil de Gestor consegue alterar com sucesso a configuração de sigilo de uma localidade enviando valor booleano (`true`), atualizando exclusivamente esse campo na base de dados com retorno HTTP 200 OK e preservando intactos todos os demais atributos cadastrais. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil de Gestor com permissão de gestão de sigilo. |
| 2 | Localidade A existente no sistema previamente cadastrada com `localSigiloso = false` (ID: `d1000001-0000-0000-0000-000000000001`). |
| 3 | Endpoint de configuração de sigilo (`/api/v1/localidades/{id}/sigilo`) ativo. |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Preparar requisição `PATCH /api/v1/localidades/d1000001-0000-0000-0000-000000000001/sigilo` com cabeçalho `Authorization: Bearer <token_gestor>`. |
| 2 | Informar payload no formato JSON contendo `{"localSigiloso": true}`. |
| 3 | Enviar a requisição para a API. |
| 4 | Consultar o código de status HTTP retornado e a resposta estruturada. |
| 5 | Consultar a base de dados para garantir que a flag `local_sigiloso` foi atualizada para `true` e que nenhum outro dado (cidade, tipo, nome, endereço) foi alterado. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| ID da Localidade | `d1000001-0000-0000-0000-000000000001` |
| Nome da Localidade | `Promotoria de Justiça da Cidadania` |
| Cidade (FK) | `c1d00001-0000-0000-0000-000000000001` (Campo Grande Alterada - Ativa) |
| Tipo de Localidade (FK) | `b1000001-0000-0000-0000-000000000001` (Promotoria de Justiça - Ativo) |
| CEP | `79004-311` |
| Logradouro | `Rua Ceará` |
| Número | `800` |
| Bairro | `Santa Fé` |
| Complemento | `Bloco B` |
| Local Sigiloso | `true` (Atualizado para booleano `true`) |
| Status / Ativo | `true` |
| Cabeçalho Authorization | `Bearer <token_gestor>` |
| Perfil do Usuário | Gestor |
| Método HTTP | `PATCH /api/v1/localidades/d1000001-0000-0000-0000-000000000001/sigilo` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Operação aceita com sucesso retornando código de status HTTP `200 OK` com o DTO `LocalidadeResponse`. |
| 2 | Atributo `localSigiloso` atualizado para `true` na base de dados. |
| 3 | Nenhum outro atributo da localidade (cidade, tipo, nome, endereço, ativo) sofre alteração. |
| 4 | Resposta estruturada (`LocalidadeResponse`) reflete o novo status de sigilo com integridade. |

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
| Observações | Atendimento ao critério de configuração pontual de sigilo estabelecido em HU-10.6 e Regra de Negócio RN-2. |
