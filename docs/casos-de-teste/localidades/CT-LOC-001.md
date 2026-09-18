# Caso de Teste - Adicionar localidade completa - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-LOC-001 |
| **Nome do Caso de Teste** | Adicionar localidade completa |
| **Funcionalidade Testada** | Cadastro de Localidade |
| **Prioridade** | Crítica |
| **Tipo de Teste** | Teste Positivo / Fluxo Feliz |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar a inclusão com sucesso de uma nova localidade com todos os atributos preenchidos com dados válidos, incluindo dados de endereço completos e complementares, persistindo o registro com status ativo. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil de Gestor ou Administrador (token JWT válido). |
| 2 | Cidade ativa previamente cadastrada no sistema (ID `c1d00001-0000-0000-0000-000000000001` - Campo Grande Alterada). |
| 3 | Tipo de localidade ativo previamente cadastrado no sistema (ID `t1p00001-0000-0000-0000-000000000001` - Promotoria de Justiça). |
| 4 | Serviço de integração e consulta de CEP operacional. |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Acessar o endpoint de cadastro de localidade (`POST /api/v1/localidades`) via API REST ou interface gráfica. |
| 2 | Informar o payload completo com os 11 atributos da localidade devidamente preenchidos. |
| 3 | Enviar a requisição informando o cabeçalho de autorização correspondente ao perfil Gestor. |
| 4 | Consultar o código de status HTTP retornado e o corpo da resposta. |
| 5 | Verificar na base de dados a persistência da nova localidade e do seu respectivo endereço vinculado. |

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
| Complemento | `Edifício Sede` |
| Local Sigiloso | `false` |
| Status / Ativo | `Ativo (true)` |
| Cabeçalho Authorization | `Bearer <token_gestor>` |
| Método HTTP / Endpoint | `POST /api/v1/localidades` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Operação realizada com sucesso retornando status HTTP `201 Created`. |
| 2 | Resposta contendo o DTO completo da localidade criada, incluindo ID gerado (`l0c00001-0000-0000-0000-000000000001`) e cabeçalho `Location` apontando para o recurso criado. |
| 3 | Endereço completo associado à localidade persistido com integridade (`Rua da Paz, 140, Bairro Jardim dos Estados, Edifício Sede, CEP 79002-000`). |
| 4 | Registro da localidade persistido com status ativo (`ativo = true`). |
| 5 | Relacionamentos com Cidade (`c1d00001-0000-0000-0000-000000000001`) e Tipo de Localidade (`t1p00001-0000-0000-0000-000000000001`) consolidados no banco de dados. |

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
| Observações | Cenário principal de fluxo feliz para inclusão de localidade completa conforme HU-10.1 e DER institucional. |
