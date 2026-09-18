# Caso de Teste - Adicionar localidade com complemento - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-LOC-003 |
| **Nome do Caso de Teste** | Adicionar localidade com complemento |
| **Funcionalidade Testada** | Cadastro de Localidade |
| **Prioridade** | Média |
| **Tipo de Teste** | Teste Positivo / Campo Opcional |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar a persistência correta e integral do campo opcional complemento no endereço da localidade ao fornecer valor preenchido específico ('Bloco B - Sala 204'). |

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
| 2 | Informar o payload com todos os atributos da localidade, preenchendo o complemento com `Bloco B - Sala 204`. |
| 3 | Enviar a requisição com o cabeçalho de autenticação de Gestor. |
| 4 | Consultar o código de status HTTP retornado e a carga útil da resposta. |
| 5 | Consultar a base de dados para certificar que o complemento foi registrado integralmente sem truncamento. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| ID da Localidade | Auto-gerado pelo sistema (ex.: `l0c00001-0000-0000-0000-000000000001`) |
| Nome da Localidade | `Promotoria de Justiça da Cidadania` |
| Cidade (FK) | `c1d00001-0000-0000-0000-000000000001` (Campo Grande Alterada) |
| Tipo de Localidade (FK) | `t1p00001-0000-0000-0000-000000000001` (Promotoria de Justiça) |
| CEP | `79002-000` |
| Logradouro | `Rua da Paz` |
| Número | `140` |
| Bairro | `Jardim dos Estados` |
| Complemento | `Bloco B - Sala 204` |
| Local Sigiloso | `false` |
| Status / Ativo | `Ativo (true)` |
| Cabeçalho Authorization | `Bearer <token_gestor>` |
| Método HTTP / Endpoint | `POST /api/v1/localidades` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Operação realizada com sucesso retornando status HTTP `201 Created`. |
| 2 | Resposta contendo o endereço associado com o campo complemento no valor exato `Bloco B - Sala 204`. |
| 3 | Registro persistido no banco de dados com fidelidade cadastral e integridade referencial. |
| 4 | Localidade vinculada à Cidade A e ao Tipo de Localidade ativo, gravada com `ativo = true`. |

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
| Observações | Cenário positivo de validação de campo opcional de endereço de localidade conforme HU-10.1 e DER institucional. |
