package cl.duoc.cloudnative.guias.service;

import cl.duoc.cloudnative.guias.dto.ActualizarGuiaRequest;
import cl.duoc.cloudnative.guias.dto.ArchivoGuia;
import cl.duoc.cloudnative.guias.dto.CrearGuiaRequest;
import cl.duoc.cloudnative.guias.dto.GuiaResponse;
import cl.duoc.cloudnative.guias.model.GuiaDespacho;
import cl.duoc.cloudnative.guias.repository.GuiaDespachoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class GuiaDespachoService {

    private final GuiaDespachoRepository guiaDespachoRepository;
    private final StorageService storageService;
    private final GuiaArchivoGenerator guiaArchivoGenerator;

    public GuiaDespachoService(
            GuiaDespachoRepository guiaDespachoRepository,
            StorageService storageService,
            GuiaArchivoGenerator guiaArchivoGenerator
    ) {
        this.guiaDespachoRepository = guiaDespachoRepository;
        this.storageService = storageService;
        this.guiaArchivoGenerator = guiaArchivoGenerator;
    }

    @Transactional
    public GuiaResponse crearGuia(CrearGuiaRequest request) {
        GuiaDespacho guia = new GuiaDespacho(
                request.transportista(),
                request.destinatario(),
                request.direccionDestino(),
                request.fechaDespacho(),
                request.detallePedido()
        );
        return GuiaResponse.from(guiaDespachoRepository.save(guia));
    }

    @Transactional
    public GuiaResponse generarYSubirArchivoGuia(UUID id) {
        GuiaDespacho guia = buscarEntidad(id);
        ArchivoGuia archivo = guiaArchivoGenerator.generar(guia);
        String key = storageService.subirGuia(id, archivo.nombreArchivo(), archivo.contenido(), archivo.contentType());
        guia.asignarArchivoS3(key);
        return GuiaResponse.from(guiaDespachoRepository.save(guia));
    }

    @Transactional(readOnly = true)
    public ArchivoGuia descargarGuia(UUID id) {
        GuiaDespacho guia = buscarEntidad(id);
        if (guia.getS3Key() == null) {
            throw new ArchivoNoDisponibleException("La guia aun no tiene archivo generado en S3");
        }
        return storageService.descargarGuia(guia.getS3Key());
    }

    @Transactional
    public GuiaResponse actualizarGuia(UUID id, ActualizarGuiaRequest request) {
        GuiaDespacho guia = buscarEntidad(id);
        guia.actualizar(request);
        return GuiaResponse.from(guiaDespachoRepository.save(guia));
    }

    @Transactional
    public void eliminarGuia(UUID id) {
        GuiaDespacho guia = buscarEntidad(id);
        if (guia.getS3Key() != null) {
            storageService.eliminarGuia(guia.getS3Key());
        }
        guiaDespachoRepository.delete(guia);
    }

    @Transactional(readOnly = true)
    public List<GuiaResponse> consultarGuias(String transportista, LocalDate fecha) {
        return guiaDespachoRepository.findByTransportistaIgnoreCaseAndFechaDespacho(transportista, fecha)
                .stream()
                .map(GuiaResponse::from)
                .toList();
    }

    private GuiaDespacho buscarEntidad(UUID id) {
        return guiaDespachoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la guia de despacho " + id));
    }
}
