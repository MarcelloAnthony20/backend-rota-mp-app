# Caso de Teste - Adicionar localidade sem número - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-LOC-007 |
| **Nome do Caso de Teste** | Adicionar localidade sem número |
| **Funcionalidade Testada** | Cadastro de Localidade / Validação de Endereço |
| **Prioridade** | Alta |
| **Tipo de Teste** | Teste Negativo / Validação de Campo Obrigatório |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que o sistema impede a inclusão de localidade quando o campo número do endereço predial é enviado em branco ou vazio (""), por se tratar de atributo obrigatório na composição do logradouro. |

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
| 2 | Informar o payload com os dados cadastrais da localidade, definindo o campo número como `"" (Vazio)`. |
| 3 | Enviar a requisição com cabeçalho `Authorization: Bearer <token_gestor>`. |
| 4 | Consultar o código de status HTTP retornado e a mensagem descritiva do erro. |
| 5 | Verificar na base de dados a ausência de registros inseridos. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| ID da Localidade | Não gerado (persistência rejeitada) |
| Nome da Localidade | `Promotoria de Justiça da Cidadania` |
| Cidade (FK) | `c1d00001-0000-0000-0000-000000000001` (Campo Grande Alterada) |
| Tipo de Localidade (FK) | `t1p00001-0000-0000-0000-000000000001` (Promotoria de Justiça) |
| CEP | `79002-000` |
| Logradouro | `Rua da Paz` |
| Número | `"" (Vazio)` |
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
| 2 | Mensagem de erro RFC 7807 indicando claramente que o número do endereço é obrigatório. |
| 3 | Nenhum registro salvo no banco de dados, garantindo atomicidade transacional. |
| 4 | Integridade cadastral assegurada sem criação de endereços incompletos. |

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
| Observações | Cenário negativo para validação de número predial obrigatório conforme regras de negócio de HU-10.1. |
