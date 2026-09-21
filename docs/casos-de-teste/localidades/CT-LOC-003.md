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
| 3 | Tipo de localidade ativo previamente cadastrado no sistema (ID `b1000001-0000-0000-0000-000000000001` - Promotoria de Justiça). |
| 4 | Serviço de integração e consulta de CEP operacional. |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Acessar o endpoint de cadastro de localidade (`POST /api/v1/localidades`) via API REST ou interface gráfica. |
| 2 | Informar o payload da requisição (`CriarLocalidadeRequest`) com os seus 10 campos devidamente preenchidos: `nome`, `cidadeId`, `tipoLocalidadeId`, `cep`, `logradouro`, `numero`, `bairro`, `complemento`, `localSigiloso` e `ativo`, preenchendo o `complemento` com `Bloco B - Sala 204`. |
| 3 | Enviar a requisição com o cabeçalho de autenticação de Gestor (`Authorization: Bearer <token_gestor>`). |
| 4 | Consultar o código de status HTTP retornado e a carga útil da resposta. |
| 5 | Consultar a base de dados para certificar que o complemento foi registrado integralmente sem truncamento. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| ID da Localidade | Gerado automaticamente pelo sistema (não enviado no payload de criação) |
| Nome da Localidade | `Promotoria de Justiça da Cidadania` |
| Cidade (FK) | `c1d00001-0000-0000-0000-000000000001` (Campo Grande Alterada) |
| Tipo de Localidade (FK) | `b1000001-0000-0000-0000-000000000001` (Promotoria de Justiça) |
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
| 2 | Resposta contendo o DTO completo da localidade criada (`LocalidadeResponse`), com o ID gerado automaticamente pelo sistema vindo no corpo da resposta e no cabeçalho `Location: /api/v1/localidades/{id}` (ex.: `d1000001-0000-0000-0000-000000000001`). |
| 3 | Endereço associado à localidade persistido com integridade; mesmo se enviado com máscara (`79002-000`), o CEP é sanitizado pelo backend e persistido/retornado com 8 dígitos numéricos (`79002000`) e com o campo complemento no valor exato `Bloco B - Sala 204`. |
| 4 | Localidade vinculada à Cidade (`c1d00001-0000-0000-0000-000000000001`) e ao Tipo de Localidade ativo (`b1000001-0000-0000-0000-000000000001`), gravada com status ativo (`ativo = true`). |

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
