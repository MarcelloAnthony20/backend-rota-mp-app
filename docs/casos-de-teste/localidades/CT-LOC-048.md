# Caso de Teste - Cadastrar localidade sigilosa com dados protegidos - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-LOC-048 |
| **Nome do Caso de Teste** | Cadastrar localidade sigilosa com dados protegidos |
| **Funcionalidade Testada** | Cadastro de Localidade / Tratamento de Sigilo (HU-10.6 / RN-2 / RN-7) |
| **Prioridade** | Alta |
| **Tipo de Teste** | Teste Positivo / Segurança e Sigilo |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que uma localidade sinalizada como sigilosa (`localSigiloso = true`) é cadastrada com sucesso com retorno HTTP 201 Created, ativando as diretivas de confidencialidade e proteção de dados de endereço desde a sua persistência inicial. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil de Gestor. |
| 2 | Cidade A ativa cadastrada no sistema (ID: `c1d00001-0000-0000-0000-000000000001` - Campo Grande Alterada). |
| 3 | Tipo de localidade ativo cadastrado no sistema (ID: `t1p00001-0000-0000-0000-000000000001` - Promotoria de Justiça). |
| 4 | Mecanismo de controle de acesso a dados sigilosos ativo no sistema. |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Preparar a requisição `POST /api/v1/localidades` com cabeçalho `Authorization: Bearer <token_gestor>`. |
| 2 | Preencher o payload com todos os atributos da entidade marcando o campo `localSigiloso = true`. |
| 3 | Enviar a requisição para o endpoint de cadastro de localidades. |
| 4 | Consultar o código de status HTTP retornado e o cabeçalho `Location`. |
| 5 | Consultar a base de dados para certificar que a localidade foi inserida com `local_sigiloso = true` e `ativo = true`. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| ID da Localidade | `l0c00048-0000-0000-0000-000000000048` (Gerado na persistência) |
| Nome da Localidade | `GAECO - Base Operacional Sigilosa` |
| Cidade (FK) | `c1d00001-0000-0000-0000-000000000001` (Campo Grande Alterada - Ativa) |
| Tipo de Localidade (FK) | `t1p00001-0000-0000-0000-000000000001` (Promotoria de Justiça - Ativo) |
| CEP | `79002-000` |
| Logradouro | `Rua da Paz` |
| Número | `500` |
| Bairro | `Jardim dos Estados` |
| Complemento | `Anexo Reservado` |
| Local Sigiloso | `true` |
| Status / Ativo | `true` |
| Cabeçalho Authorization | `Bearer <token_gestor>` |
| Perfil do Usuário | Gestor |
| Método HTTP | `POST /api/v1/localidades` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Cadastro aceito com sucesso retornando código de status HTTP `201 Created`. |
| 2 | Cabeçalho `Location` fornecido com a URI da nova localidade criada. |
| 3 | Resposta estruturada confirmando a persistência com `localSigiloso = true` e `ativo = true`. |
| 4 | Tratamento de confidencialidade ativado na entidade, restringindo a visualização pública de endereço para usuários sem permissão de sigilo (RN-2). |

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
| Observações | Atendimento às diretrizes de HU-10.1, HU-10.6 e Regras de Negócio RN-2 e RN-7 quanto à criação e tratamento seguro de dados sigilosos. |
