# Caso de Teste - Adicionar localidade somente com obrigatórios - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-LOC-002 |
| **Nome do Caso de Teste** | Adicionar localidade somente com obrigatórios |
| **Funcionalidade Testada** | Cadastro de Localidade |
| **Prioridade** | Alta |
| **Tipo de Teste** | Teste Positivo / Campos Obrigatórios |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar o cadastro de localidade informando estritamente os campos obrigatórios da entidade e fornecendo o campo complementar opcional como vazio, garantindo que o sistema grave o complemento como nulo sem gerar inconsistências. |

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
| 2 | Informar o payload com todos os campos obrigatórios preenchidos e o campo complemento definido com valor `"" (Vazio)`. |
| 3 | Enviar a requisição com o cabeçalho `Authorization: Bearer <token_gestor>`. |
| 4 | Consultar o código de status HTTP retornado e o corpo da resposta. |
| 5 | Verificar na base de dados a persistência da localidade e a integridade do endereço com complemento nulo. |

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
| 1 | Operação realizada com sucesso retornando status HTTP `201 Created`. |
| 2 | Resposta contendo os dados da localidade cadastrada e cabeçalho `Location` gerado com sucesso. |
| 3 | Registro de endereço persistido no banco de dados com atributo `complemento = null`. |
| 4 | Demais atributos persistidos exatamente com os valores informados na requisição. |
| 5 | Localidade persistida com status ativo (`ativo = true`). |

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
| Observações | Cenário positivo de validação de preenchimento mínimo obrigatório conforme HU-10.1 e DER. |
