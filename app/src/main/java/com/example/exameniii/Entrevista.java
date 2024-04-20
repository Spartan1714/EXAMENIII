package com.example.exameniii;

public class Entrevista {

    private String id;
    private String descripcion;
    private String periodista;
    private String fecha;
    private String urlAudio;
    private String urlImagen;



    public Entrevista() {
        // Constructor vacío requerido para Firebase
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

    public String getUrlImagen() {
        return fecha;
    }

    public void setUrlImagen(String fecha) {
        this.fecha = fecha;
    }

    public String getUrlAudio() {
        return fecha;
    }

    public void setUrlAudio(String fecha) {
        this.fecha = fecha;
    }
}
