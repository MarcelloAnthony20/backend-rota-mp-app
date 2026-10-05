-- =============================================================================
-- Migration UP: V20260928160600__modulo_despacho_viagens.sql
-- Módulo: Despacho e Execução de Viagens (HU-13, HU-14)
-- Atomicidade: Criação isolada da estrutura de viagens, rotas e alocações
-- SGBD Alvo: PostgreSQL 16+
-- =============================================================================

-- 1. Tabela: viagem
CREATE TABLE viagem (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    fk_motorista UUID NOT NULL,
    fk_solicitacao UUID NOT NULL,
    fk_veiculo UUID NOT NULL,
    status VARCHAR(20) NOT NULL,
    data_inicio TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    data_conclusao TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT fk_viagem_motorista FOREIGN KEY (fk_motorista) REFERENCES motorista (id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_viagem_solicitacao FOREIGN KEY (fk_solicitacao) REFERENCES solicitacao_atendimento (id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_viagem_veiculo FOREIGN KEY (fk_veiculo) REFERENCES veiculo (id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT chk_viagem_status CHECK (status IN ('AGENDADA', 'EM_ROTA', 'CONCLUIDA', 'ATRASADA'))
);

COMMENT ON TABLE viagem IS 'Execução operacional de viagens associando motorista, veículo e solicitação atendida.';
COMMENT ON COLUMN viagem.id IS 'Identificador único (UUID) da viagem.';
COMMENT ON COLUMN viagem.fk_motorista IS 'Chave estrangeira do motorista escalado para a rota.';
COMMENT ON COLUMN viagem.fk_solicitacao IS 'Chave estrangeira da solicitação de atendimento despachada.';
COMMENT ON COLUMN viagem.fk_veiculo IS 'Chave estrangeira do veículo designado.';
COMMENT ON COLUMN viagem.status IS 'Situação operacional da viagem (AGENDADA, EM_ROTA, CONCLUIDA, ATRASADA).';
COMMENT ON COLUMN viagem.data_inicio IS 'Carimbo de data/hora programado/efetivo de início do deslocamento.';
COMMENT ON COLUMN viagem.data_conclusao IS 'Carimbo de data/hora efetivo de encerramento da viagem.';

-- 2. Índices B-Tree de Performance
CREATE INDEX idx_viagem_fk_motorista ON viagem (fk_motorista);
CREATE INDEX idx_viagem_fk_solicitacao ON viagem (fk_solicitacao);
CREATE INDEX idx_viagem_fk_veiculo ON viagem (fk_veiculo);
CREATE INDEX idx_viagem_status ON viagem (status);
CREATE INDEX idx_viagem_data_inicio ON viagem (data_inicio);
