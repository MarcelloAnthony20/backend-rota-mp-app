# Caso de Teste - Adicionar localidade com caracteres especiais no número - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-LOC-013 |
| **Nome do Caso de Teste** | Adicionar localidade com caracteres especiais no número |
| **Funcionalidade Testada** | Cadastro de Localidade / Higienização de Dados |
| **Prioridade** | Média |
| **Tipo de Teste** | Teste Negativo / Segurança e Sanitização |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que o sistema bloqueia e rejeita a inclusão de caracteres especiais e símbolos inválidos ('#@$!%') no campo número predial, evitando injeções de conteúdo malicioso e inconsistências na base. |

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
| 2 | Informar o payload com todos os atributos preenchidos, atribuindo ao campo número o valor com símbolos `'#@$!%'`. |
| 3 | Enviar a requisição com o cabeçalho `Authorization: Bearer <token_gestor>`. |
| 4 | Consultar o código de status HTTP retornado e o detalhamento do erro. |
| 5 | Verificar na base de dados que a requisição foi abortada sem persistência. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| ID da Localidade | Não gerado (persistência rejeitada) |
| Nome da Localidade | `Promotoria de Justiça da Cidadania` |
| Cidade (FK) | `c1d00001-0000-0000-0000-000000000001` (Campo Grande Alterada) |
| Tipo de Localidade (FK) | `t1p00001-0000-0000-0000-000000000001` (Promotoria de Justiça) |
| CEP | `79002-000` |
| Logradouro | `Rua da Paz` |
| Número | `#@$!%` |
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
| 2 | Detalhamento do erro conforme RFC 7807 indicando a presença de caracteres inválidos no campo número predial. |
| 3 | Bloqueio efetivo na camada de entrada antes de qualquer comunicação com o banco de dados. |
| 4 | Preservação da segurança, integridade e sanitização da base de dados. |

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
| Observações | Cenário de teste negativo voltado a validação de sanitização e caracteres especiais no endereço conforme HU-10.1. |
