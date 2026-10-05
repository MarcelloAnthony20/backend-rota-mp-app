-- =============================================================================
-- Migration UP: V20260928160300__modulo_veiculos.sql
-- Módulo: Gestão da Frota de Veículos, Modelos e Fotografias (HU-04, HU-05)
-- Atomicidade: Criação isolada da estrutura de catálogo de veículos e fotos
-- SGBD Alvo: PostgreSQL 16+
-- =============================================================================

-- 1. Tabela: modelo_veiculo
CREATE TABLE modelo_veiculo (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    marca VARCHAR(100) NOT NULL,
    modelo VARCHAR(100) NOT NULL,
    num_passageiro INTEGER NOT NULL,
    logo VARCHAR(500),
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);

COMMENT ON TABLE modelo_veiculo IS 'Catálogo de marcas e modelos de veículos da frota institucional.';
COMMENT ON COLUMN modelo_veiculo.id IS 'Identificador único (UUID) do modelo de veículo.';
COMMENT ON COLUMN modelo_veiculo.marca IS 'Marca/fabricante do veículo (ex: Toyota, Chevrolet).';
COMMENT ON COLUMN modelo_veiculo.modelo IS 'Modelo específico do veículo (ex: Corolla, Hilux).';
COMMENT ON COLUMN modelo_veiculo.num_passageiro IS 'Capacidade total de passageiros acomodados.';
COMMENT ON COLUMN modelo_veiculo.logo IS 'URI ou URL da imagem do logotipo/ilustração do modelo.';
COMMENT ON COLUMN modelo_veiculo.ativo IS 'Indicador se o modelo está ativo para cadastro de novos veículos.';

-- 2. Tabela: veiculo
CREATE TABLE veiculo (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    fk_localidade UUID NOT NULL,
    fk_modelo UUID NOT NULL,
    placa VARCHAR(10) NOT NULL,
    ano INTEGER NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_veiculo_localidade FOREIGN KEY (fk_localidade) REFERENCES localidade (id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_veiculo_modelo FOREIGN KEY (fk_modelo) REFERENCES modelo_veiculo (id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT uk_veiculo_placa UNIQUE (placa)
);

COMMENT ON TABLE veiculo IS 'Unidades veiculares pertencentes à frota do MPMS.';
COMMENT ON COLUMN veiculo.id IS 'Identificador único (UUID) do veículo.';
COMMENT ON COLUMN veiculo.fk_localidade IS 'Chave estrangeira da localidade/garagem base de lotação.';
COMMENT ON COLUMN veiculo.fk_modelo IS 'Chave estrangeira do modelo cadastrado.';
COMMENT ON COLUMN veiculo.placa IS 'Placa de identificação veicular oficial (formato Mercosul ou padrão antigo).';
COMMENT ON COLUMN veiculo.ano IS 'Ano de fabricação/modelo do veículo.';
COMMENT ON COLUMN veiculo.ativo IS 'Indicador se o veículo está ativo e apto para viagens.';

-- 3. Tabela: foto_veiculo
CREATE TABLE foto_veiculo (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    foto VARCHAR(500) NOT NULL,
    fk_veiculo UUID NOT NULL,
    CONSTRAINT fk_foto_veiculo_veiculo FOREIGN KEY (fk_veiculo) REFERENCES veiculo (id) ON UPDATE CASCADE ON DELETE CASCADE
);

COMMENT ON TABLE foto_veiculo IS 'Registro de fotos e registros visuais do estado dos veículos da frota.';
COMMENT ON COLUMN foto_veiculo.id IS 'Identificador único (UUID) da foto.';
COMMENT ON COLUMN foto_veiculo.foto IS 'URI/caminho de armazenamento da fotografia.';
COMMENT ON COLUMN foto_veiculo.fk_veiculo IS 'Chave estrangeira do veículo ao qual a foto pertence.';

-- 4. Índices B-Tree de Performance
CREATE INDEX idx_veiculo_fk_localidade ON veiculo (fk_localidade);
CREATE INDEX idx_veiculo_fk_modelo ON veiculo (fk_modelo);
CREATE INDEX idx_veiculo_ativo ON veiculo (ativo);
CREATE INDEX idx_foto_veiculo_fk_veiculo ON foto_veiculo (fk_veiculo);
