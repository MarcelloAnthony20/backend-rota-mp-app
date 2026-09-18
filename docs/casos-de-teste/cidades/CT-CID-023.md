# Caso de Teste - Inativar cidade sem autenticação - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-CID-023 |
| **Nome do Caso de Teste** | Inativar cidade sem autenticação |
| **Funcionalidade Testada** | Segurança / Autenticação na Inativação |
| **Prioridade** | Alta |
| **Tipo de Teste** | Segurança / Autenticação / Teste Negativo |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que a tentativa de inativação da Seed Cidade B sem envio de credenciais de autenticação no cabeçalho HTTP é imediatamente bloqueada pelo sistema com status HTTP 401 Unauthorized. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Seed Cidade B cadastrada na base de dados (ID `c1d00002-0000-0000-0000-000000000002`, Nome: `"Dourados"`, Código: `"5003702"`). |
| 2 | Filtro de segurança e autenticação JWT da aplicação ativo e operacional. |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Construir requisição `DELETE /api/v1/cidades/c1d00002-0000-0000-0000-000000000002` sem fornecer o cabeçalho `Authorization`. |
| 2 | Enviar a requisição para o servidor. |
| 3 | Consultar o código de status HTTP retornado e a estrutura da mensagem de erro. |
| 4 | Verificar no banco de dados que o registro da Seed Cidade B não sofreu nenhuma alteração. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| Entidade / Seed | Seed Cidade B |
| ID da Cidade | `c1d00002-0000-0000-0000-000000000002` |
| Nome da Cidade | `Dourados` |
| Código da Cidade | `5003702` |
| Status / Ativo | `Ativo (true)` |
| Método HTTP | `DELETE` |
| Cabeçalho Authorization | `""` (Ausente / não informado) |
| Perfil do Usuário | `Não autenticado (Anônimo)` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Operação rejeitada pelo sistema retornando status HTTP `401 Unauthorized`. |
| 2 | Resposta de erro estruturada conforme RFC 7807 indicando ausência de credenciais válidas de autenticação. |
| 3 | Nenhuma alteração é processada ou persistida na base de dados, mantendo a Seed Cidade B íntegra. |

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
| Observações | Validação mandatória de proteção de endpoints contra acessos anônimos e integridade de controle de acesso conforme HU-08.4. Utiliza explicitamente a Seed Cidade B (`c1d00002-0000-0000-0000-000000000002`). |
