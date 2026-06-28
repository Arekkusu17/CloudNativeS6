package cl.duoc.cloudnative.guias.model;

import cl.duoc.cloudnative.guias.dto.ActualizarGuiaRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "guias_despacho")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GuiaDespacho {

    @Id
    private UUID id;

    @Column(nullable = false, length = 120)
    private String transportista;

    @Column(nullable = false, length = 120)
    private String destinatario;

    @Column(nullable = false, length = 220)
    private String direccionDestino;

    @Column(nullable = false)
    private LocalDate fechaDespacho;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoGuia estado;

    @Column(nullable = false, length = 500)
    private String detallePedido;

    @Column(name = "s3_key", length = 500)
    private String s3Key;

    @Column(nullable = false, updatable = false)
    private Instant creadoEn;

    @Column(nullable = false)
    private Instant actualizadoEn;

    public GuiaDespacho(String transportista, String destinatario, String direccionDestino, LocalDate fechaDespacho, String detallePedido) {
        this.id = UUID.randomUUID();
        this.transportista = transportista;
        this.destinatario = destinatario;
        this.direccionDestino = direccionDestino;
        this.fechaDespacho = fechaDespacho;
        this.detallePedido = detallePedido;
        this.estado = EstadoGuia.CREADA;
    }

    @PrePersist
    void asignarFechasCreacion() {
        Instant now = Instant.now();
        this.creadoEn = now;
        this.actualizadoEn = now;
    }

    @PreUpdate
    void asignarFechaActualizacion() {
        this.actualizadoEn = Instant.now();
    }

    public void actualizar(ActualizarGuiaRequest request) {
        this.transportista = request.transportista();
        this.destinatario = request.destinatario();
        this.direccionDestino = request.direccionDestino();
        this.fechaDespacho = request.fechaDespacho();
        this.detallePedido = request.detallePedido();
        this.estado = request.estado();
    }

    public void asignarArchivoS3(String s3Key) {
        this.s3Key = s3Key;
        this.estado = EstadoGuia.GENERADA;
    }

    public void actualizarEstado(EstadoGuia estado) {
        this.estado = estado;
    }
}
