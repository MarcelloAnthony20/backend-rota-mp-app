# Caso de Teste - Adicionar localidade com CEP inválido - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-LOC-009 |
| **Nome do Caso de Teste** | Adicionar localidade com CEP inválido |
| **Funcionalidade Testada** | Cadastro de Localidade / Validação de CEP |
| **Prioridade** | Alta |
| **Tipo de Teste** | Teste Negativo / Formato Inválido |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que o sistema rejeita o cadastro de localidade ao informar um CEP fora do formato regulamentar brasileiro (ex.: '123', contendo menos de 8 dígitos), impedindo a integração e a persistência na base. |

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
| 2 | Informar o payload com todos os atributos preenchidos, atribuindo ao campo CEP o valor malformado `'123'`. |
| 3 | Enviar a requisição com o cabeçalho `Authorization: Bearer <token_gestor>`. |
| 4 | Consultar o código de status HTTP retornado e a mensagem descritiva de erro. |
| 5 | Verificar na base de dados que a transação foi abortada e nenhum dado foi persistido. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| ID da Localidade | Não gerado (persistência rejeitada) |
| Nome da Localidade | `Promotoria de Justiça da Cidadania` |
| Cidade (FK) | `c1d00001-0000-0000-0000-000000000001` (Campo Grande Alterada) |
| Tipo de Localidade (FK) | `t1p00001-0000-0000-0000-000000000001` (Promotoria de Justiça) |
| CEP | `123` |
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
| 2 | Resposta RFC 7807 indicando explicitamente que o CEP informado possui formato inválido (exigido padrão de 8 dígitos numéricos). |
| 3 | Nenhuma consulta indevida aos serviços de CEP gera estado inconsistente e nenhum dado é salvo. |
| 4 | Validação estrutural de entrada executada antes do acionamento das regras de negócio. |

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
| Observações | Cenário negativo para validação sintática de formato de CEP brasileiro (8 dígitos numéricos) conforme RN-8 e HU-10.1. |
