package com.example.exameniii;

public class Entrevista {

    private String id;
    private String descripcion;
    private String periodista;
    private String fecha;

    public Entrevista() {
        // Constructor vacío requerido para Firebase
    }

    public Entrevista(String id, String descripcion, String periodista, String fecha) {
        this.id = id;
        this.descripcion = descripcion;
        this.periodista = periodista;
        this.fecha = fecha;
    }

    public Entrevista(String entrevistaId, String descripcion, String periodista, String fecha, String urlImagen, String urlAudio) {
    }

    // Métodos getter y setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getPeriodista() {
        return periodista;
    }

    public void setPeriodista(String periodista) {
        this.periodista = periodista;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
