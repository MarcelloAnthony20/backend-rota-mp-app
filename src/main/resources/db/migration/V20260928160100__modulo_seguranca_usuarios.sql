-- =============================================================================
-- Migration UP: V20260928160100__modulo_seguranca_usuarios.sql
-- Módulo: Gestão de Acesso, Usuários, Perfis e Telas (HU-01, HU-02, HU-03)
-- Atomicidade: Criação isolada das entidades de controle de acesso e auditoria
-- SGBD Alvo: PostgreSQL 16+
-- =============================================================================

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- 1. Tabela: tema
CREATE TABLE tema (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tema BOOLEAN NOT NULL
);

COMMENT ON TABLE tema IS 'Preferência visual de tema da interface para cada usuário.';
COMMENT ON COLUMN tema.id IS 'Identificador único (UUID) do registro de tema.';
COMMENT ON COLUMN tema.tema IS 'Sinalizador booleano de preferência visual (true para tema escuro, false para claro).';

-- 2. Tabela: perfil
CREATE TABLE perfil (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(255) NOT NULL,
    descricao VARCHAR(255) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);

COMMENT ON TABLE perfil IS 'Perfis institucionais de acesso atribuídos a usuários do sistema.';
COMMENT ON COLUMN perfil.id IS 'Identificador único (UUID) do perfil.';
COMMENT ON COLUMN perfil.nome IS 'Nome descritivo do perfil institucional.';
COMMENT ON COLUMN perfil.descricao IS 'Descrição detalhada das responsabilidades e permissões do perfil.';
COMMENT ON COLUMN perfil.ativo IS 'Indicador se o perfil está disponível para atribuição.';

-- 3. Tabela: tela
CREATE TABLE tela (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(255) NOT NULL,
    rota VARCHAR(255) NOT NULL
);

COMMENT ON TABLE tela IS 'Mapeamento das telas e rotas da aplicação para controle de visibilidade.';
COMMENT ON COLUMN tela.id IS 'Identificador único (UUID) da tela.';
COMMENT ON COLUMN tela.nome IS 'Nome amigável da tela.';
COMMENT ON COLUMN tela.rota IS 'Caminho de rota da URL associada à tela.';

-- 4. Tabela: perfil_tela (Tabela Associativa N:M)
CREATE TABLE perfil_tela (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    fk_perfil UUID NOT NULL,
    fk_tela UUID NOT NULL,
    CONSTRAINT fk_perfil_tela_perfil FOREIGN KEY (fk_perfil) REFERENCES perfil (id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_perfil_tela_tela FOREIGN KEY (fk_tela) REFERENCES tela (id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT uk_perfil_tela UNIQUE (fk_perfil, fk_tela)
);

COMMENT ON TABLE perfil_tela IS 'Junção entre Perfis e Telas para definição de permissões de visualização.';
COMMENT ON COLUMN perfil_tela.id IS 'Identificador único (UUID) da associação perfil-tela.';
COMMENT ON COLUMN perfil_tela.fk_perfil IS 'Chave estrangeira do perfil associado.';
COMMENT ON COLUMN perfil_tela.fk_tela IS 'Chave estrangeira da tela associada.';

-- 5. Tabela: usuario
CREATE TABLE usuario (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(200) NOT NULL,
    email VARCHAR(150) NOT NULL,
    mail_nickname VARCHAR(100) NOT NULL,
    fk_tema UUID NOT NULL,
    fk_perfil UUID NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    data_criacao TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT fk_usuario_tema FOREIGN KEY (fk_tema) REFERENCES tema (id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_usuario_perfil FOREIGN KEY (fk_perfil) REFERENCES perfil (id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT uk_usuario_email UNIQUE (email),
    CONSTRAINT uk_usuario_mail_nickname UNIQUE (mail_nickname)
);

COMMENT ON TABLE usuario IS 'Cadastro institucional de usuários e colaboradores sincronizados via Keycloak/SSO.';
COMMENT ON COLUMN usuario.id IS 'Identificador único (UUID) do usuário.';
COMMENT ON COLUMN usuario.nome IS 'Nome completo institucional do colaborador.';
COMMENT ON COLUMN usuario.email IS 'E-mail corporativo único institucional (@mpms.mp.br).';
COMMENT ON COLUMN usuario.mail_nickname IS 'Identificador de rede (login LDAP/AD) único.';
COMMENT ON COLUMN usuario.fk_tema IS 'Chave estrangeira do tema preferencial selecionado.';
COMMENT ON COLUMN usuario.fk_perfil IS 'Chave estrangeira do perfil institucional concedido.';
COMMENT ON COLUMN usuario.ativo IS 'Indicador se a conta do usuário tem permissão para operar o sistema.';
COMMENT ON COLUMN usuario.data_criacao IS 'Carimbo de data/hora de criação do usuário.';
COMMENT ON COLUMN usuario.data_atualizacao IS 'Carimbo de data/hora da última atualização de dados.';

-- 6. Índices B-Tree de Performance
CREATE INDEX idx_perfil_tela_fk_perfil ON perfil_tela (fk_perfil);
CREATE INDEX idx_perfil_tela_fk_tela ON perfil_tela (fk_tela);
CREATE INDEX idx_usuario_fk_tema ON usuario (fk_tema);
CREATE INDEX idx_usuario_fk_perfil ON usuario (fk_perfil);
CREATE INDEX idx_usuario_ativo ON usuario (ativo);

-- 7. Função e Trigger de Auditoria para data_atualizacao
CREATE OR REPLACE FUNCTION fn_atualizar_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.data_atualizacao = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_usuario_data_atualizacao
BEFORE UPDATE ON usuario
FOR EACH ROW
EXECUTE FUNCTION fn_atualizar_timestamp();
