-- =============================================================================
-- Migration UP: V20260928160400__modulo_motoristas.sql
-- Módulo: Gestão de Motoristas (HU-06, HU-07)
-- Atomicidade: Criação isolada da estrutura de escala e cadastro de condutores
-- SGBD Alvo: PostgreSQL 16+
-- =============================================================================

-- 1. Tabela: motorista
CREATE TABLE motorista (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    display_nome VARCHAR(200) NOT NULL,
    fk_localidade UUID NOT NULL,
    matricula VARCHAR(100) NOT NULL,
    foto VARCHAR(500),
    contato_pessoal VARCHAR(30) NOT NULL,
    contatos_funcional VARCHAR(30) NOT NULL,
    status VARCHAR(20) NOT NULL,
    fk_usuario UUID NOT NULL,
    CONSTRAINT fk_motorista_localidade FOREIGN KEY (fk_localidade) REFERENCES localidade (id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_motorista_usuario FOREIGN KEY (fk_usuario) REFERENCES usuario (id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT uk_motorista_usuario UNIQUE (fk_usuario),
    CONSTRAINT chk_motorista_status CHECK (status IN ('DISPONIVEL', 'INDISPONIVEL', 'INATIVO'))
);

COMMENT ON TABLE motorista IS 'Cadastro funcional de condutores oficiais e motoristas terceirizados vinculados ao MPMS.';
COMMENT ON COLUMN motorista.id IS 'Identificador único (UUID) do motorista.';
COMMENT ON COLUMN motorista.display_nome IS 'Nome de exibição funcional do motorista.';
COMMENT ON COLUMN motorista.fk_localidade IS 'Chave estrangeira da localidade/base operacional de atuação.';
COMMENT ON COLUMN motorista.matricula IS 'Número de matrícula ou registro funcional único.';
COMMENT ON COLUMN motorista.foto IS 'URI da fotografia do condutor para identificação do passageiro.';
COMMENT ON COLUMN motorista.contato_pessoal IS 'Telefone/celular de contato pessoal.';
COMMENT ON COLUMN motorista.contatos_funcional IS 'Telefone/ramal ou contato funcional de plantão.';
COMMENT ON COLUMN motorista.status IS 'Situação operacional (DISPONIVEL, INDISPONIVEL, INATIVO).';
COMMENT ON COLUMN motorista.fk_usuario IS 'Chave estrangeira 1:1 associando à credencial de acesso do usuário.';

-- 2. Índices B-Tree de Performance
CREATE INDEX idx_motorista_fk_localidade ON motorista (fk_localidade);
CREATE INDEX idx_motorista_status ON motorista (status);
CREATE INDEX idx_motorista_matricula ON motorista (matricula);
