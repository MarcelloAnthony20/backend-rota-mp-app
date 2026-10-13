# Caso de Teste - Inativar Cidade B ativa - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-CID-011 |
| **Nome do Caso de Teste** | Inativar Cidade B ativa |
| **Funcionalidade Testada** | Inativação de cidade |
| **Prioridade** | Crítica |
| **Tipo de Teste** | Transição de Estado / Teste de Deleção |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que a Seed Cidade B (Dourados), atualmente ativa, é inativada com sucesso por um gestor autorizado via método DELETE, retornando status HTTP 204 No Content sem corpo de resposta. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil de Gestor ou Administrador. |
| 2 | A Seed Cidade B (ID `c1d00002-0000-0000-0000-000000000002`, Nome: `"Dourados"`, Código: `"5003702"`) existe na base de dados com status ativo (`ativo = true`). |
| 3 | Serviço de inativação operacional e acessível via API REST. |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Enviar requisição DELETE de inativação para a Seed Cidade B (`DELETE /api/v1/cidades/c1d00002-0000-0000-0000-000000000002`). |
| 2 | Informar o cabeçalho `Authorization: Bearer <token_gestor>`. |
| 3 | Consultar o código de status HTTP retornado e verificar a inexistência de payload na resposta. |
| 4 | Consultar o banco de dados e confirmar que o status da Seed Cidade B foi atualizado para inativo (`ativo = false`). |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| Entidade / Seed | Seed Cidade B |
| ID da Cidade | `c1d00002-0000-0000-0000-000000000002` |
| Nome da Cidade | `Dourados` |
| Código da Cidade | `5003702` |
| Status / Ativo | `Ativo (true)` (Transição para `Inativo (false)`) |
| Método HTTP | `DELETE` |
| Cabeçalho Authorization | `Bearer <token_gestor>` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Operação de inativação/deleção concluída com sucesso retornando status HTTP `204 No Content`. |
| 2 | Resposta sem corpo de dados (Empty Body), em estrita conformidade com a especificação HTTP 204. |
| 3 | Estado da Seed Cidade B no banco de dados é alterado para inativo (`ativo = false`) por meio de exclusão lógica (soft delete). |
| 4 | Registro preservado para integridade histórica com localidades e viagens já existentes. |

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
| Observações | Alinhado com a diretriz mandatória do pipeline: inativação da Seed Cidade B (`c1d00002-0000-0000-0000-000000000002`) retorna HTTP 204 No Content e estabelece o estado inativo para posterior validação de integridade referencial em localidades (CT-LOC-045 e CT-CID-036) conforme HU-08.4. |
