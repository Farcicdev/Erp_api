CREATE SEQUENCE usuario_empresa_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE usuario_empresa (
    id BIGINT NOT NULL DEFAULT nextval('usuario_empresa_seq'),
    usuario_id BIGINT NOT NULL,
    empresa_id BIGINT NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT pk_usuario_empresa PRIMARY KEY (id),
    CONSTRAINT uk_usuario_empresa UNIQUE (usuario_id, empresa_id),
    CONSTRAINT fk_usuario_empresa_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id),
    CONSTRAINT fk_usuario_empresa_empresa FOREIGN KEY (empresa_id) REFERENCES empresa (id)
);

CREATE INDEX idx_usuario_empresa_empresa_id ON usuario_empresa (empresa_id);
