# Caso de Teste - Cadastrar cidade com payload nulo ou JSON malformado - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-CID-028 |
| **Nome do Caso de Teste** | Cadastrar cidade com payload nulo ou JSON malformado |
| **Funcionalidade Testada** | Cadastro de cidade / Validação de Sintaxe |
| **Prioridade** | Alta |
| **Tipo de Teste** | Teste Negativo / Validação de Payload / Formato |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que o envio de requisição de cadastro de cidade com payload vazio, corpo nulo ou JSON sintaticamente malformado é rejeitado com status HTTP 400 Bad Request, sem disparar falhas internas não tratadas (HTTP 500). |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil de Gestor ou superior. |
| 2 | Endpoint de cadastro de cidades disponível e operacional (`POST /api/v1/cidades`). |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Preparar requisição `POST /api/v1/cidades` contendo corpo de dados malformado (ex.: JSON truncado sem fechamento de chaves ou corpo nulo). |
| 2 | Enviar a requisição acompanhada do token de autorização de Gestor. |
| 3 | Consultar o código de status HTTP retornado pelo servidor. |
| 4 | Validar o corpo da resposta de erro estruturada. |
| 5 | Verificar que nenhuma gravação residual ocorreu no banco de dados. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| ID da Cidade | Gerado automaticamente pelo sistema (não enviado no payload de criação) |
| Nome da Cidade | `""` (Payload malformado) |
| Código da Cidade | `""` (Payload malformado) |
| Status / Ativo | `""` (Payload malformado) |
| Payload Bruto | `{"nome": "Naviraí", "codigo": ` (JSON sintaticamente inválido) |
| Método HTTP | `POST` |
| Cabeçalho Authorization | `Bearer <token_gestor_valido>` |
| Perfil do Usuário | `Gestor` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Operação rejeitada pelo parser de requisições retornando status HTTP `400 Bad Request`. |
| 2 | Resposta de erro estruturada conforme RFC 7807 indicando corpo de requisição ilegível ou erro de sintaxe JSON. |
| 3 | Nenhuma falha de infraestrutura interna (HTTP 500) ou stack trace é exposta. |
| 4 | Nenhum dado é inserido no banco de dados. |

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
| Observações | Validação de robustez do desserializador JSON e proteção de integridade da API conforme RFC 7807 e HU-08.1. |
