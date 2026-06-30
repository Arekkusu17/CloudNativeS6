package cl.duoc.cloudnative.guias.controller;

import cl.duoc.cloudnative.guias.dto.ActualizarGuiaRequest;
import cl.duoc.cloudnative.guias.dto.ArchivoGuia;
import cl.duoc.cloudnative.guias.dto.CrearGuiaRequest;
import cl.duoc.cloudnative.guias.dto.GuiaResponse;
import cl.duoc.cloudnative.guias.service.GuiaDespachoService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/guias")
public class GuiaDespachoController {

    private final GuiaDespachoService service;

    public GuiaDespachoController(GuiaDespachoService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GuiaResponse crear(@Valid @RequestBody CrearGuiaRequest request) {
        return service.crearGuia(request);
    }

    @PostMapping("/{id}/archivo")
    public ResponseEntity<GuiaResponse> generarArchivo(@PathVariable UUID id) {
        return ResponseEntity.ok(service.generarYSubirArchivoGuia(id));
    }

    @GetMapping("/{id}/descargar")
    public ResponseEntity<byte[]> descargar(@PathVariable UUID id) {
        ArchivoGuia archivo = service.descargarGuia(id);
        MediaType mediaType = archivo.contentType() == null
                ? MediaType.APPLICATION_OCTET_STREAM
                : MediaType.parseMediaType(archivo.contentType());

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename(archivo.nombreArchivo())
                        .build()
                        .toString())
                .body(archivo.contenido());
    }

    @PutMapping("/{id}")
    public ResponseEntity<GuiaResponse> actualizar(@PathVariable UUID id, @Valid @RequestBody ActualizarGuiaRequest request) {
        return ResponseEntity.ok(service.actualizarGuia(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable UUID id) {
        service.eliminarGuia(id);
    }

    @GetMapping
    public ResponseEntity<List<GuiaResponse>> consultar(
            @RequestParam String transportista,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha
    ) {
        return ResponseEntity.ok(service.consultarGuias(transportista, fecha));
    }
}
