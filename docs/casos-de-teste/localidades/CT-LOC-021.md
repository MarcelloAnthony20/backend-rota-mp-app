# Caso de Teste - Impedir alteração indevida do logradouro - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-LOC-021 |
| **Nome do Caso de Teste** | Impedir alteração indevida do logradouro |
| **Funcionalidade Testada** | Integridade de Endereço por CEP (RN-8) |
| **Prioridade** | Alta |
| **Tipo de Teste** | Integridade / Regra de Negócio |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que o sistema impede a gravação de logradouro não confirmado ou conflitante com a base oficial do CEP informado, assegurando a integridade cadastral do endereço. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil de Gestor ou superior. |
| 2 | Serviço postal de consulta de CEP operacional (CEP `79002-000` correspondente a `Rua da Paz`). |
| 3 | Cidade A cadastrada e ativa (`c1d00001-0000-0000-0000-000000000001` - Campo Grande Alterada). |
| 4 | Tipo de localidade cadastrado e ativo (`t1p00001-0000-0000-0000-000000000001` - Promotoria de Justiça). |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Acessar o endpoint de cadastro de localidade (`POST /api/v1/localidades`). |
| 2 | Informar payload com CEP `79002-000` e enviar manualmente um valor de Logradouro arbitrário e divergente (`Rua Inventada Incompatível`). |
| 3 | Enviar a requisição para processamento. |
| 4 | Consultar o código de status HTTP retornado e a estrutura da resposta. |
| 5 | Conferir na base de dados se a tentativa de gravação de logradouro inconsistente foi bloqueada ou corrigida pelo valor oficial. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| ID da Localidade | Gerado automaticamente pelo sistema (nulo no envio) |
| Nome da Localidade | `Promotoria de Justiça da Cidadania` |
| Cidade (FK) | `c1d00001-0000-0000-0000-000000000001` (Campo Grande Alterada) |
| Tipo de Localidade (FK) | `t1p00001-0000-0000-0000-000000000001` (Promotoria de Justiça) |
| CEP | `79002-000` |
| Logradouro | `Rua Inventada Incompatível` (valor divergente do oficial da base postal) |
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
| 1 | O sistema rejeita a operação com status HTTP `400 Bad Request` ou `422 Unprocessable Entity` (ou sobrepõe automaticamente pelo logradouro oficial `Rua da Paz`). |
| 2 | Mensagem de erro informa incompatibilidade entre o logradouro enviado e a base oficial do CEP informado (RFC 7807). |
| 3 | Nenhum logradouro falso ou desvinculado da base oficial dos Correios é persistido no banco de dados. |
| 4 | A consistência e integridade do endereço da localidade são preservadas. |

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
| Observações | Atendimento ao critério de HU-10.1: "o sistema não deve preencher logradouro não confirmado". |
