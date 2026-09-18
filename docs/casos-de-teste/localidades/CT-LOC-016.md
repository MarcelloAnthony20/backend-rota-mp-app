# Caso de Teste - Adicionar localidade sem nome - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-LOC-016 |
| **Nome do Caso de Teste** | Adicionar localidade sem nome |
| **Funcionalidade Testada** | Cadastro de localidade / Validação de Campo Obrigatório |
| **Prioridade** | Alta |
| **Tipo de Teste** | Teste Negativo / Validação de Campo Obrigatório |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que o sistema rejeita o cadastro de localidade quando o campo nome é enviado vazio ou nulo, retornando erro de validação sem persistir dados. |

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
| 2 | Informar payload com o campo Nome vazio (`""`) e todos os demais atributos preenchidos com valores válidos. |
| 3 | Enviar a requisição para processamento. |
| 4 | Consultar o código de status HTTP retornado e o corpo da resposta. |
| 5 | Verificar na base de dados se algum registro foi persistido. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| ID da Localidade | Gerado automaticamente pelo sistema (nulo no envio) |
| Nome da Localidade | `""` (Vazio) |
| Cidade (FK) | `c1d00001-0000-0000-0000-000000000001` (Campo Grande Alterada) |
| Tipo de Localidade (FK) | `t1p00001-0000-0000-0000-000000000001` (Promotoria de Justiça) |
| CEP | `79002-000` |
| Logradouro | `Rua da Paz` |
| Número | `140` |
| Bairro | `Jardim dos Estados` |
| Complemento | `Edifício Sede` |
| Local Sigiloso | `false` |
| Status / Ativo | `true` |
| Cabeçalho Authorization | `Bearer <token_gestor>` |
| Método HTTP | `POST /api/v1/localidades` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Operação rejeitada pelo sistema retornando status HTTP `400 Bad Request` ou `422 Unprocessable Entity`. |
| 2 | Mensagem de erro clara informando que o campo nome da localidade é de preenchimento obrigatório (ProblemDetail RFC 7807). |
| 3 | Nenhum registro persistido na tabela `localidade` ou tabela de endereço (atomicidade garantida). |
| 4 | Nenhuma exceção não tratada ou stack trace é exposta na resposta da API. |

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
| Observações | Coluna `nome` NOT NULL na entidade `Localidade` conforme DER e HU-10.1. |
