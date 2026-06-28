package cl.duoc.cloudnative.guias.config;

import cl.duoc.cloudnative.guias.model.EstadoGuia;
import cl.duoc.cloudnative.guias.model.GuiaDespacho;
import cl.duoc.cloudnative.guias.repository.GuiaDespachoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final GuiaDespachoRepository guiaDespachoRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (guiaDespachoRepository.count() > 0) {
            return;
        }

        List<GuiaDespacho> guias = List.of(
                new GuiaDespacho(
                        "Transporte Norte",
                        "Comercial Los Andes",
                        "Av. Las Condes 1234, Santiago",
                        LocalDate.now().plusDays(1),
                        "Pedido OC-1001: 12 cajas de insumos industriales"
                ),
                new GuiaDespacho(
                        "Logistica Sur",
                        "Ferreteria Puerto Montt",
                        "Camino El Tepual 450, Puerto Montt",
                        LocalDate.now().plusDays(2),
                        "Pedido OC-1002: herramientas electricas y repuestos"
                ),
                new GuiaDespacho(
                        "Expreso Central",
                        "Distribuidora Valparaiso",
                        "Av. Argentina 880, Valparaiso",
                        LocalDate.now().plusDays(3),
                        "Pedido OC-1003: materiales de embalaje"
                )
        );

        guias.get(1).actualizarEstado(EstadoGuia.DESPACHADA);
        guiaDespachoRepository.saveAll(guias);
    }
}
