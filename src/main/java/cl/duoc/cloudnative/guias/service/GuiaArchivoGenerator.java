package cl.duoc.cloudnative.guias.service;

import cl.duoc.cloudnative.guias.dto.ArchivoGuia;
import cl.duoc.cloudnative.guias.model.GuiaDespacho;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class GuiaArchivoGenerator {

    public ArchivoGuia generar(GuiaDespacho guia) {
        String contenido = """
                GUIA DE DESPACHO

                ID: %s
                Transportista: %s
                Destinatario: %s
                Direccion destino: %s
                Fecha despacho: %s
                Estado: %s

                Detalle pedido:
                %s
                """.formatted(
                guia.getId(),
                guia.getTransportista(),
                guia.getDestinatario(),
                guia.getDireccionDestino(),
                guia.getFechaDespacho(),
                guia.getEstado(),
                guia.getDetallePedido()
        );

        return new ArchivoGuia(
                contenido.getBytes(StandardCharsets.UTF_8),
                "guia-" + guia.getId() + ".txt",
                MediaType.TEXT_PLAIN_VALUE
        );
    }
}
