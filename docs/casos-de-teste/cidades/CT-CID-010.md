# Caso de Teste - Ativar cidade inativa - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-CID-010 |
| **Nome do Caso de Teste** | Ativar cidade inativa |
| **Funcionalidade Testada** | Reativação de cidade |
| **Prioridade** | Alta |
| **Tipo de Teste** | Transição de Estado / Teste Positivo |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que uma cidade previamente inativada (Seed Cidade D - Corumbá) pode ser ativada com sucesso por um gestor autorizado via método PATCH na rota de ativação, alterando seu estado para ativo com retorno HTTP 200 OK. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil de Gestor ou Administrador. |
| 2 | Seed Cidade D (Inativa) cadastrada na base com ID `c1d00004-0000-0000-0000-000000000004`, Nome: `"Corumbá"`, Código: `"5003207"`. |
| 3 | A Seed Cidade D se encontra com status inativo (`ativo = false`) na base de dados. |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Enviar requisição PATCH para a rota de ativação da Seed Cidade D (`PATCH /api/v1/cidades/c1d00004-0000-0000-0000-000000000004/ativar`). |
| 2 | Informar o cabeçalho `Authorization: Bearer <token_gestor>`. |
| 3 | Consultar o código de status HTTP retornado e o payload da resposta. |
| 4 | Consultar a base de dados para confirmar a transição de estado da cidade para ativo (`ativo = true`). |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| Entidade / Seed | Seed Cidade D (Inativa) |
| ID da Cidade | `c1d00004-0000-0000-0000-000000000004` |
| Nome da Cidade | `Corumbá` |
| Código da Cidade | `5003207` |
| Status / Ativo | `Inativo (false)` (Transição para `Ativo (true)`) |
| Método HTTP | `PATCH` |
| Cabeçalho Authorization | `Bearer <token_gestor>` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Operação aceita com sucesso retornando status HTTP `200 OK`. |
| 2 | O status da Seed Cidade D no banco de dados é atualizado para ativo (`ativo = true`). |
| 3 | O payload da resposta retorna o DTO `CidadeResponse` com `id: "c1d00004-0000-0000-0000-000000000004"`, `nome: "Corumbá"`, `codigo: "5003207"` e `ativo: true`. |
| 4 | A cidade passa a ficar disponível para seleção e uso em cadastros operacionais do sistema. |

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
| Observações | Funcionalidade de reativação de cidade prevista no critério de aceitação de HU-08.4. Utiliza explicitamente a Seed Cidade D (`c1d00004-0000-0000-0000-000000000004`, Corumbá) inicializada com `ativo = false` pelo `CidadeDataInitializer` e o método PATCH implementado na API REST. |
