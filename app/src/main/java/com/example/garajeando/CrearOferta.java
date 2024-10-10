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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class CrearOferta extends AppCompatActivity {

    String usuario, idComunidad, idCoche;

    EditText fechaInicioEditText, fechaFinalEditText, horaInicioEditText, horaFinalEditText;
    Button publicarOfertaButton;
    
    Calendar inicioCalendario, finalCalendario;
    private static final String zonaHorariaEspania = "Europe/Paris"; // Central European Standard Time

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

        inicioCalendario = Calendar.getInstance(TimeZone.getTimeZone(zonaHorariaEspania));
        finalCalendario = Calendar.getInstance(TimeZone.getTimeZone(zonaHorariaEspania));

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
                        sdf.setTimeZone(TimeZone.getTimeZone(zonaHorariaEspania));
                        editText.setText(sdf.format(calendario.getTime()));

                        if (!comienzo && finalCalendario.getTime().before(inicioCalendario.getTime())) {
                            Toast.makeText(CrearOferta.this, "La fecha final no puede ser anterior a la fecha inicial", Toast.LENGTH_SHORT).show();
                            editText.setText("");
                        }

                        // Validate that the start date is not before the finish date if both dates are set
                        if (comienzo && !finalCalendario.equals(Calendar.getInstance())) {
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
        final Calendar horaActual = Calendar.getInstance(TimeZone.getTimeZone(zonaHorariaEspania));

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
                        sdf.setTimeZone(TimeZone.getTimeZone(zonaHorariaEspania));
                        editText.setText(sdf.format(calendario.getTime()));

                        if (!comienzo) {
                            boolean mismoDia = finalCalendario.get(Calendar.YEAR) == inicioCalendario.get(Calendar.YEAR) &&
                                    finalCalendario.get(Calendar.DAY_OF_YEAR) == inicioCalendario.get(Calendar.DAY_OF_YEAR);
                            if (mismoDia && finalCalendario.getTime().compareTo(inicioCalendario.getTime()) <= 0) {
                                Toast.makeText(CrearOferta.this, "La hora final no puede ser anterior o igual a la hora inicial si se trata del mismo día", Toast.LENGTH_SHORT).show();
                                editText.setText("");
                            }
                        }

                        if (comienzo && !finalCalendario.equals(Calendar.getInstance())) {
                            if (calendario.getTime().after(finalCalendario.getTime())) {
                                Toast.makeText(CrearOferta.this, "La hora inicial no puede ser posterior a la hora final si se trata del mismo día", Toast.LENGTH_SHORT).show();
                                editText.setText("");
                            }
                        }
                    }
                }, hora, minuto, true);
        timePickerDialog.show();
    }

    private boolean esHoy(Calendar calendario) {
        Calendar hoy = Calendar.getInstance(TimeZone.getTimeZone(zonaHorariaEspania));
        return hoy.get(Calendar.YEAR) == calendario.get(Calendar.YEAR) &&
                hoy.get(Calendar.DAY_OF_YEAR) == calendario.get(Calendar.DAY_OF_YEAR);
    }

    private void guardarFechas() {
        Calendar tiempoActual = Calendar.getInstance(TimeZone.getTimeZone(zonaHorariaEspania));

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
    }
}