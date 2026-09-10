CREATE SEQUENCE usuario_sequence START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE usuario_revenda_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE usuarios (
    id BIGINT NOT NULL DEFAULT nextval('usuario_sequence'),
    keycloak_sub VARCHAR(100) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT pk_usuarios PRIMARY KEY (id),
    CONSTRAINT uk_usuarios_keycloak_sub UNIQUE (keycloak_sub)
);

CREATE TABLE usuario_revenda (
    id BIGINT NOT NULL DEFAULT nextval('usuario_revenda_seq'),
    usuario_id BIGINT NOT NULL,
    revenda_id BIGINT NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT pk_usuario_revenda PRIMARY KEY (id),
    CONSTRAINT uk_usuario_revenda UNIQUE (usuario_id, revenda_id),
    CONSTRAINT fk_usuario_revenda_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id),
    CONSTRAINT fk_usuario_revenda_revenda FOREIGN KEY (revenda_id) REFERENCES revenda (id)
);

CREATE INDEX idx_usuario_revenda_revenda_id ON usuario_revenda (revenda_id);
