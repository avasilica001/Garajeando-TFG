package com.example.garajeando;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Oferta {


    String idOferta, idCoche, idComunidad, fechaHoraInicio, fechaHoraFin, fotoCoche, matricula;

    TimeZone zonaHorariaMovil = TimeZone.getDefault();
    TimeZone zonaHorariaLondres = TimeZone.getTimeZone("Europe/London");

    public Oferta(String idOferta, String idCoche, String idComunidad, String fechaHoraInicio, String fechaHoraFin, String fotoCoche, String matricula) {
        this.idOferta=idOferta;
        this.idCoche=idCoche;
        this.matricula=matricula;
        this.idComunidad=idComunidad;
        this.fotoCoche=fotoCoche;

        SimpleDateFormat formatoOriginal = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        formatoOriginal.setTimeZone(zonaHorariaLondres);

        try{
                Date  fechaHoraInicioFormatoOriginal = formatoOriginal.parse(fechaHoraInicio);

                SimpleDateFormat formatoMovilInicio = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                formatoMovilInicio.setTimeZone(zonaHorariaMovil);

                Date fechaHoraFinalFormatoOriginal = formatoOriginal.parse(fechaHoraFin);

                SimpleDateFormat formatoMovilFin = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                formatoMovilFin.setTimeZone(zonaHorariaMovil);

                this.fechaHoraInicio = formatoMovilInicio.format(fechaHoraInicioFormatoOriginal);
                this.fechaHoraFin = formatoMovilFin.format(fechaHoraFinalFormatoOriginal);
        } catch (ParseException e) {
            //e.printStackTrace();
        }
    }

    public String getIdOferta() {
        return idOferta;
    }

    public void setIdOferta(String idOferta) {
        this.idOferta = idOferta;
    }

    public String getIdCoche() {
        return idCoche;
    }

    public void setIdCoche(String idCoche) {
        this.idCoche = idCoche;
    }

    public String getIdComunidad() {
        return idComunidad;
    }

    public void setIdComunidad(String idComunidad) {
        this.idComunidad = idComunidad;
    }

    public String getFechaHoraInicio() {
        return fechaHoraInicio;
    }

    public void setFechaHoraInicio(String fechaHoraInicio) {
        this.fechaHoraInicio = fechaHoraInicio;
    }

    public String getFechaHoraFin() {
        return fechaHoraFin;
    }

    public void setFechaHoraFin(String fechaHoraFin) {
        this.fechaHoraFin = fechaHoraFin;
    }

    public String getFotoCoche() {
        return fotoCoche;
    }

    public void setFotoCoche(String fotoCoche) {
        this.fotoCoche = fotoCoche;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
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
