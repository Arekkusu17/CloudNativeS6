CREATE TABLE IF NOT EXISTS guias_despacho (
    id UUID PRIMARY KEY,
    transportista VARCHAR(120) NOT NULL,
    destinatario VARCHAR(120) NOT NULL,
    direccion_destino VARCHAR(220) NOT NULL,
    fecha_despacho DATE NOT NULL,
    estado VARCHAR(30) NOT NULL,
    detalle_pedido VARCHAR(500) NOT NULL,
    s3_key VARCHAR(500),
    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE guias_despacho
    ADD COLUMN IF NOT EXISTS creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE guias_despacho
    ADD COLUMN IF NOT EXISTS actualizado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

CREATE INDEX IF NOT EXISTS idx_guias_transportista_fecha
    ON guias_despacho (transportista, fecha_despacho);
