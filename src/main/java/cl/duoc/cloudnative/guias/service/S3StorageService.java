package cl.duoc.cloudnative.guias.service;

import cl.duoc.cloudnative.guias.dto.ArchivoGuia;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.UUID;

@Service
public class S3StorageService implements StorageService {

    private final S3Client s3Client;
    private final String bucket;

    public S3StorageService(S3Client s3Client, @Value("${app.aws.s3.bucket}") String bucket) {
        this.s3Client = s3Client;
        this.bucket = bucket;
    }

    @Override
    public String subirGuia(UUID guiaId, String nombreArchivo, byte[] contenido, String contentType) {
        validarArchivo(nombreArchivo, contenido);
        String filename = nombreArchivo.replaceAll("[^a-zA-Z0-9._-]", "_");
        String key = "guias/" + guiaId + "/" + filename;
        String bucketDestino = obtenerBucket();

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketDestino)
                .key(key)
                .contentType(contentType)
                .build();
        s3Client.putObject(request, RequestBody.fromBytes(contenido));
        return key;
    }

    @Override
    public ArchivoGuia descargarGuia(String key) {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(obtenerBucket())
                .key(key)
                .build();
        ResponseBytes<GetObjectResponse> response = s3Client.getObjectAsBytes(request);
        String filename = key.substring(key.lastIndexOf('/') + 1);
        return new ArchivoGuia(response.asByteArray(), filename, response.response().contentType());
    }

    @Override
    public void eliminarGuia(String key) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(obtenerBucket())
                .key(key)
                .build();
        s3Client.deleteObject(request);
    }

    private String obtenerBucket() {
        if (bucket == null || bucket.trim().isEmpty()) {
            throw new IllegalStateException("Debe configurar S3_BUCKET para usar S3.");
        }
        return bucket.trim();
    }

    private void validarArchivo(String nombreArchivo, byte[] contenido) {
        if (nombreArchivo == null || nombreArchivo.isBlank()) {
            throw new IllegalArgumentException("Debe indicar el nombre del archivo de guia.");
        }
        if (contenido == null || contenido.length == 0) {
            throw new IllegalArgumentException("El archivo de guia generado esta vacio.");
        }
    }
}
