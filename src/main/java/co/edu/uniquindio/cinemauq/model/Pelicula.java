package co.edu.uniquindio.cinemauq.model;

import co.edu.uniquindio.cinemauq.model.enums.Genero;

public class Pelicula {

    private final String codigo;
    private String titulo;
    private Genero genero;
    private int duracionMinutos;
    private String clasificacion;
    private String sinopsis;
    private boolean enCartelera = true;

    public Pelicula(String codigo, String titulo, Genero genero, int duracionMinutos, String clasificacion, String sinopsis) {
        this.codigo = codigo;
        this.titulo = titulo;
        this.genero = genero;
        this.duracionMinutos = duracionMinutos;
        this.clasificacion = clasificacion;
        this.sinopsis = sinopsis;
    }

    public String getCodigo() { return codigo; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public Genero getGenero() { return genero; }
    public void setGenero(Genero genero) { this.genero = genero; }
    public int getDuracionMinutos() { return duracionMinutos; }
    public void setDuracionMinutos(int duracionMinutos) { this.duracionMinutos = duracionMinutos; }
    public String getClasificacion() { return clasificacion; }
    public void setClasificacion(String clasificacion) { this.clasificacion = clasificacion; }
    public String getSinopsis() { return sinopsis; }
    public void setSinopsis(String sinopsis) { this.sinopsis = sinopsis; }
    public boolean isEnCartelera() { return enCartelera; }
    public void setEnCartelera(boolean enCartelera) { this.enCartelera = enCartelera; }

    @Override
    public String toString() { return titulo; }
}
