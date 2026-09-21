# Caso de Teste - Cadastrar localidade sem perfil de Gestor - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-LOC-049 |
| **Nome do Caso de Teste** | Cadastrar localidade sem perfil de Gestor |
| **Funcionalidade Testada** | Cadastro de Localidade / Autorização RBAC |
| **Prioridade** | Crítica |
| **Tipo de Teste** | Segurança / Controle de Acesso (RBAC) / Teste Negativo |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que usuário autenticado no sistema sem perfil de Gestor (ex.: perfil Operador ou Usuário Comum) tem sua solicitação de cadastro de localidade bloqueada com código HTTP 403 Forbidden. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil Operador / Comum (sem permissão de escrita em localidades). |
| 2 | Cidade A ativa existente no sistema (ID: `c1d00001-0000-0000-0000-000000000001` - Campo Grande Alterada). |
| 3 | Tipo de localidade ativo cadastrado (ID: `b1000001-0000-0000-0000-000000000001` - Promotoria de Justiça). |
| 4 | Módulo de segurança Spring Security / RBAC ativo na API. |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Preparar a requisição `POST /api/v1/localidades` contendo o payload `CriarLocalidadeRequest` composto por 10 campos (`nome`, `cidadeId`, `tipoLocalidadeId`, `cep`, `logradouro`, `numero`, `bairro`, `complemento`, `localSigiloso`, `ativo`), com dados válidos da entidade associando Cidade A (`c1d00001-0000-0000-0000-000000000001`) e Tipo (`b1000001-0000-0000-0000-000000000001`). |
| 2 | Configurar o cabeçalho `Authorization: Bearer <token_operador>`. |
| 3 | Enviar a requisição para a API. |
| 4 | Consultar o código de status HTTP retornado e a mensagem de erro. |
| 5 | Verificar na base de dados que nenhuma localidade foi inserida. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| ID da Localidade | Gerado automaticamente pelo sistema (não enviado no payload de criação) |
| Nome da Localidade | `Posto Administrativo Não Autorizado` |
| Cidade (FK) | `c1d00001-0000-0000-0000-000000000001` (Campo Grande Alterada - Ativa) |
| Tipo de Localidade (FK) | `b1000001-0000-0000-0000-000000000001` (Promotoria de Justiça - Ativo) |
| CEP | `79002-000` |
| Logradouro | `Rua da Paz` |
| Número | `140` |
| Bairro | `Jardim dos Estados` |
| Complemento | `Térreo` |
| Local Sigiloso | `false` |
| Status / Ativo | `true` |
| Cabeçalho Authorization | `Bearer <token_operador>` |
| Perfil do Usuário | Operador / Usuário Comum (sem permissão de escrita) |
| Método HTTP | `POST /api/v1/localidades` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Requisição bloqueada retornando código de status HTTP `403 Forbidden`. |
| 2 | Mensagem de erro em formato RFC 7807 (ProblemDetail) informando ausência de privilégios para executar cadastros de localidades. |
| 3 | Nenhuma modificação é persistida na base de dados. |

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
| Observações | Atendimento ao critério de autorização RBAC de HU-10.1: apenas usuários com perfil de Gestor possuem permissão para inclusão de localidades. |
