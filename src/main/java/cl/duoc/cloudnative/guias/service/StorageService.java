package cl.duoc.cloudnative.guias.service;

import cl.duoc.cloudnative.guias.dto.ArchivoGuia;

import java.util.UUID;

public interface StorageService {

    String subirGuia(UUID guiaId, String nombreArchivo, byte[] contenido, String contentType);

    ArchivoGuia descargarGuia(String key);

    void eliminarGuia(String key);
}
