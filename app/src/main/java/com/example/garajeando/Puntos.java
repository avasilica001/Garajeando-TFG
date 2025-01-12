package com.example.garajeando;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Puntos {

    private String idUsuario;
    private String fechaHora;
    private String puntos;
    private String decripcion;

    TimeZone zonaHorariaMovil = TimeZone.getDefault();
    TimeZone zonaHorariaLondres = TimeZone.getTimeZone("Europe/London");

    public Puntos (String idUsuario, String fechaHora, String puntos, String decripcion){
        this.idUsuario=idUsuario;
        this.fechaHora=fechaHora;
        this.puntos=puntos;
        this.decripcion=decripcion;

        SimpleDateFormat formatoOriginal = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        formatoOriginal.setTimeZone(zonaHorariaLondres);

        try{
            Date fechaHoraFormatoOriginal = formatoOriginal.parse(fechaHora);

            SimpleDateFormat formatoMovil = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            formatoMovil.setTimeZone(zonaHorariaMovil);

            this.fechaHora = formatoMovil.format(fechaHoraFormatoOriginal);
        } catch (ParseException e) {
            //e.printStackTrace();
        }
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(String fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getPuntos() {
        return puntos;
    }

    public void setPuntos(String puntos) {
        this.puntos = puntos;
    }

    public String getDecripcion() {
        return decripcion;
    }

    public void setDecripcion(String decripcion) {
        this.decripcion = decripcion;
    }

    public TimeZone getZonaHorariaMovil() {
        return zonaHorariaMovil;
    }

    public void setZonaHorariaMovil(TimeZone zonaHorariaMovil) {
        this.zonaHorariaMovil = zonaHorariaMovil;
    }

    public TimeZone getZonaHorariaLondres() {
        return zonaHorariaLondres;
    }

    public void setZonaHorariaLondres(TimeZone zonaHorariaLondres) {
        this.zonaHorariaLondres = zonaHorariaLondres;
    }
}
