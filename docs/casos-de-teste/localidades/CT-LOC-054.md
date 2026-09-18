# Caso de Teste - Alterar CEP da localidade para novo CEP válido - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-LOC-054 |
| **Nome do Caso de Teste** | Alterar CEP da localidade para novo CEP válido |
| **Funcionalidade Testada** | Alteração de Endereço / Integração Automática por CEP (HU-10.3 / RN-8) |
| **Prioridade** | Alta |
| **Tipo de Teste** | Teste de Integração / Teste Positivo / Regra de Negócio |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que a alteração do CEP de uma localidade para um novo CEP válido consulta automaticamente o serviço de endereçamento, preenchendo e atualizando o Logradouro e o Bairro correspondentes com retorno HTTP 200 OK. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil de Gestor. |
| 2 | Localidade A existente no sistema com endereço anterior (ID: `l0c00001-0000-0000-0000-000000000001`, CEP: `79002-000`). |
| 3 | Serviço de busca de CEP operacional e configurado para resolver `79004-311` como Logradouro "Rua Ceará" e Bairro "Santa Fé". |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Preparar a requisição `PUT /api/v1/localidades/l0c00001-0000-0000-0000-000000000001` com cabeçalho `Authorization: Bearer <token_gestor>`. |
| 2 | Informar o novo CEP `79004-311`, novo número `800`, novo complemento `Bloco B` e manter os demais dados consistentes. |
| 3 | Enviar a requisição para a API. |
| 4 | Consultar o código de status HTTP retornado. |
| 5 | Verificar na resposta e no banco de dados se os campos Logradouro (`Rua Ceará`) e Bairro (`Santa Fé`) foram atualizados automaticamente. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| ID da Localidade | `l0c00001-0000-0000-0000-000000000001` |
| Nome da Localidade | `Promotoria de Justiça da Cidadania` |
| Cidade (FK) | `c1d00001-0000-0000-0000-000000000001` (Campo Grande Alterada - Ativa) |
| Tipo de Localidade (FK) | `t1p00001-0000-0000-0000-000000000001` (Promotoria de Justiça - Ativo) |
| CEP | `79004-311` (Novo CEP válido) |
| Logradouro | `Rua Ceará` (Atualizado automaticamente via CEP) |
| Número | `800` |
| Bairro | `Santa Fé` (Atualizado automaticamente via CEP) |
| Complemento | `Bloco B` |
| Local Sigiloso | `false` |
| Status / Ativo | `true` |
| Cabeçalho Authorization | `Bearer <token_gestor>` |
| Perfil do Usuário | Gestor |
| Método HTTP | `PUT /api/v1/localidades/l0c00001-0000-0000-0000-000000000001` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Operação aceita com sucesso retornando código de status HTTP `200 OK`. |
| 2 | Logradouro atualizado automaticamente para "Rua Ceará" e Bairro para "Santa Fé" a partir da resolução do novo CEP. |
| 3 | Resposta estruturada confirma os novos dados de endereço persistidos com integridade. |
| 4 | Histórico de modificação e consistência cadastral preservados. |

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
| Observações | Atendimento às diretrizes de HU-10.3 e Regra de Negócio RN-8 quanto ao preenchimento automático de logradouro e bairro via integração de CEP. |
