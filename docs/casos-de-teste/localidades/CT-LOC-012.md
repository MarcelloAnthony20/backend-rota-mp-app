# Caso de Teste - Adicionar localidade com letras no número - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-LOC-012 |
| **Nome do Caso de Teste** | Adicionar localidade com letras no número |
| **Funcionalidade Testada** | Cadastro de Localidade / Validação de Tipagem |
| **Prioridade** | Média |
| **Tipo de Teste** | Teste Negativo / Partição de Equivalência |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar a rejeição pelo sistema ao submeter caracteres alfanuméricos contendo letras no campo número predial ('140-A'), assegurando a integridade da tipagem estrita caso o padrão numérico seja exigido pela especificação. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil de Gestor ou Administrador (token JWT válido). |
| 2 | Cidade ativa previamente cadastrada no sistema (ID `c1d00001-0000-0000-0000-000000000001` - Campo Grande Alterada). |
| 3 | Tipo de localidade ativo previamente cadastrado no sistema (ID `b1000001-0000-0000-0000-000000000001` - Promotoria de Justiça). |
| 4 | Serviço de integração de CEP operacional. |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Acessar o endpoint de cadastro de localidade (`POST /api/v1/localidades`) via API REST ou interface gráfica. |
| 2 | Informar o payload da requisição (`CriarLocalidadeRequest`) com os seus 10 campos (`nome`, `cidadeId`, `tipoLocalidadeId`, `cep`, `logradouro`, `numero`, `bairro`, `complemento`, `localSigiloso` e `ativo`), atribuindo ao campo `numero` o valor com letras `'140-A'`. |
| 3 | Enviar a requisição com o cabeçalho `Authorization: Bearer <token_gestor>`. |
| 4 | Consultar o código de status HTTP retornado e a mensagem descritiva de erro. |
| 5 | Verificar na base de dados que a transação foi rejeitada sem persistência. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| ID da Localidade | Gerado automaticamente pelo sistema (não enviado no payload de criação) |
| Nome da Localidade | `Promotoria de Justiça da Cidadania` |
| Cidade (FK) | `c1d00001-0000-0000-0000-000000000001` (Campo Grande Alterada) |
| Tipo de Localidade (FK) | `b1000001-0000-0000-0000-000000000001` (Promotoria de Justiça) |
| CEP | `79002-000` |
| Logradouro | `Rua da Paz` |
| Número | `140-A` |
| Bairro | `Jardim dos Estados` |
| Complemento | `Edifício Sede` |
| Local Sigiloso | `false` |
| Status / Ativo | `Ativo (true)` |
| Cabeçalho Authorization | `Bearer <token_gestor>` |
| Método HTTP / Endpoint | `POST /api/v1/localidades` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Operação rejeitada pelo sistema retornando status HTTP `400 Bad Request` ou `422 Unprocessable Entity`. |
| 2 | Resposta de erro estruturada no padrão RFC 7807 (Problem Details) indicando que o campo número deve conter exclusivamente dígitos numéricos. |
| 3 | Registro de localidade e endereço não persistidos no banco de dados (atomicidade garantida). |
| 4 | Preservação da integridade de tipagem e padronização dos dados de endereço sem vazamento de exceções internas. |

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
| Observações | Cenário negativo de partição de equivalência para validação de caracteres alfabéticos no campo número conforme HU-10.1. |
