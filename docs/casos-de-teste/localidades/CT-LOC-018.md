# Caso de Teste - Adicionar localidade com campos acima do limite - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-LOC-018 |
| **Nome do Caso de Teste** | Adicionar localidade com campos acima do limite |
| **Funcionalidade Testada** | Validação de Limites do DER |
| **Prioridade** | Alta |
| **Tipo de Teste** | Valor Limite / Teste Negativo |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar a rejeição de cadastro de localidade quando campos de texto, como o nome (com mais de 200 caracteres), ultrapassam os limites estritos do DER. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil de Gestor ou superior. |
| 2 | Cidade A cadastrada e ativa (`c1d00001-0000-0000-0000-000000000001` - Campo Grande Alterada). |
| 3 | Tipo de localidade cadastrado e ativo (`t1p00001-0000-0000-0000-000000000001` - Promotoria de Justiça). |
| 4 | Serviço de cadastro de localidades operacional. |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Acessar o endpoint de cadastro de localidade (`POST /api/v1/localidades`). |
| 2 | Informar payload preenchendo o campo Nome com string de 201 caracteres (> 200) e os outros 10 campos com dados válidos. |
| 3 | Enviar a requisição para a API. |
| 4 | Consultar o código de status HTTP retornado e a mensagem de validação. |
| 5 | Confirmar na base de dados que nenhum registro foi persistido. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| ID da Localidade | Gerado automaticamente pelo sistema (nulo no envio) |
| Nome da Localidade | `Promotoria de Justiça Especializada da Cidadania e Direitos Constitucionais de Defesa do Meio Ambiente e Patrimônio Público do Estado de Mato Grosso do Sul com Atuação Integrada e Coordenação Regional Extraordinária ABC` (201 caracteres) |
| Cidade (FK) | `c1d00001-0000-0000-0000-000000000001` (Campo Grande Alterada) |
| Tipo de Localidade (FK) | `t1p00001-0000-0000-0000-000000000001` (Promotoria de Justiça) |
| CEP | `79002-000` |
| Logradouro | `Rua da Paz` |
| Número | `140` |
| Bairro | `Jardim dos Estados` |
| Complemento | `Edifício Sede` |
| Local Sigiloso | `false` |
| Status / Ativo | `true` |
| Cabeçalho Authorization | `Bearer <token_gestor>` |
| Método HTTP | `POST /api/v1/localidades` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Operação rejeitada pelo sistema retornando status HTTP `400 Bad Request` ou `422 Unprocessable Entity`. |
| 2 | Mensagem de erro informativa (RFC 7807) indicando que o limite máximo de 200 caracteres para o campo nome foi excedido. |
| 3 | O banco de dados permanece protegido contra truncamento ou inserção de dados inconsistentes. |
| 4 | Nenhum dado é gravado na tabela `localidade`. |

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
| Observações | Limites definidos no DER: `nome` VARCHAR(200), `bairro` VARCHAR(100), `numero` VARCHAR(20), `complemento` VARCHAR(200) conforme HU-10.1. |
