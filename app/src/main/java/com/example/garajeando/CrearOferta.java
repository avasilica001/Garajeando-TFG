package com.example.garajeando;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class CrearOferta extends AppCompatActivity {

    String usuario, idComunidad, idCoche;

    EditText fechaInicioEditText, fechaFinalEditText, horaInicioEditText, horaFinalEditText;
    Button publicarOfertaButton;
    
    Calendar inicioCalendario, finalCalendario;
    TimeZone zonaHorariaMovil = TimeZone.getDefault();
    TimeZone zonaHorariaLondres = TimeZone.getTimeZone("Europe/London");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_crear_oferta);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        usuario = getIntent().getExtras().getString("usuario");
        idComunidad = getIntent().getExtras().getString("idComunidad");
        idCoche = getIntent().getExtras().getString("idCoche");

        setSupportActionBar(findViewById(R.id.crearOfertaToolbar));
        getSupportActionBar().setTitle("CREAR OFERTA");

        inicioCalendario = Calendar.getInstance(zonaHorariaMovil);
        finalCalendario = Calendar.getInstance(zonaHorariaMovil);

        fechaInicioEditText = (EditText) findViewById(R.id.fechaInicioEditText);
        fechaFinalEditText = (EditText) findViewById(R.id.fechaFinalEditText);

        horaInicioEditText = (EditText) findViewById(R.id.horaInicioEditText);
        horaFinalEditText = (EditText) findViewById(R.id.horaFinalEditText);

        publicarOfertaButton = (Button) findViewById(R.id.publicarOfertaButton);

        fechaInicioEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoFecha(inicioCalendario, fechaInicioEditText, true);
            }
        });

        horaInicioEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoHora(inicioCalendario, horaInicioEditText, true);
            }
        });

        fechaFinalEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoFecha(finalCalendario, fechaFinalEditText, false);
            }
        });

        horaFinalEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoHora(finalCalendario, horaFinalEditText, false);
            }
        });

        publicarOfertaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarFechas();
            }
        });
    }

    private void mostrarDialogoFecha(Calendar calendario, EditText editText, boolean comienzo) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(CrearOferta.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int anio, int mes, int dia) {
                        calendario.set(Calendar.YEAR, anio);
                        calendario.set(Calendar.MONTH, mes);
                        calendario.set(Calendar.DAY_OF_MONTH, dia);

                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        sdf.setTimeZone(zonaHorariaMovil);
                        editText.setText(sdf.format(calendario.getTime()));

                        if (!comienzo && finalCalendario.getTime().before(inicioCalendario.getTime()) && !fechaInicioEditText.getText().toString().trim().isEmpty()) {
                            Toast.makeText(CrearOferta.this, "La fecha final no puede ser anterior a la fecha inicial", Toast.LENGTH_SHORT).show();
                            editText.setText("");
                        }

                        // Validate that the start date is not before the finish date if both dates are set
                        if (comienzo && !finalCalendario.equals(Calendar.getInstance())  && !fechaFinalEditText.getText().toString().trim().isEmpty()) {
                            if (calendario.getTime().after(finalCalendario.getTime())) {
                                Toast.makeText(CrearOferta.this, "La fecha inical no puede ser posterior a la fecha final", Toast.LENGTH_SHORT).show();
                                editText.setText("");
                            }
                        }
                    }
                }, calendario.get(Calendar.YEAR), calendario.get(Calendar.MONTH), calendario.get(Calendar.DAY_OF_MONTH));

        if (comienzo) {
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        } else {
            datePickerDialog.getDatePicker().setMinDate(inicioCalendario.getTimeInMillis());
        }

        datePickerDialog.show();
    }

    private void mostrarDialogoHora(Calendar calendario, EditText editText, boolean comienzo) {
        final Calendar horaActual = Calendar.getInstance(zonaHorariaMovil);

        int hora = calendario.get(Calendar.HOUR_OF_DAY);
        int minuto = calendario.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(CrearOferta.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int horaDia, int minutoDia) {
                        if (comienzo && esHoy(calendario)) {
                            if (horaDia < horaActual.get(Calendar.HOUR_OF_DAY) ||
                                    (horaDia == horaActual.get(Calendar.HOUR_OF_DAY) && minutoDia < horaActual.get(Calendar.MINUTE))) {
                                Toast.makeText(CrearOferta.this, "La hora inicial no puede ser anterior a la hora actual", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }

                        calendario.set(Calendar.HOUR_OF_DAY, horaDia);
                        calendario.set(Calendar.MINUTE, minutoDia);

                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                        sdf.setTimeZone(zonaHorariaMovil);
                        editText.setText(sdf.format(calendario.getTime()));

                        if (!comienzo) {
                            boolean mismoDia = finalCalendario.get(Calendar.YEAR) == inicioCalendario.get(Calendar.YEAR) &&
                                    finalCalendario.get(Calendar.DAY_OF_YEAR) == inicioCalendario.get(Calendar.DAY_OF_YEAR);
                            if (mismoDia && finalCalendario.getTime().compareTo(inicioCalendario.getTime()) <= 0 && !horaInicioEditText.getText().toString().trim().isEmpty()) {
                                Toast.makeText(CrearOferta.this, "La hora final no puede ser igual o anterior a la hora inicial si se trata del mismo día", Toast.LENGTH_SHORT).show();
                                editText.setText("");
                            }
                        }

                        if (comienzo && !finalCalendario.equals(Calendar.getInstance())) {
                            if (calendario.getTime().after(finalCalendario.getTime()) && !horaFinalEditText.getText().toString().trim().isEmpty()) {
                                Toast.makeText(CrearOferta.this, "La hora inicial no puede ser posterior a la hora final si se trata del mismo día", Toast.LENGTH_SHORT).show();
                                editText.setText("");
                            }
                        }
                    }
                }, hora, minuto, true);
        timePickerDialog.show();
    }

    private boolean esHoy(Calendar calendario) {
        Calendar hoy = Calendar.getInstance(zonaHorariaMovil);
        return hoy.get(Calendar.YEAR) == calendario.get(Calendar.YEAR) &&
                hoy.get(Calendar.DAY_OF_YEAR) == calendario.get(Calendar.DAY_OF_YEAR);
    }

    private void guardarFechas() {
        Calendar tiempoActual = Calendar.getInstance(zonaHorariaMovil);

        if (fechaInicioEditText.getText().toString().trim().isEmpty() || fechaFinalEditText.getText().toString().trim().isEmpty() || horaInicioEditText.getText().toString().trim().isEmpty() || horaFinalEditText.getText().toString().trim().isEmpty()) {
            Toast.makeText(CrearOferta.this, "Debe rellenar todos los campos para poder continuar", Toast.LENGTH_SHORT).show();
            return;
        }

        if (inicioCalendario.before(tiempoActual)) {
            Toast.makeText(this, "La fecha y hora de inicio no pueden estar en el pasado", Toast.LENGTH_SHORT).show();
            return;
        }

        if (finalCalendario.before(tiempoActual)) {
            Toast.makeText(this, "La fecha y hora finales no pueden estar en el pasado", Toast.LENGTH_SHORT).show();
            return;
        }

        if (finalCalendario.before(inicioCalendario)) {
            Toast.makeText(this, "La fecha final no puede ser anterior a la fecha inicial", Toast.LENGTH_SHORT).show();
            return;
        }

        long diferenciaMillis = finalCalendario.getTimeInMillis() - inicioCalendario.getTimeInMillis();
        long diferenciaHoras = diferenciaMillis / (1000 * 60 * 60);

        if (diferenciaHoras < 1) {
            Toast.makeText(this, "La diferencia mínima de oferta debe ser de una hora", Toast.LENGTH_SHORT).show();
            return;
        }

        //si no enseña ningun mensaje es ok
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        sdf.setTimeZone(zonaHorariaLondres);

        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        sdf2.setTimeZone(zonaHorariaLondres);


        String fechaHoraInicioOriginal = fechaInicioEditText.getText().toString().trim() + " " + horaInicioEditText.getText().toString().trim();
        String fechaHoraFinalOriginal = fechaFinalEditText.getText().toString().trim() + " " + horaFinalEditText.getText().toString().trim();
        SimpleDateFormat formatoOriginal = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        formatoOriginal.setTimeZone(zonaHorariaMovil);

        try {

            // 1. Parse the string to a Date object
            Date  fechaHoraInicioFormatoOriginal = formatoOriginal.parse(fechaHoraInicioOriginal);

            // 2. Convert the Date object to London's timezone
            SimpleDateFormat formatoLondresInicio = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            formatoLondresInicio.setTimeZone(zonaHorariaLondres);

            Date fechaHoraFinalFormatoOriginal = formatoOriginal.parse(fechaHoraFinalOriginal);


            // 2. Convert the Date object to London's timezone
            SimpleDateFormat formatoLondresFinal = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            formatoLondresFinal.setTimeZone(zonaHorariaLondres);

            // 3. Display the original date/time and the converted date/time
            Toast.makeText(this, "Londres inicio: " + formatoLondresInicio.format(fechaHoraInicioFormatoOriginal), Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "Londres final: " + formatoLondresFinal.format(fechaHoraFinalFormatoOriginal), Toast.LENGTH_SHORT).show();

        } catch (ParseException e) {
            //e.printStackTrace();
        }

    }
}