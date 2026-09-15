CREATE SEQUENCE produto_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE produtos (
    id BIGINT NOT NULL DEFAULT nextval('produto_seq'),
    codigo_interno VARCHAR(50) NOT NULL,
    descricao VARCHAR(255) NOT NULL,
    gtin VARCHAR(14),
    unidade VARCHAR(10) NOT NULL,
    ncm VARCHAR(8) NOT NULL,
    cest VARCHAR(7),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    loja_id BIGINT NOT NULL,
    CONSTRAINT pk_produtos PRIMARY KEY (id),
    CONSTRAINT uk_produto_loja_codigo UNIQUE (loja_id, codigo_interno),
    CONSTRAINT uk_produtos_loja_gtin UNIQUE (loja_id, gtin),
    CONSTRAINT fk_produtos_loja FOREIGN KEY (loja_id) REFERENCES loja (id),
    CONSTRAINT ck_produtos_unidade CHECK (unidade IN
        ('UN', 'CX', 'PCT', 'PAR', 'DZ', 'KG', 'G', 'L', 'ML', 'M', 'M2', 'M3'))
);

-- Os índices das constraints de unicidade já começam por loja_id.
