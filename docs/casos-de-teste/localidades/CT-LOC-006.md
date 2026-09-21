# Caso de Teste - Adicionar localidade sem tipo - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-LOC-006 |
| **Nome do Caso de Teste** | Adicionar localidade sem tipo |
| **Funcionalidade Testada** | Cadastro de Localidade / Integridade Referencial |
| **Prioridade** | Alta |
| **Tipo de Teste** | Teste Negativo / Validação de Obrigatório |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que o sistema bloqueia o cadastro de localidade quando o identificador de Tipo de Localidade (FK) é enviado vazio ("") ou ausente, assegurando a classificação institucional obrigatória. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil de Gestor ou Administrador (token JWT válido). |
| 2 | Cidade ativa previamente cadastrada no sistema (ID `c1d00001-0000-0000-0000-000000000001` - Campo Grande Alterada). |
| 3 | Serviço de cadastro de localidades operacional. |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Acessar o endpoint de cadastro de localidade (`POST /api/v1/localidades`) via API REST ou interface gráfica. |
| 2 | Informar o payload da requisição (`CriarLocalidadeRequest`) com os seus 10 campos (`nome`, `cidadeId`, `tipoLocalidadeId`, `cep`, `logradouro`, `numero`, `bairro`, `complemento`, `localSigiloso` e `ativo`), definindo o campo `tipoLocalidadeId` como `"" (Vazio)` ou ausente, mantendo os demais com valores válidos. |
| 3 | Enviar a requisição com o cabeçalho `Authorization: Bearer <token_gestor>`. |
| 4 | Consultar o código de status HTTP retornado e a estrutura do corpo de erro. |
| 5 | Confirmar na base de dados que nenhum registro foi gerado. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| ID da Localidade | Gerado automaticamente pelo sistema (não enviado no payload de criação) |
| Nome da Localidade | `Promotoria de Justiça da Cidadania` |
| Cidade (FK) | `c1d00001-0000-0000-0000-000000000001` (Campo Grande Alterada) |
| Tipo de Localidade (FK) | `"" (Vazio)` |
| CEP | `79002-000` |
| Logradouro | `Rua da Paz` |
| Número | `140` |
| Bairro | `Jardim dos Estados` |
| Complemento | `Edifício Sede` |
| Local Sigiloso | `false` |
| Status / Ativo | `Ativo (true)` |
| Cabeçalho Authorization | `Bearer <token_gestor>` |
| Método HTTP / Endpoint | `POST /api/v1/localidades` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Operação rejeitada pelo sistema com status HTTP `400 Bad Request` ou `422 Unprocessable Entity`. |
| 2 | Resposta no padrão RFC 7807 (Problem Details) indicando a obrigatoriedade da seleção de um Tipo de Localidade (`tipoLocalidadeId`). |
| 3 | Nenhuma entidade de localidade ou endereço persistida no banco de dados (atomicidade preservada). |
| 4 | Integridade de domínio e regras de negócio preservadas sem exposição de stack trace. |

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
| Observações | Cenário negativo para validação de tipo de localidade obrigatório conforme HU-10.1 e DER institucional. |
