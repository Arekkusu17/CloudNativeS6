package cl.duoc.cloudnative.guias.dto;

import cl.duoc.cloudnative.guias.model.EstadoGuia;
import cl.duoc.cloudnative.guias.model.GuiaDespacho;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record GuiaResponse(
        UUID id,
        String transportista,
        String destinatario,
        String direccionDestino,
        LocalDate fechaDespacho,
        EstadoGuia estado,
        String detallePedido,
        boolean archivoDisponible,
        Instant creadoEn,
        Instant actualizadoEn
) {
    public static GuiaResponse from(GuiaDespacho guia) {
        return new GuiaResponse(
                guia.getId(),
                guia.getTransportista(),
                guia.getDestinatario(),
                guia.getDireccionDestino(),
                guia.getFechaDespacho(),
                guia.getEstado(),
                guia.getDetallePedido(),
                guia.getS3Key() != null,
                guia.getCreadoEn(),
                guia.getActualizadoEn()
        );
    }
}
