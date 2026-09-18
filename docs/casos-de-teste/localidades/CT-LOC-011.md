# Caso de Teste - Adicionar localidade com número acima do limite - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-LOC-011 |
| **Nome do Caso de Teste** | Adicionar localidade com número acima do limite |
| **Funcionalidade Testada** | Cadastro de Localidade / Validação de Limite |
| **Prioridade** | Média |
| **Tipo de Teste** | Teste Negativo / Valor Limite |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que o sistema impede a inclusão de localidade quando o campo número predial ultrapassa o limite máximo permitido de 20 caracteres (ex.: '123456789012345678901' com 21 caracteres). |

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
| 2 | Informar o payload com todos os atributos preenchidos, atribuindo ao campo número o valor `'123456789012345678901'` (21 caracteres). |
| 3 | Enviar a requisição com o cabeçalho `Authorization: Bearer <token_gestor>`. |
| 4 | Consultar o código de status HTTP retornado e a mensagem descritiva de erro. |
| 5 | Verificar na base de dados que a transação foi rejeitada sem persistência. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| ID da Localidade | Não gerado (persistência rejeitada) |
| Nome da Localidade | `Promotoria de Justiça da Cidadania` |
| Cidade (FK) | `c1d00001-0000-0000-0000-000000000001` (Campo Grande Alterada) |
| Tipo de Localidade (FK) | `t1p00001-0000-0000-0000-000000000001` (Promotoria de Justiça) |
| CEP | `79002-000` |
| Logradouro | `Rua da Paz` |
| Número | `123456789012345678901` |
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
| 2 | Resposta RFC 7807 indicando violação de tamanho máximo permitido para o campo número (limite: 20 caracteres). |
| 3 | Nenhum dado é gravado no banco de dados, prevenindo erros de estouro de coluna (DataTruncation). |
| 4 | Validação de restrição de tamanho executada na camada de aplicação. |

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
| Observações | Teste de valor limite para campo número do endereço conforme dicionário de dados do banco e HU-10.1. |
