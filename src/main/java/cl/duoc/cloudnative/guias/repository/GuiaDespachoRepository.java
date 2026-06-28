package cl.duoc.cloudnative.guias.repository;

import cl.duoc.cloudnative.guias.model.GuiaDespacho;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface GuiaDespachoRepository extends JpaRepository<GuiaDespacho, UUID> {

    List<GuiaDespacho> findByTransportistaIgnoreCaseAndFechaDespacho(String transportista, LocalDate fechaDespacho);
}
