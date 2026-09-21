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
| 3 | Tipo de localidade ativo previamente cadastrado no sistema (ID `b1000001-0000-0000-0000-000000000001` - Promotoria de Justiça). |
| 4 | Serviço de busca de CEP operacional. |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Acessar o endpoint de cadastro de localidade (`POST /api/v1/localidades`) via API REST ou interface gráfica. |
| 2 | Informar o payload da requisição (`CriarLocalidadeRequest`) com os seus 10 campos devidamente preenchidos: `nome`, `cidadeId`, `tipoLocalidadeId`, `cep`, `logradouro`, `numero`, `bairro`, `complemento`, `localSigiloso` e `ativo`, definindo o atributo `complemento` como `"" (Vazio)`. |
| 3 | Enviar a requisição com o cabeçalho de autenticação de Gestor (`Authorization: Bearer <token_gestor>`). |
| 4 | Consultar o código de status HTTP retornado e a integridade da resposta. |
| 5 | Verificar na base de dados a persistência da localidade e do endereço com complemento nulo. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| ID da Localidade | Gerado automaticamente pelo sistema (não enviado no payload de criação) |
| Nome da Localidade | `Promotoria de Justiça da Cidadania` |
| Cidade (FK) | `c1d00001-0000-0000-0000-000000000001` (Campo Grande Alterada) |
| Tipo de Localidade (FK) | `b1000001-0000-0000-0000-000000000001` (Promotoria de Justiça) |
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
| 2 | Resposta contendo o DTO completo da localidade criada (`LocalidadeResponse`), com o ID gerado automaticamente pelo sistema vindo no corpo da resposta e no cabeçalho `Location: /api/v1/localidades/{id}` (ex.: `d1000001-0000-0000-0000-000000000001`). |
| 3 | Endereço associado à localidade persistido com integridade; mesmo se enviado com máscara (`79002-000`), o CEP é sanitizado pelo backend e persistido/retornado com 8 dígitos numéricos (`79002000`) e com o atributo `complemento = null`. |
| 4 | Demais atributos persistidos exatamente com os valores informados na requisição. |
| 5 | Localidade gravada com status ativo (`ativo = true`) e relacionamentos com Cidade (`c1d00001-0000-0000-0000-000000000001`) e Tipo de Localidade (`b1000001-0000-0000-0000-000000000001`) consolidados no banco de dados. |

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
