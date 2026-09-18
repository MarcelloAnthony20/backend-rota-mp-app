# Caso de Teste - Adicionar localidade sem bairro - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-LOC-008 |
| **Nome do Caso de Teste** | Adicionar localidade sem bairro |
| **Funcionalidade Testada** | Cadastro de Localidade / Validação de Endereço |
| **Prioridade** | Alta |
| **Tipo de Teste** | Teste Negativo / Validação de Campo Obrigatório |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que o sistema bloqueia o cadastro de localidade quando o campo bairro do endereço é enviado em branco ou vazio (""), exigindo o preenchimento desse atributo essencial. |

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
| 1 | Acessar o endpoint `POST /api/v1/localidades`. |
| 2 | Informar o payload com todos os atributos preenchidos com dados válidos, exceto o campo bairro configurado como `"" (Vazio)`. |
| 3 | Enviar a requisição com o cabeçalho `Authorization: Bearer <token_gestor>`. |
| 4 | Consultar o código de retorno HTTP e analisar a resposta de erro. |
| 5 | Confirmar na base de dados que nenhum registro foi persistido. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| ID da Localidade | Não gerado (persistência rejeitada) |
| Nome da Localidade | `Promotoria de Justiça da Cidadania` |
| Cidade (FK) | `c1d00001-0000-0000-0000-000000000001` (Campo Grande Alterada) |
| Tipo de Localidade (FK) | `t1p00001-0000-0000-0000-000000000001` (Promotoria de Justiça) |
| CEP | `79002-000` |
| Logradouro | `Rua da Paz` |
| Número | `140` |
| Bairro | `"" (Vazio)` |
| Complemento | `Edifício Sede` |
| Local Sigiloso | `false` |
| Status / Ativo | `Ativo (true)` |
| Cabeçalho Authorization | `Bearer <token_gestor>` |
| Método HTTP / Endpoint | `POST /api/v1/localidades` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Operação rejeitada pelo sistema retornando HTTP `400 Bad Request` ou `422 Unprocessable Entity`. |
| 2 | Resposta RFC 7807 contendo mensagem descritiva apontando a obrigatoriedade do campo bairro. |
| 3 | Nenhum registro persistido na base de dados (rollback garantido). |
| 4 | Integridade dos dados de localização mantida sem inserção de registros parciais. |

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
| Observações | Validação de campo obrigatório de endereço predial conforme HU-10.1 e DER institucional. |
