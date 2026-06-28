package cl.duoc.cloudnative.guias.dto;

public record ArchivoGuia(byte[] contenido, String nombreArchivo, String contentType) {
}
