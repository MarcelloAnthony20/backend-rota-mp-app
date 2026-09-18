# Caso de Teste - Alterar localidade com token inválido - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-LOC-037 |
| **Nome do Caso de Teste** | Alterar localidade com token inválido |
| **Funcionalidade Testada** | Alteração de Localidade / Validação Criptográfica JWT |
| **Prioridade** | Crítica |
| **Tipo de Teste** | Segurança / Autenticação |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que a tentativa de alterar uma localidade utilizando um token JWT corrompido, adulterado ou com assinatura criptográfica inválida é rejeitada com status HTTP 401 Unauthorized, sem permitir modificações no banco de dados. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Token JWT com payload adulterado ou com assinatura criptográfica não reconhecida pela chave pública/segredo da aplicação. |
| 2 | Localidade A existente cadastrada com ID `l0c00001-0000-0000-0000-000000000001`. |
| 3 | Filtro de segurança de autenticação JWT ativo. |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Enviar requisição `PUT /api/v1/localidades/l0c00001-0000-0000-0000-000000000001` contendo alterações cadastrais e cabeçalho `Authorization: Bearer <token_invalido>`. |
| 2 | Consultar o código de status HTTP retornado na resposta da requisição. |
| 3 | Validar que a operação foi sumariamente rejeitada pelo gateway/filtro de autenticação. |
| 4 | Consultar a base de dados e certificar que os dados originais da localidade permaneceram inalterados. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| ID da Localidade | `l0c00001-0000-0000-0000-000000000001` |
| Nome da Localidade | `Promotoria de Justiça da Cidadania Modificada` |
| Cidade (FK) | `c1d00001-0000-0000-0000-000000000001 - Campo Grande Alterada` |
| Tipo de Localidade (FK) | `t1p00001-0000-0000-0000-000000000001 - Promotoria de Justiça` |
| CEP | `79002-000` |
| Logradouro | `Rua da Paz` |
| Número | `140` |
| Bairro | `Jardim dos Estados` |
| Complemento | `Edifício Sede` |
| Local Sigiloso | `false` |
| Status / Ativo | `true` |
| Cabeçalho Authorization | `Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.token_invalido_adulterado` |
| Perfil do Usuário | `Indefinido (Token Inválido)` |
| Método HTTP | `PUT` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Operação sumariamente rejeitada retornando status HTTP `401 Unauthorized`. |
| 2 | Mensagem descritiva de erro informando falha na validação de assinatura ou integridade da credencial. |
| 3 | Nenhuma alteração é efetuada no registro da localidade na base de dados. |

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
| Observações | Validação de assinatura criptográfica e integridade de tokens JWT conforme HU-10.3 e padrões de segurança de API REST. |
