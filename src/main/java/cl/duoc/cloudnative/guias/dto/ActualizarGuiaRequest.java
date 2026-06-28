package cl.duoc.cloudnative.guias.dto;

import cl.duoc.cloudnative.guias.model.EstadoGuia;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ActualizarGuiaRequest(
        @NotBlank @Size(max = 120) String transportista,
        @NotBlank @Size(max = 120) String destinatario,
        @NotBlank @Size(max = 220) String direccionDestino,
        @NotNull LocalDate fechaDespacho,
        @NotBlank @Size(max = 500) String detallePedido,
        @NotNull EstadoGuia estado
) {
}
