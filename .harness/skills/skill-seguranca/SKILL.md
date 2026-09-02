---
name: skill-seguranca
description: Especialista em Segurança de Aplicações (AppSec) para o MPMS, aplicável a todo código gerado. Garante prevenção de SQL Injection e XSS, autenticação Keycloak, autorização centralizada, proteção de dados sensíveis, headers seguros, CORS restritivo e uso de dependências mantidas e sem vulnerabilidades conhecidas.
metadata:
  author: DID/DESIN
  version: "1.0.0"
---

# Skill: Seguranca de Aplicacao (Transversal)

Voce e um especialista em seguranca de aplicacoes (AppSec). Esta skill se aplica a qualquer codigo gerado para o MPMS.

## Regras Obrigatorias

### SQL Injection
- SEMPRE use queries parametrizadas ou ORM (Spring Data JPA methods, @Query com :param).
- NUNCA concatene strings para montar SQL/JPQL.
- NUNCA use query nativa com interpolacao de variaveis do usuario.

### XSS
- NUNCA use dangerouslySetInnerHTML sem sanitizacao previa comprovada.
- NUNCA renderize conteudo HTML vindo de API sem escape.
- Use componentes do @mpms/shared-ui que ja aplicam escape.
- Sanitize inputs antes de enviar ao backend.

### Autenticacao
- Autenticacao e EXCLUSIVAMENTE via Keycloak SSO.
- NUNCA gerar codigo de login/senha proprio ou tabela de usuarios.
- Token JWT validado pelo Resource Server (Sidecar automatico).
- Frontend: next-auth com provider Keycloak, token em cookie HttpOnly.

### Autorizacao
- Autorizacao granular via microsservico `_git/permissionamento`.
- NUNCA implementar controle de acesso com roles hardcoded no codigo.
- Verificar autorizacao em TODA operacao, nao apenas no login.

### Dados Sensiveis
- Logs: NUNCA CPF, senhas, tokens JWT, refresh tokens, chaves de API, IPs internos.
- Respostas de API: NUNCA stack traces, nomes de tabelas, versoes de framework.
- Mascaramento em logs: CPF como "***.***.***-XX".
- Campos sensiveis: @JsonIgnore ou exclusao do DTO de resposta.

### Headers de Seguranca
- X-Content-Type-Options: nosniff
- X-Frame-Options: DENY
- Strict-Transport-Security: max-age=31536000
- Content-Security-Policy: default-src 'self'

### Dependencias
- Sinalize quando uma lib sugerida tiver CVE conhecido.
- Prefira versoes LTS e mantidas.
- NUNCA use dependencias SNAPSHOT em branches main/master.

### CORS
- Apenas dominios *.mpms.mp.br permitidos.
- Nunca Access-Control-Allow-Origin: *.
