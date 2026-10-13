# Caso de Teste - Impedir seleção ou vínculo de cidade inativa - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-CID-036 |
| **Nome do Caso de Teste** | Impedir seleção ou vínculo de cidade inativa |
| **Funcionalidade Testada** | Regra de Negócio / Integridade Operacional / Linhagem Contínua |
| **Prioridade** | Crítica |
| **Tipo de Teste** | Integração / Regra de Negócio / Teste Negativo / Fechamento de Pipeline |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que o sistema impede terminantemente a seleção ou vinculação de uma cidade com status inativo (Seed Cidade B: `c1d00002-0000-0000-0000-000000000002`, inativada no CT-CID-022) em novos cadastros de localidades e viagens operacionais, retornando HTTP 400 Bad Request ou 422 Unprocessable Entity, comprovando o fechamento do pipeline de Cidades. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil de Gestor ou superior. |
| 2 | Seed Cidade A existente e ativa na base de dados (ID `c1d00001-0000-0000-0000-000000000001`, Nome: `"Campo Grande"`, Código: `"5002704"`, `ativo = true`). |
| 3 | Seed Cidade B existente na base de dados e com inativação aplicada pelo teste CT-CID-022 (ID `c1d00002-0000-0000-0000-000000000002`, Nome: `"Dourados"`, Código: `"5003702"`, `ativo = false`). |
| 4 | Tipo de localidade ativo cadastrado no sistema (`t1p00001-0000-0000-0000-000000000001 - Promotoria de Justiça`). |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Acessar o endpoint de criação de localidade (`POST /api/v1/localidades`). |
| 2 | Montar payload de cadastro de localidade informando o campo `cidadeId` vinculado à Seed Cidade B inativa (`c1d00002-0000-0000-0000-000000000002`). |
| 3 | Enviar a requisição acompanhada do token de autorização de Gestor. |
| 4 | Consultar o código de status HTTP retornado e a estrutura da mensagem de erro. |
| 5 | Consultar o endpoint de cidades disponíveis para seleção operacional e verificar que a Seed Cidade B inativa não é listada, enquanto a Seed Cidade A ativa permanece selecionável. |
| 6 | Confirmar que nenhuma localidade com vínculo à Seed Cidade B inativa foi gravada no banco de dados. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| Entidade da Cidade Alvo | Seed Cidade B (Inativa) |
| ID da Cidade | `c1d00002-0000-0000-0000-000000000002` |
| Nome da Cidade | `Dourados` |
| Código da Cidade | `5003702` |
| Status / Ativo | `Inativo (false)` |
| Operação Alvo | `Cadastro de Localidade vinculada a Cidade Inativa` |
| Nome da Localidade de Teste | `Promotoria de Justiça de Teste de Vínculo` |
| Tipo de Localidade (FK) | `t1p00001-0000-0000-0000-000000000001` |
| CEP | `79800-000` |
| Logradouro | `Rua das Flores` |
| Número | `50` |
| Bairro | `Centro` |
| Complemento | `Bloco Único` |
| Local Sigiloso | `false` |
| Status da Localidade | `Ativo (true)` |
| Método HTTP | `POST` (em `/api/v1/localidades`) |
| Cabeçalho Authorization | `Bearer <token_gestor_valido>` |
| Perfil do Usuário | `Gestor` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Tentativa de vinculação rejeitada pelo sistema retornando status HTTP `400 Bad Request` ou `422 Unprocessable Entity`. |
| 2 | Mensagem de erro estruturada conforme RFC 7807 informa que cidades inativas não podem ser vinculadas a novas localidades ou operações. |
| 3 | A Seed Cidade B inativa é omitida dos seletores de opções de origem, destino e vinculação ativa de localidades. |
| 4 | Nenhum registro é gravado na base de dados. |
| 5 | Fechamento do pipeline de testes comprovado: Seed Cidade A ativa e disponível (`c1d00001-0000-0000-0000-000000000001`); Seed Cidade B inativa (`c1d00002-0000-0000-0000-000000000002 - Dourados`) com novos vínculos bloqueados. |

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
| Observações | Atendimento estrito à regra de negócio de HU-08.4 e sincronização com CT-LOC-045: "bloqueando sua seleção em opções de origem, destino e vinculação de novas localidades". Utiliza explicitamente a Seed Cidade B (`c1d00002-0000-0000-0000-000000000002`) e Seed Cidade A (`c1d00001-0000-0000-0000-000000000001`). Conclusão do pipeline da entidade Cidades. |
