package cl.duoc.cloudnative.guias.service;

import cl.duoc.cloudnative.guias.dto.ArchivoGuia;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface StorageService {

    String subirGuia(UUID guiaId, MultipartFile archivo);

    ArchivoGuia descargarGuia(String key);

    void eliminarGuia(String key);
}
