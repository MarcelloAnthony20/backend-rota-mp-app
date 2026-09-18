# Caso de Teste - Consultar localidade sigilosa com perfil autorizado - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-LOC-051 |
| **Nome do Caso de Teste** | Consultar localidade sigilosa com perfil autorizado |
| **Funcionalidade Testada** | Consulta de Localidade Sigilosa / Acesso Privilegiado (HU-10.2 / HU-10.6 / RN-2) |
| **Prioridade** | Crítica |
| **Tipo de Teste** | Segurança / Acesso Privilegiado / Teste Positivo |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que usuário autenticado detentor de permissão de acesso a dados confidenciais (Gestor de Segurança) consegue consultar com sucesso os dados detalhados e o endereço completo de uma localidade sigilosa com retorno HTTP 200 OK. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil de Gestor de Segurança (possuidor do privilégio `LOCALIDADE_SIGILO_READ`). |
| 2 | Localidade sigilosa previamente cadastrada no sistema (ID: `l0c00048-0000-0000-0000-000000000048`, `localSigiloso = true`). |
| 3 | Configuração de controle de sigilo ativa no endpoint de consulta de localidade por ID. |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Preparar a requisição `GET /api/v1/localidades/l0c00048-0000-0000-0000-000000000048` com cabeçalho `Authorization: Bearer <token_gestor_seguranca>`. |
| 2 | Enviar a requisição para a API. |
| 3 | Consultar o código de status HTTP retornado. |
| 4 | Validar o payload retornado: certificar que os campos de endereço (CEP, logradouro, número, bairro e complemento) são exibidos integralmente sem qualquer máscara ou supressão. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| ID da Localidade | `l0c00048-0000-0000-0000-000000000048` |
| Nome da Localidade | `GAECO - Base Operacional Sigilosa` |
| Cidade (FK) | `c1d00001-0000-0000-0000-000000000001` (Campo Grande Alterada - Ativa) |
| Tipo de Localidade (FK) | `t1p00001-0000-0000-0000-000000000001` (Promotoria de Justiça - Ativo) |
| CEP | `79002-000` |
| Logradouro | `Rua da Paz` |
| Número | `500` |
| Bairro | `Jardim dos Estados` |
| Complemento | `Anexo Reservado` |
| Local Sigiloso | `true` |
| Status / Ativo | `true` |
| Cabeçalho Authorization | `Bearer <token_gestor_seguranca>` |
| Perfil do Usuário | Gestor de Segurança (com permissão `LOCALIDADE_SIGILO_READ`) |
| Método HTTP | `GET /api/v1/localidades/l0c00048-0000-0000-0000-000000000048` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | API retorna status HTTP `200 OK`. |
| 2 | Os campos de endereço completo (logradouro, número, bairro, CEP e complemento) são apresentados na resposta para o perfil com permissão de sigilo. |
| 3 | Flag `localSigiloso = true` é informada no DTO de resposta. |
| 4 | O status da localidade é apresentado como ativo (`ativo = true`). |

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
| Observações | Atendimento estrito aos critérios de confidencialidade de HU-10.2, HU-10.6 e Regra de Negócio RN-2. |
