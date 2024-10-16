package com.example.garajeando;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Oferta {

    String idOferta, idCoche, idComunidad, fechaHoraInicio, fechaHoraFin, fotoCoche;

    TimeZone zonaHorariaMovil = TimeZone.getDefault();
    TimeZone zonaHorariaLondres = TimeZone.getTimeZone("Europe/London");

    public Oferta(String idOferta, String idCoche, String idComunidad, String fechaHoraInicio, String fechaHoraFin, String fotoCoche) {
        this.idOferta=idOferta;
        this.idCoche=idCoche;
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

}
