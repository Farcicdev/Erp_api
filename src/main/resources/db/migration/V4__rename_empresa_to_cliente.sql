-- Renomeações preservam linhas, IDs, valores das sequences e dependências das FKs.
ALTER TABLE empresa RENAME TO cliente;
ALTER SEQUENCE empresa_seq RENAME TO cliente_seq;
ALTER TABLE cliente RENAME CONSTRAINT pk_empresa TO pk_cliente;
ALTER TABLE cliente RENAME CONSTRAINT fk_empresa_revenda TO fk_cliente_revenda;
ALTER INDEX idx_empresa_revenda_id RENAME TO idx_cliente_revenda_id;

ALTER TABLE loja RENAME COLUMN empresa_id TO cliente_id;
ALTER TABLE loja RENAME CONSTRAINT fk_loja_empresa TO fk_loja_cliente;
ALTER INDEX idx_loja_empresa_id RENAME TO idx_loja_cliente_id;

ALTER TABLE usuario_empresa RENAME TO usuario_cliente;
ALTER SEQUENCE usuario_empresa_seq RENAME TO usuario_cliente_seq;
ALTER TABLE usuario_cliente RENAME COLUMN empresa_id TO cliente_id;
ALTER TABLE usuario_cliente RENAME CONSTRAINT pk_usuario_empresa TO pk_usuario_cliente;
ALTER TABLE usuario_cliente RENAME CONSTRAINT uk_usuario_empresa TO uk_usuario_cliente;
ALTER TABLE usuario_cliente RENAME CONSTRAINT fk_usuario_empresa_usuario TO fk_usuario_cliente_usuario;
ALTER TABLE usuario_cliente RENAME CONSTRAINT fk_usuario_empresa_empresa TO fk_usuario_cliente_cliente;
ALTER INDEX idx_usuario_empresa_empresa_id RENAME TO idx_usuario_cliente_cliente_id;
