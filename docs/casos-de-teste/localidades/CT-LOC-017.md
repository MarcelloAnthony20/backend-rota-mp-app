# Caso de Teste - Adicionar localidade com sigilo inválido - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-LOC-017 |
| **Nome do Caso de Teste** | Adicionar localidade com sigilo inválido |
| **Funcionalidade Testada** | Validação de Tipo de Dado / Sigilo |
| **Prioridade** | Alta |
| **Tipo de Teste** | Teste Negativo / Validação de Tipagem |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que o campo localSigiloso aceita exclusivamente valores booleanos, rejeitando strings não-booleanas ou tipos incompatíveis sem persistir os dados. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil de Gestor ou superior. |
| 2 | Cidade A cadastrada e ativa (`c1d00001-0000-0000-0000-000000000001` - Campo Grande Alterada). |
| 3 | Tipo de localidade cadastrado e ativo (`t1p00001-0000-0000-0000-000000000001` - Promotoria de Justiça). |
| 4 | Serviço de cadastro de localidades operacional. |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Acessar o endpoint de cadastro de localidade (`POST /api/v1/localidades`). |
| 2 | Informar payload preenchendo o campo Local Sigiloso com o valor não-booleano `"invalido"` e os outros 10 campos com dados válidos. |
| 3 | Enviar a requisição para a API. |
| 4 | Consultar o código de status HTTP retornado e a mensagem de erro. |
| 5 | Confirmar na base de dados que nenhum registro foi inserido. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| ID da Localidade | Gerado automaticamente pelo sistema (nulo no envio) |
| Nome da Localidade | `Promotoria de Justiça da Cidadania` |
| Cidade (FK) | `c1d00001-0000-0000-0000-000000000001` (Campo Grande Alterada) |
| Tipo de Localidade (FK) | `t1p00001-0000-0000-0000-000000000001` (Promotoria de Justiça) |
| CEP | `79002-000` |
| Logradouro | `Rua da Paz` |
| Número | `140` |
| Bairro | `Jardim dos Estados` |
| Complemento | `Edifício Sede` |
| Local Sigiloso | `"invalido"` (string não-booleana) |
| Status / Ativo | `true` |
| Cabeçalho Authorization | `Bearer <token_gestor>` |
| Método HTTP | `POST /api/v1/localidades` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Operação rejeitada pelo sistema retornando status HTTP `400 Bad Request` ou `422 Unprocessable Entity`. |
| 2 | Mensagem de erro estruturada (ProblemDetail RFC 7807) informando incompatibilidade de tipo de dado para o campo `localSigiloso`. |
| 3 | Nenhuma inserção é efetuada no banco de dados. |
| 4 | Nenhuma falha interna não tratada (500 Internal Server Error) é exposta ao cliente. |

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
| Observações | Atendimento ao critério de HU-10.1: "validar e aceitar exclusivamente valores booleanos para o campo localSigiloso". |
