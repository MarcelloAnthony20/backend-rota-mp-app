# Caso de Teste - Adicionar localidade com CEP inexistente - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-LOC-010 |
| **Nome do Caso de Teste** | Adicionar localidade com CEP inexistente |
| **Funcionalidade Testada** | Cadastro de Localidade / Integração com Serviço de CEP |
| **Prioridade** | Alta |
| **Tipo de Teste** | Teste Negativo / Integração Externa |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que o sistema bloqueia o cadastro de localidade quando o CEP informado, embora estruturalmente válido em formato (8 dígitos), não existe nas bases oficiais dos Correios/ViaCEP (ex.: '99999-999'). |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil de Gestor ou Administrador (token JWT válido). |
| 2 | Cidade ativa previamente cadastrada no sistema (ID `c1d00001-0000-0000-0000-000000000001` - Campo Grande Alterada). |
| 3 | Tipo de localidade ativo previamente cadastrado no sistema (ID `b1000001-0000-0000-0000-000000000001` - Promotoria de Justiça). |
| 4 | Serviço externo de consulta de CEP ativo, operacional e acessível. |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Acessar o endpoint de cadastro de localidade (`POST /api/v1/localidades`) via API REST ou interface gráfica. |
| 2 | Informar o payload da requisição (`CriarLocalidadeRequest`) com os seus 10 campos (`nome`, `cidadeId`, `tipoLocalidadeId`, `cep`, `logradouro`, `numero`, `bairro`, `complemento`, `localSigiloso` e `ativo`), atribuindo ao campo `cep` o valor inexistente `'99999-999'`. |
| 3 | Enviar a requisição com o cabeçalho `Authorization: Bearer <token_gestor>`. |
| 4 | Consultar o código de status HTTP retornado e a mensagem descritiva do erro. |
| 5 | Verificar na base de dados a não persistência da entidade. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| ID da Localidade | Gerado automaticamente pelo sistema (não enviado no payload de criação) |
| Nome da Localidade | `Promotoria de Justiça da Cidadania` |
| Cidade (FK) | `c1d00001-0000-0000-0000-000000000001` (Campo Grande Alterada) |
| Tipo de Localidade (FK) | `b1000001-0000-0000-0000-000000000001` (Promotoria de Justiça) |
| CEP | `99999-999` |
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
| 1 | Operação rejeitada pelo sistema retornando status HTTP `400 Bad Request` ou `422 Unprocessable Entity`. |
| 2 | Mensagem de erro informativa no padrão RFC 7807 (Problem Details) indicando que o CEP informado não foi localizado na base postal de referência. |
| 3 | Registro de localidade e dados de endereço não persistidos no banco de dados (atomicidade garantida). |
| 4 | Falha externa tratada adequadamente sem provocar erro interno `500 Internal Server Error`. |

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
| Observações | Cenário negativo de integração externa validando resposta a CEPs inexistentes na base postal conforme HU-10.1. |
