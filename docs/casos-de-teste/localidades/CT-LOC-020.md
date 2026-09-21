# Caso de Teste - Validar preenchimento automático do logradouro e bairro via CEP - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-LOC-020 |
| **Nome do Caso de Teste** | Validar preenchimento automático do logradouro e bairro via CEP |
| **Funcionalidade Testada** | Integração com Serviço de CEP (RN-8) |
| **Prioridade** | Alta |
| **Tipo de Teste** | Teste de Integração / Teste Positivo |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que ao informar um CEP válido (`79002-000`) no cadastro de localidade, o sistema consulta a base postal e preenche automaticamente o logradouro e o bairro correspondentes, sanitizando o CEP. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil de Gestor ou superior. |
| 2 | Serviço de consulta de CEP operacional e acessível. |
| 3 | Cidade A cadastrada e ativa (`c1d00001-0000-0000-0000-000000000001` - Campo Grande Alterada). |
| 4 | Tipo de localidade cadastrado e ativo (`b1000001-0000-0000-0000-000000000001` - Promotoria de Justiça). |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Acessar o endpoint de cadastro de localidade (`POST /api/v1/localidades`). |
| 2 | Informar o payload da requisição (`CriarLocalidadeRequest` composto por 10 campos: `nome`, `cidadeId`, `tipoLocalidadeId`, `cep`, `logradouro`, `numero`, `bairro`, `complemento`, `localSigiloso` e `ativo`), preenchendo o CEP `79002-000` e os demais dados necessários, permitindo a resolução automática de logradouro e bairro pelo serviço postal. |
| 3 | Enviar a requisição para a API. |
| 4 | Consultar o código de status HTTP retornado e a resposta estruturada. |
| 5 | Conferir no banco de dados e no JSON de resposta os valores de logradouro e bairro gerados, bem como a sanitização do CEP. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| ID da Localidade | Gerado automaticamente pelo sistema (não enviado no payload de criação) |
| Nome da Localidade | `Promotoria de Justiça da Cidadania` |
| Cidade (FK) | `c1d00001-0000-0000-0000-000000000001` (Campo Grande Alterada) |
| Tipo de Localidade (FK) | `b1000001-0000-0000-0000-000000000001` (Promotoria de Justiça) |
| CEP | `79002-000` |
| Logradouro | `Rua da Paz` (preenchido automaticamente via CEP) |
| Número | `140` |
| Bairro | `Jardim dos Estados` (preenchido automaticamente via CEP) |
| Complemento | `Edifício Sede` |
| Local Sigiloso | `false` |
| Status / Ativo | `true` |
| Cabeçalho Authorization | `Bearer <token_gestor>` |
| Método HTTP | `POST /api/v1/localidades` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Operação realizada com sucesso retornando status HTTP `201 Created`. |
| 2 | Resposta contendo o DTO completo da localidade criada (`LocalidadeResponse`), com o ID gerado automaticamente pelo sistema vindo no corpo da resposta e no cabeçalho `Location: /api/v1/localidades/{id}` (ex.: `d1000001-0000-0000-0000-000000000001`). |
| 3 | O campo Logradouro é preenchido com `Rua da Paz` e o campo Bairro com `Jardim dos Estados` a partir da integração com a base oficial do CEP; mesmo se enviado com máscara (`79002-000`), o CEP é sanitizado pelo backend e persistido/retornado com 8 dígitos numéricos (`79002000`). |
| 4 | Os dados da localidade e do seu endereço são persistidos com integridade relacional e status ativo (`ativo = true`). |

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
| Observações | Atendimento à Regra de Negócio RN-8 e critério de aceitação de HU-10.1. |
