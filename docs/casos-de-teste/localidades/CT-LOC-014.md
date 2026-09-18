# Caso de Teste - Adicionar localidade com número negativo - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-LOC-014 |
| **Nome do Caso de Teste** | Adicionar localidade com número negativo |
| **Funcionalidade Testada** | Cadastro de Localidade / Validação Numérica |
| **Prioridade** | Baixa |
| **Tipo de Teste** | Teste Negativo / Valor Limite |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que o sistema bloqueia e rejeita a inclusão de valores numéricos negativos ('-140') no número predial do endereço da localidade, garantindo a integridade dos dados cadastrais. |

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
| 2 | Informar o payload com todos os atributos preenchidos, atribuindo ao campo número o valor negativo `'-140'`. |
| 3 | Enviar a requisição com o cabeçalho `Authorization: Bearer <token_gestor>`. |
| 4 | Analisar a resposta retornada pela API e o código de status HTTP. |
| 5 | Verificar na base de dados a inexistência de registros persistidos. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| ID da Localidade | Não gerado (persistência rejeitada) |
| Nome da Localidade | `Promotoria de Justiça da Cidadania` |
| Cidade (FK) | `c1d00001-0000-0000-0000-000000000001` (Campo Grande Alterada) |
| Tipo de Localidade (FK) | `t1p00001-0000-0000-0000-000000000001` (Promotoria de Justiça) |
| CEP | `79002-000` |
| Logradouro | `Rua da Paz` |
| Número | `-140` |
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
| 2 | Mensagem de erro informativa RFC 7807 indicando que valores negativos não são aceitos para número predial. |
| 3 | Nenhum registro salvo no banco de dados. |
| 4 | Regra de negócio de numeração predial positiva estritamente respeitada. |

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
| Observações | Cenário negativo de valor limite para validação de número predial estritamente positivo conforme HU-10.1. |
