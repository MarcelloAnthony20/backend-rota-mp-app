# Caso de Teste - Alterar cidade sem perfil de Gestor - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-CID-034 |
| **Nome do Caso de Teste** | Alterar cidade sem perfil de Gestor |
| **Funcionalidade Testada** | Segurança / Autorização RBAC na Alteração |
| **Prioridade** | Alta |
| **Tipo de Teste** | Segurança / Autorização / Teste Negativo |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que a tentativa de alteração dos dados cadastrais da Seed Cidade A por usuário autenticado que não possua perfil de Gestor (ex.: usuário com perfil de Operador) é rejeitada com status HTTP 403 Forbidden. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com credenciais válidas associadas ao perfil `Operador` (sem permissão administrativa de gestão de cidades). |
| 2 | Seed Cidade A existente e ativa na base de dados (ID `c1d00001-0000-0000-0000-000000000001`, Nome: `"Campo Grande"`, Código: `"5002704"`, `ativo = true`). |
| 3 | Endpoint de alteração de cidades operacional. |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Preparar requisição de alteração de dados para a Seed Cidade A (`PUT /api/v1/cidades/c1d00001-0000-0000-0000-000000000001`). |
| 2 | Montar payload JSON (`AtualizarCidadeRequest`) contendo `nome`, `codigo` e `ativo` (sem ID no corpo). |
| 3 | Incluir no cabeçalho `Authorization` o token JWT correspondente ao perfil `Operador`. |
| 4 | Enviar a requisição para processamento. |
| 5 | Consultar o código de status HTTP retornado pelo servidor. |
| 6 | Verificar na base de dados se os dados da Seed Cidade A permaneceram inalterados. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| Entidade / Seed | Seed Cidade A |
| ID da Cidade (Path Variable) | `c1d00001-0000-0000-0000-000000000001` |
| Nome da Cidade | `Campo Grande Modificada por Operador` |
| Código da Cidade | `5002704` |
| Status / Ativo | `Ativo (true)` |
| Payload da API (`AtualizarCidadeRequest`) | `{"nome": "Campo Grande Modificada por Operador", "codigo": "5002704", "ativo": true}` (sem ID no corpo) |
| Método HTTP | `PUT` |
| Cabeçalho Authorization | `Bearer <token_operador_valido>` |
| Perfil do Usuário | `Operador` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Requisição bloqueada pelo controle de acesso retornando status HTTP `403 Forbidden`. |
| 2 | Resposta de erro estruturada conforme RFC 7807 indicando privilégios insuficientes para alterar cidades. |
| 3 | Os dados da Seed Cidade A permanecem totalmente intactos na base de dados (`Campo Grande`, código `5002704`, ativo `true`). |

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
| Observações | Atendimento ao controle de perfis RBAC de HU-08.3: somente usuários com perfil de Gestor ou superior possuem permissão para alterar cidades cadastradas. Utiliza explicitamente a Seed Cidade A (`c1d00001-0000-0000-0000-000000000001`). |
