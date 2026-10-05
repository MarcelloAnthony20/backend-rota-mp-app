-- =============================================================================
-- Migration UP: V20260928160500__modulo_solicitacoes_atendimento.sql
-- Módulo: Gestão de Tipos e Solicitações de Atendimento (HU-11, HU-12)
-- Atomicidade: Criação isolada da estrutura de requisições de transporte
-- SGBD Alvo: PostgreSQL 16+
-- =============================================================================

-- 1. Tabela: tipo (Tipos de Atendimento/Viagem)
CREATE TABLE tipo (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(100) NOT NULL,
    descricao VARCHAR(255) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);

COMMENT ON TABLE tipo IS 'Tipos e modalidades de atendimento de transporte (ex: Urgente, Administrativo, Audiência).';
COMMENT ON COLUMN tipo.id IS 'Identificador único (UUID) do tipo de atendimento.';
COMMENT ON COLUMN tipo.nome IS 'Nome da modalidade de atendimento.';
COMMENT ON COLUMN tipo.descricao IS 'Descrição detalhada dos critérios do tipo de atendimento.';
COMMENT ON COLUMN tipo.ativo IS 'Indicador se a modalidade está ativa para novas solicitações.';

-- 2. Tabela: solicitacao_atendimento
CREATE TABLE solicitacao_atendimento (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    fk_tipo UUID NOT NULL,
    fk_usuario UUID NOT NULL,
    fk_localidade_origem UUID NOT NULL,
    fk_localidade_destino UUID NOT NULL,
    data_criacao TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL,
    prioridade VARCHAR(30) NOT NULL,
    data_analise TIMESTAMP WITHOUT TIME ZONE,
    num_passageiro INTEGER NOT NULL,
    justificativa VARCHAR(1000) NOT NULL,
    data_pedido TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT fk_solicitacao_atendimento_tipo FOREIGN KEY (fk_tipo) REFERENCES tipo (id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_solicitacao_atendimento_usuario FOREIGN KEY (fk_usuario) REFERENCES usuario (id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_solicitacao_atendimento_origem FOREIGN KEY (fk_localidade_origem) REFERENCES localidade (id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_solicitacao_atendimento_destino FOREIGN KEY (fk_localidade_destino) REFERENCES localidade (id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT chk_solicitacao_status CHECK (status IN ('PENDENTE', 'ACEITO', 'RECUSADO'))
);

COMMENT ON TABLE solicitacao_atendimento IS 'Pedidos formais de deslocamento de passageiros e transporte oficial.';
COMMENT ON COLUMN solicitacao_atendimento.id IS 'Identificador único (UUID) da solicitação.';
COMMENT ON COLUMN solicitacao_atendimento.fk_tipo IS 'Chave estrangeira do tipo/modalidade de atendimento.';
COMMENT ON COLUMN solicitacao_atendimento.fk_usuario IS 'Chave estrangeira do usuário solicitante.';
COMMENT ON COLUMN solicitacao_atendimento.fk_localidade_origem IS 'Chave estrangeira da localidade de partida.';
COMMENT ON COLUMN solicitacao_atendimento.fk_localidade_destino IS 'Chave estrangeira da localidade de chegada/destino.';
COMMENT ON COLUMN solicitacao_atendimento.data_criacao IS 'Carimbo de data/hora da abertura da requisição.';
COMMENT ON COLUMN solicitacao_atendimento.status IS 'Status da solicitação (PENDENTE, ACEITO, RECUSADO).';
COMMENT ON COLUMN solicitacao_atendimento.prioridade IS 'Nível de prioridade da solicitação (ex: NORMAL, ALTA, URGENTE).';
COMMENT ON COLUMN solicitacao_atendimento.data_analise IS 'Carimbo de data/hora da apreciação pelo gestor da frota.';
COMMENT ON COLUMN solicitacao_atendimento.num_passageiro IS 'Quantidade prevista de passageiros para o transporte.';
COMMENT ON COLUMN solicitacao_atendimento.justificativa IS 'Motivação institucional e fundamentação do pedido de transporte.';
COMMENT ON COLUMN solicitacao_atendimento.data_pedido IS 'Data e hora programadas para a realização da viagem.';

-- 3. Índices B-Tree de Performance
CREATE INDEX idx_solicitacao_atendimento_fk_tipo ON solicitacao_atendimento (fk_tipo);
CREATE INDEX idx_solicitacao_atendimento_fk_usuario ON solicitacao_atendimento (fk_usuario);
CREATE INDEX idx_solicitacao_atendimento_fk_origem ON solicitacao_atendimento (fk_localidade_origem);
CREATE INDEX idx_solicitacao_atendimento_fk_destino ON solicitacao_atendimento (fk_localidade_destino);
CREATE INDEX idx_solicitacao_atendimento_status ON solicitacao_atendimento (status);
CREATE INDEX idx_solicitacao_atendimento_data_pedido ON solicitacao_atendimento (data_pedido);
CREATE INDEX idx_solicitacao_atendimento_data_criacao ON solicitacao_atendimento (data_criacao);
