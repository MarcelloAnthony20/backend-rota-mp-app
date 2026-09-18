# Caso de Teste - Alterar cidade usando método HTTP não permitido - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-CID-020 |
| **Nome do Caso de Teste** | Alterar cidade usando método HTTP não permitido |
| **Funcionalidade Testada** | Alteração de cidade e método HTTP |
| **Prioridade** | Média |
| **Tipo de Teste** | Teste de Contrato / Método HTTP / Teste Negativo |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que a API rejeita requisições que utilizem método HTTP não suportado pelo endpoint de alteração por identificador (ex.: POST na rota `/api/v1/cidades/{id}` informando a Seed Cidade A), retornando status HTTP 405 Method Not Allowed. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil de Gestor ou superior. |
| 2 | Seed Cidade A existente e ativa na base de dados (ID `c1d00001-0000-0000-0000-000000000001`, Nome: `"Campo Grande"`, Código: `"5002704"`, `ativo = true`). |
| 3 | Serviço de gerenciamento de cidades operacional. |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Montar payload contendo dados para a Seed Cidade A. |
| 2 | Enviar a requisição para o endpoint `/api/v1/cidades/c1d00001-0000-0000-0000-000000000001` utilizando o método `POST`. |
| 3 | Consultar o código de status HTTP e os cabeçalhos retornados pelo servidor. |
| 4 | Verificar no banco de dados se os dados originais da Seed Cidade A permaneceram inalterados. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| Entidade / Seed | Seed Cidade A |
| ID da Cidade | `c1d00001-0000-0000-0000-000000000001` |
| Nome da Cidade | `Campo Grande Tentativa POST` |
| Código da Cidade | `5002704` |
| Status / Ativo | `Ativo (true)` |
| Método HTTP | `POST` (na rota `/api/v1/cidades/{id}`) |
| Cabeçalho Authorization | `Bearer <token_gestor_valido>` |
| Perfil do Usuário | `Gestor` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Requisição rejeitada pelo servidor retornando status HTTP `405 Method Not Allowed`. |
| 2 | Cabeçalho `Allow` é retornado na resposta especificando os métodos HTTP suportados (`GET, PUT, DELETE`). |
| 3 | Os dados da Seed Cidade A no banco de dados permanecem inalterados (`Campo Grande`, código `5002704`, ativo `true`). |
| 4 | Nenhuma exceção interna não tratada (HTTP 500) ou stack trace é exposta. |

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
| Observações | Atendimento ao critério de HU-08.3: retornar erro HTTP 405 Method Not Allowed caso a requisição utilize um método HTTP não suportado. Utiliza explicitamente a Seed Cidade A (`c1d00001-0000-0000-0000-000000000001`). |
