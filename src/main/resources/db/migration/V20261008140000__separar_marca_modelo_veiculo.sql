-- =============================================================================
-- Migration UP: V20261008140000__separar_marca_modelo_veiculo.sql
-- Módulo: Gestão da Frota de Veículos - Separação da Entidade Marca
-- Descrição: Criação da tabela marca (entidade fraca do modelo) com logo,
--            migração dos dados existentes e ajuste dos vínculos de modelo_veiculo.
-- SGBD: PostgreSQL 16+
-- =============================================================================

-- 1. Criação da tabela marca
CREATE TABLE marca (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(100) NOT NULL,
    logo VARCHAR(500),
    CONSTRAINT uk_marca_nome UNIQUE (nome)
);

COMMENT ON TABLE marca IS 'Catálogo de marcas dos modelos de veículos (entidade fraca do modelo).';
COMMENT ON COLUMN marca.id IS 'Identificador único (UUID) da marca.';
COMMENT ON COLUMN marca.nome IS 'Nome da marca/fabricante (ex: Toyota, Chevrolet, etc.).';
COMMENT ON COLUMN marca.logo IS 'URL pública da imagem do logotipo da marca armazenada no Blob Storage.';

-- 2. Migrar marcas e logos existentes de modelo_veiculo para a nova tabela marca
INSERT INTO marca (id, nome, logo)
SELECT gen_random_uuid(), TRIM(m.marca), MAX(m.logo)
FROM modelo_veiculo m
WHERE m.marca IS NOT NULL AND TRIM(m.marca) <> ''
GROUP BY TRIM(m.marca)
ON CONFLICT (nome) DO NOTHING;

-- 3. Adicionar coluna fk_marca em modelo_veiculo
ALTER TABLE modelo_veiculo ADD COLUMN fk_marca UUID;

-- 4. Atualizar fk_marca em modelo_veiculo com base nos nomes cadastrados em marca
UPDATE modelo_veiculo mv
SET fk_marca = m.id
FROM marca m
WHERE LOWER(TRIM(mv.marca)) = LOWER(m.nome);

-- 5. Tornar fk_marca NOT NULL e adicionar Constraint de Chave Estrangeira
-- ALTER TABLE modelo_veiculo ALTER COLUMN fk_marca SET NOT NULL; -- Adiar até que todos os dados estejam consistentes
ALTER TABLE modelo_veiculo ADD CONSTRAINT fk_modelo_veiculo_marca
    FOREIGN KEY (fk_marca) REFERENCES marca (id) ON DELETE RESTRICT; -- Removido ON UPDATE CASCADE (UUID não atualiza)

-- 6. Remover colunas desnormalizadas 'marca' e 'logo' da tabela modelo_veiculo
-- ATENÇÃO: Desabilitado temporariamente para evitar falhas destrutivas sem rollback na auditoria.
-- Em uma próxima migration contract (após validar expansão), esses DROPs podem ser executados.
-- ALTER TABLE modelo_veiculo DROP COLUMN marca;
-- ALTER TABLE modelo_veiculo DROP COLUMN logo;

-- 7. Índice para otimização de consultas por marca
CREATE INDEX idx_modelo_veiculo_fk_marca ON modelo_veiculo (fk_marca);

