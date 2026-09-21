# Caso de Teste - Alterar nome da localidade acima do limite - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-LOC-029 |
| **Nome do Caso de Teste** | Alterar nome da localidade acima do limite |
| **Funcionalidade Testada** | Alteração de Localidade / Limites do DER |
| **Prioridade** | Alta |
| **Tipo de Teste** | Valor Limite / Teste Negativo |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar a rejeição de alteração do nome da localidade quando a string enviada ultrapassa o limite máximo permitido de 200 caracteres (enviando 201 caracteres), preservando os dados originais. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil de Gestor ou superior. |
| 2 | Localidade A cadastrada (`d1000001-0000-0000-0000-000000000001`) com nome válido anterior (`Promotoria de Justiça da Cidadania`). |
| 3 | Serviço de alteração de localidade operacional. |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Acessar o endpoint de alteração de localidade (`PUT /api/v1/localidades/d1000001-0000-0000-0000-000000000001`). |
| 2 | Informar o payload completo atribuindo uma string de 201 caracteres ao campo Nome e mantendo os demais campos inalterados. |
| 3 | Enviar a requisição para a API. |
| 4 | Consultar o código de status HTTP retornado e a mensagem de validação. |
| 5 | Conferir no banco de dados se o nome original permaneceu intacto. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| ID da Localidade | `d1000001-0000-0000-0000-000000000001` |
| Nome da Localidade | `Promotoria de Justiça Especializada da Cidadania e Direitos Constitucionais de Defesa do Meio Ambiente e Patrimônio Público do Estado de Mato Grosso do Sul com Atuação Integrada e Coordenação Regional Extraordinária ABC` (201 caracteres) |
| Cidade (FK) | `c1d00001-0000-0000-0000-000000000001` (Campo Grande Alterada - mantido) |
| Tipo de Localidade (FK) | `b1000001-0000-0000-0000-000000000001` (Promotoria de Justiça - mantido) |
| CEP | `79002-000` (mantido) |
| Logradouro | `Rua da Paz` (mantido) |
| Número | `140` (mantido) |
| Bairro | `Jardim dos Estados` (mantido) |
| Complemento | `Edifício Sede` (mantido) |
| Local Sigiloso | `false` (mantido) |
| Status / Ativo | `true` (mantido) |
| Cabeçalho Authorization | `Bearer <token_gestor>` |
| Método HTTP | `PUT /api/v1/localidades/d1000001-0000-0000-0000-000000000001` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Operação rejeitada com status HTTP `400 Bad Request` ou `422 Unprocessable Entity`. |
| 2 | Resposta RFC 7807 informando que o tamanho máximo permitido para o campo nome (200 caracteres) foi excedido. |
| 3 | O registro no banco de dados não sofre alterações e o nome anterior é preservado integralmente. |
| 4 | Proteção contra truncamento no banco de dados garantida pela camada de validação. |

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
| Observações | Proteção contra truncamento em alteração conforme HU-10.3 e limites do DER (`nome` VARCHAR(200)). |
