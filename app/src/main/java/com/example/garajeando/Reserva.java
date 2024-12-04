package com.example.garajeando;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Reserva {


    String idReserva, idCoche, idUsuario, idComunidad, fechaHoraInicio, fechaHoraFin, fotoCoche, matricula, aprobada, propietario, nombreApellidos;

    TimeZone zonaHorariaMovil = TimeZone.getDefault();
    TimeZone zonaHorariaLondres = TimeZone.getTimeZone("Europe/London");

    public Reserva(String idReserva, String idCoche, String idUsuario, String idComunidad, String fechaHoraInicio, String fechaHoraFin, String fotoCoche, String matricula, String aprobada,String propietario, String nombreApellidos) {
        this.idReserva=idReserva;
        this.idUsuario=idUsuario;
        this.idCoche=idCoche;
        this.matricula=matricula;
        this.idComunidad=idComunidad;
        this.fotoCoche=fotoCoche;
        this.aprobada=aprobada;
        this.propietario=propietario;
        this.nombreApellidos=nombreApellidos;

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

    public String getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(String idReserva) {
        this.idReserva = idReserva;
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

    public String getAprobada() {
        return aprobada;
    }

    public void setAprobada(String aprobada) {
        this.aprobada = aprobada;
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

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreApellidos() {
        return nombreApellidos;
    }

    public void setNombreApellidos(String nombreApellidos) {
        this.nombreApellidos = nombreApellidos;
    }

    public String getPropietario() {
        return propietario;
    }

    public void setPropietario(String propietario) {
        this.propietario = propietario;
    }
}
