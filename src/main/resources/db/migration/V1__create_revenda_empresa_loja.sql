CREATE SEQUENCE revenda_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE empresa_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE loja_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE revenda (
    id BIGINT NOT NULL DEFAULT nextval('revenda_seq'),
    nome VARCHAR(100) NOT NULL,
    email_contato VARCHAR(150) NOT NULL,
    cnpj VARCHAR(14) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT pk_revenda PRIMARY KEY (id),
    CONSTRAINT uk_revenda_email_contato UNIQUE (email_contato),
    CONSTRAINT uk_revenda_cnpj UNIQUE (cnpj)
);

CREATE TABLE empresa (
    id BIGINT NOT NULL DEFAULT nextval('empresa_seq'),
    nome VARCHAR(255) NOT NULL,
    email_contato VARCHAR(255),
    telefone VARCHAR(255),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    revenda_id BIGINT NOT NULL,
    CONSTRAINT pk_empresa PRIMARY KEY (id),
    CONSTRAINT fk_empresa_revenda FOREIGN KEY (revenda_id) REFERENCES revenda (id)
);

CREATE TABLE loja (
    id BIGINT NOT NULL DEFAULT nextval('loja_seq'),
    nome VARCHAR(255) NOT NULL,
    nome_fantasia VARCHAR(255) NOT NULL,
    razao_social VARCHAR(255) NOT NULL,
    cnpj VARCHAR(14) NOT NULL,
    inscricao_estadual VARCHAR(15) NOT NULL,
    regime_tributario VARCHAR(30) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    empresa_id BIGINT NOT NULL,
    CONSTRAINT uk_loja_cnpj UNIQUE (cnpj),
    CONSTRAINT pk_loja PRIMARY KEY (id),
    CONSTRAINT ck_loja_regime_tributario CHECK (regime_tributario IN
        ('SIMPLES_NACIONAL', 'LUCRO_PRESUMIDO', 'LUCRO_REAL')),
    CONSTRAINT fk_loja_empresa FOREIGN KEY (empresa_id) REFERENCES empresa (id)
);

CREATE INDEX idx_empresa_revenda_id ON empresa (revenda_id);
CREATE INDEX idx_loja_empresa_id ON loja (empresa_id);
