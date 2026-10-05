-- =============================================================================
-- Migration UP: V20260928160200__modulo_localidades.sql
-- Módulo: Gestão de Cidades, Tipos de Localidade, Localidades e Endereços (HU-08, HU-09, HU-10)
-- Atomicidade: Criação isolada da estrutura de comarcas, tipos e instalações físicas
-- SGBD Alvo: PostgreSQL 16+
-- =============================================================================

-- 1. Tabela: cidade
CREATE TABLE cidade (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(200) NOT NULL,
    codigo VARCHAR(7) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uk_cidade_codigo UNIQUE (codigo)
);

COMMENT ON TABLE cidade IS 'Cadastro de municípios e comarcas de Mato Grosso do Sul atendidos pelo MPMS.';
COMMENT ON COLUMN cidade.id IS 'Identificador único (UUID) da cidade.';
COMMENT ON COLUMN cidade.nome IS 'Nome oficial do município.';
COMMENT ON COLUMN cidade.codigo IS 'Código IBGE de 7 dígitos.';
COMMENT ON COLUMN cidade.ativo IS 'Indicador de vigência/operação da cidade no sistema.';

-- 2. Tabela: tipo_localidade
CREATE TABLE tipo_localidade (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    codigo VARCHAR(50) NOT NULL,
    tipo_localidade VARCHAR(100) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uk_tipo_localidade_codigo UNIQUE (codigo)
);

COMMENT ON TABLE tipo_localidade IS 'Tipos e classificações de unidades do MPMS (PROJUS, SEDEADM, etc.).';
COMMENT ON COLUMN tipo_localidade.id IS 'Identificador único (UUID) do tipo de localidade.';
COMMENT ON COLUMN tipo_localidade.codigo IS 'Código identificador único (ex: PROJUS).';
COMMENT ON COLUMN tipo_localidade.tipo_localidade IS 'Nome por extenso do tipo de unidade institucional.';
COMMENT ON COLUMN tipo_localidade.ativo IS 'Indicador de vigência do tipo de localidade.';

-- 3. Tabela: localidade
CREATE TABLE localidade (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    fk_tipo_localidade UUID NOT NULL,
    fk_cidade UUID NOT NULL,
    nome VARCHAR(200) NOT NULL,
    local_sigiloso BOOLEAN NOT NULL DEFAULT FALSE,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_localidade_tipo_localidade FOREIGN KEY (fk_tipo_localidade) REFERENCES tipo_localidade (id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_localidade_cidade FOREIGN KEY (fk_cidade) REFERENCES cidade (id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT uk_localidade_cidade_tipo_nome UNIQUE (fk_cidade, fk_tipo_localidade, nome)
);

COMMENT ON TABLE localidade IS 'Locais de embarque, desembarque, promotorias e unidades operacionais.';
COMMENT ON COLUMN localidade.id IS 'Identificador único (UUID) da localidade.';
COMMENT ON COLUMN localidade.fk_tipo_localidade IS 'Chave estrangeira do tipo de classificação.';
COMMENT ON COLUMN localidade.fk_cidade IS 'Chave estrangeira da cidade/comarca de instalação.';
COMMENT ON COLUMN localidade.nome IS 'Nome descritivo da localidade institucional.';
COMMENT ON COLUMN localidade.local_sigiloso IS 'Indicador de sigilo operacional (RN-07: requer anonimização de endereço).';
COMMENT ON COLUMN localidade.ativo IS 'Indicador de vigência da unidade para viagens e atendimentos.';

-- 4. Tabela: endereco_localidade (Relação 1:1 com localidade)
CREATE TABLE endereco_localidade (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    logradouro VARCHAR(200) NOT NULL,
    numero VARCHAR(20) NOT NULL,
    cep VARCHAR(8) NOT NULL,
    bairro VARCHAR(100) NOT NULL,
    complemento VARCHAR(200),
    fk_cidade UUID NOT NULL,
    fk_localidade UUID NOT NULL,
    CONSTRAINT fk_endereco_localidade_cidade FOREIGN KEY (fk_cidade) REFERENCES cidade (id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_endereco_localidade_localidade FOREIGN KEY (fk_localidade) REFERENCES localidade (id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT uk_endereco_localidade_localidade UNIQUE (fk_localidade)
);

COMMENT ON TABLE endereco_localidade IS 'Endereço físico detalhado de cada localidade institucional.';
COMMENT ON COLUMN endereco_localidade.id IS 'Identificador único (UUID) do endereço.';
COMMENT ON COLUMN endereco_localidade.logradouro IS 'Logradouro oficial (rua, avenida, praça).';
COMMENT ON COLUMN endereco_localidade.numero IS 'Número predial (RN-10: não pode ser negativo).';
COMMENT ON COLUMN endereco_localidade.cep IS 'CEP postal com 8 dígitos numéricos.';
COMMENT ON COLUMN endereco_localidade.bairro IS 'Bairro do imóvel.';
COMMENT ON COLUMN endereco_localidade.complemento IS 'Complemento de endereço (bloco, sala, andar).';
COMMENT ON COLUMN endereco_localidade.fk_cidade IS 'Chave estrangeira associando ao município.';
COMMENT ON COLUMN endereco_localidade.fk_localidade IS 'Chave estrangeira 1:1 associando à respectiva localidade.';

-- 5. Índices B-Tree de Performance
CREATE INDEX idx_localidade_fk_tipo_localidade ON localidade (fk_tipo_localidade);
CREATE INDEX idx_localidade_fk_cidade ON localidade (fk_cidade);
CREATE INDEX idx_localidade_ativo ON localidade (ativo);
CREATE INDEX idx_endereco_localidade_fk_cidade ON endereco_localidade (fk_cidade);
CREATE INDEX idx_endereco_localidade_cep ON endereco_localidade (cep);
