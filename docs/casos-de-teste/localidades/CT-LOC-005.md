# Caso de Teste - Adicionar localidade sem cidade - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-LOC-005 |
| **Nome do Caso de Teste** | Adicionar localidade sem cidade |
| **Funcionalidade Testada** | Cadastro de Localidade / Integridade Referencial |
| **Prioridade** | Alta |
| **Tipo de Teste** | Teste Negativo / Validação de Obrigatório |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que o sistema rejeita o cadastro de localidade quando o identificador de Cidade (FK) não é informado ou é enviado vazio (""), mantendo a integridade referencial do modelo de dados. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil de Gestor ou Administrador (token JWT válido). |
| 2 | Tipo de localidade ativo previamente cadastrado no sistema (ID `t1p00001-0000-0000-0000-000000000001` - Promotoria de Justiça). |
| 3 | Serviço de cadastro de localidades operacional. |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Acessar o endpoint de cadastro de localidade (`POST /api/v1/localidades`). |
| 2 | Informar o payload com todos os atributos preenchidos com valores válidos, exceto o campo Cidade (FK) definido como `"" (Vazio)`. |
| 3 | Enviar a requisição com o cabeçalho `Authorization: Bearer <token_gestor>`. |
| 4 | Consultar o código de status HTTP retornado e a estrutura do corpo de erro. |
| 5 | Verificar na base de dados se algum registro foi persistido indevidamente. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| ID da Localidade | Não gerado (persistência rejeitada) |
| Nome da Localidade | `Promotoria de Justiça da Cidadania` |
| Cidade (FK) | `"" (Vazio)` |
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
| 1 | Operação rejeitada pelo sistema retornando status HTTP `400 Bad Request` ou `422 Unprocessable Entity`. |
| 2 | Mensagem de erro informativa indicando que a vinculação da cidade é obrigatória (RFC 7807 Problem Details). |
| 3 | Nenhum registro de localidade ou endereço é persistido na base de dados (atomicidade garantida). |
| 4 | Nenhuma exceção de banco de dados ou stack trace exposta ao cliente. |

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
| Observações | Cenário negativo para validação de integridade referencial obrigatória: localidade exige vínculo com cidade válida e ativa conforme HU-10.1. |
