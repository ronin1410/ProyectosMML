package pe.gob.mml.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Entidad que representa un Proyecto de Inversión Pública. */
public class Proyecto {

    private int id;
    private String nombre;
    private String descripcion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String asesorTecnico;
    private BigDecimal presupuesto;

    public Proyecto() {
    }

    public Proyecto(int id, String nombre, String descripcion, LocalDate fechaInicio,
                    LocalDate fechaFin, String asesorTecnico, BigDecimal presupuesto) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.asesorTecnico = asesorTecnico;
        this.presupuesto = presupuesto;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public String getAsesorTecnico() { return asesorTecnico; }
    public void setAsesorTecnico(String asesorTecnico) { this.asesorTecnico = asesorTecnico; }

    public BigDecimal getPresupuesto() { return presupuesto; }
    public void setPresupuesto(BigDecimal presupuesto) { this.presupuesto = presupuesto; }
}
