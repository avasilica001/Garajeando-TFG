package com.example.garajeando;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class CrearOferta extends AppCompatActivity {

    Context context = this;

    String usuario, idComunidad, idCoche, idOferta, fechaInicio, fechaFinal, horaInicio, horaFinal, accion;

    EditText fechaInicioEditText, fechaFinalEditText, horaInicioEditText, horaFinalEditText;
    TextView avisoCrearOfertaTextView;
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
        accion = getIntent().getExtras().getString("accion");

        fechaInicioEditText = findViewById(R.id.fechaInicioEditText);
        fechaFinalEditText = findViewById(R.id.fechaFinalEditText);

        horaInicioEditText = findViewById(R.id.horaInicioEditText);
        horaFinalEditText = findViewById(R.id.horaFinalEditText);

        publicarOfertaButton = findViewById(R.id.publicarOfertaButton);

        setSupportActionBar(findViewById(R.id.crearOfertaToolbar));
        if(accion.equals("modificar")){
            getSupportActionBar().setTitle("MODIFICAR OFERTA");
            idOferta = getIntent().getExtras().getString("idOferta");
            fechaInicio = getIntent().getExtras().getString("fechaHoraInicio");
            fechaFinal = getIntent().getExtras().getString("fechaHoraFin");

            fechaInicioEditText.setText(fechaInicio.split(" ")[0]);
            horaInicioEditText.setText(fechaInicio.split(" ")[1]);

            fechaFinalEditText.setText(fechaFinal.split(" ")[0]);
            horaFinalEditText.setText(fechaFinal.split(" ")[1]);

            inicioCalendario = Calendar.getInstance(zonaHorariaMovil);
            finalCalendario = Calendar.getInstance(zonaHorariaMovil);

            publicarOfertaButton.setText("MODIFICAR OFERTA");

            try {
                inicioCalendario.setTime(new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).parse(fechaInicio));
                finalCalendario.setTime(new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).parse(fechaFinal));
            } catch (Exception e) {
                //e.printStackTrace();
            }

        }else{
            getSupportActionBar().setTitle("CREAR OFERTA");
            inicioCalendario = Calendar.getInstance(zonaHorariaMovil);
            finalCalendario = Calendar.getInstance(zonaHorariaMovil);
        }

        avisoCrearOfertaTextView = findViewById(R.id.avisoCrearOfertaTextView);
        avisoCrearOfertaTextView.setVisibility(View.GONE);

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

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("aviso", avisoCrearOfertaTextView.getText().toString().trim());
        outState.putBoolean("avisoVisible",avisoCrearOfertaTextView.getVisibility() == View.VISIBLE);
        outState.putString("fechaInicio", fechaInicioEditText.getText().toString().trim());
        outState.putString("fechaFinal", fechaFinalEditText.getText().toString().trim());
        outState.putString("horaInicio",horaInicioEditText.getText().toString().trim());
        outState.putString("horaFinal", horaFinalEditText.getText().toString().trim());

        outState.putLong("inicioCalendario", inicioCalendario.getTimeInMillis());
        outState.putLong("finalCalendario", finalCalendario.getTimeInMillis());

    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        avisoCrearOfertaTextView.setText(String.valueOf(savedInstanceState.getString("aviso")));
        if(savedInstanceState.getBoolean("avisoVisible")){avisoCrearOfertaTextView.setVisibility(View.VISIBLE);}

        fechaInicioEditText.setText(String.valueOf(savedInstanceState.getString("fechaInicio")));
        fechaFinalEditText.setText(String.valueOf(savedInstanceState.getString("fechaFinal")));
        horaInicioEditText.setText(String.valueOf(savedInstanceState.getString("horaInicio")));
        horaFinalEditText.setText(String.valueOf(savedInstanceState.getString("horaFinal")));

        inicioCalendario.setTimeInMillis(savedInstanceState.getLong("inicioCalendario"));
        finalCalendario.setTimeInMillis(savedInstanceState.getLong("finalCalendario"));
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
                            avisoCrearOfertaTextView.setVisibility(View.VISIBLE);
                            avisoCrearOfertaTextView.setText("La fecha final no puede ser anterior a la fecha inicial");
                        }

                        if (comienzo && !finalCalendario.equals(Calendar.getInstance())  && !fechaFinalEditText.getText().toString().trim().isEmpty()) {
                            if (calendario.getTime().after(finalCalendario.getTime())) {
                                avisoCrearOfertaTextView.setVisibility(View.VISIBLE);
                                avisoCrearOfertaTextView.setText("La fecha inical no puede ser posterior a la fecha final");
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
                                avisoCrearOfertaTextView.setVisibility(View.VISIBLE);
                                avisoCrearOfertaTextView.setText("La hora inicial no puede ser anterior a la hora actual");
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
                                avisoCrearOfertaTextView.setVisibility(View.VISIBLE);
                                avisoCrearOfertaTextView.setText("La hora final no puede ser igual o anterior a la hora inicial si se trata del mismo día");
                            }
                        }

                        if (comienzo && !finalCalendario.equals(Calendar.getInstance())) {
                            if (calendario.getTime().after(finalCalendario.getTime()) && !horaFinalEditText.getText().toString().trim().isEmpty()) {
                                avisoCrearOfertaTextView.setVisibility(View.VISIBLE);
                                avisoCrearOfertaTextView.setText("La hora inicial no puede ser posterior a la hora final si se trata del mismo día");
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
            avisoCrearOfertaTextView.setVisibility(View.VISIBLE);
            avisoCrearOfertaTextView.setText("Debe rellenar todos los campos para poder continuar");
            return;
        }

        if (inicioCalendario.before(tiempoActual)) {
            avisoCrearOfertaTextView.setVisibility(View.VISIBLE);
            avisoCrearOfertaTextView.setText("La fecha y hora de inicio no pueden estar en el pasado");
            return;
        }

        if (finalCalendario.before(tiempoActual)) {
            avisoCrearOfertaTextView.setVisibility(View.VISIBLE);
            avisoCrearOfertaTextView.setText("La fecha y hora finales no pueden estar en el pasado");
            return;
        }

        if (finalCalendario.before(inicioCalendario)) {
            avisoCrearOfertaTextView.setVisibility(View.VISIBLE);
            avisoCrearOfertaTextView.setText("La fecha final no puede ser anterior a la fecha inicial");
            return;
        }

        long diferenciaMillis = finalCalendario.getTimeInMillis() - inicioCalendario.getTimeInMillis();
        long diferenciaHoras = diferenciaMillis / (1000 * 60 * 60);

        if (diferenciaHoras < 1) {
            avisoCrearOfertaTextView.setVisibility(View.VISIBLE);
            avisoCrearOfertaTextView.setText("La diferencia mínima de oferta debe ser de una hora");
            return;
        }
        //caso cuando es todo ok
        avisoCrearOfertaTextView.setVisibility(View.GONE);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        sdf.setTimeZone(zonaHorariaLondres);

        SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        sdf2.setTimeZone(zonaHorariaLondres);

        String fechaHoraInicioOriginal = fechaInicioEditText.getText().toString().trim() + " " + horaInicioEditText.getText().toString().trim();
        String fechaHoraFinalOriginal = fechaFinalEditText.getText().toString().trim() + " " + horaFinalEditText.getText().toString().trim();
        SimpleDateFormat formatoOriginal = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        formatoOriginal.setTimeZone(zonaHorariaMovil);

        try {
            Date  fechaHoraInicioFormatoOriginal = formatoOriginal.parse(fechaHoraInicioOriginal);

            SimpleDateFormat formatoLondresInicio = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            formatoLondresInicio.setTimeZone(zonaHorariaLondres);

            Date fechaHoraFinalFormatoOriginal = formatoOriginal.parse(fechaHoraFinalOriginal);

            SimpleDateFormat formatoLondresFinal = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            formatoLondresFinal.setTimeZone(zonaHorariaLondres);

            String fechaHoraInicio = formatoLondresInicio.format(fechaHoraInicioFormatoOriginal);
            String fechaHoraFinal = formatoLondresFinal.format(fechaHoraFinalFormatoOriginal);

            if(accion.equals("modificar")){
                StringRequest peticion = new StringRequest(Request.Method.POST,
                        Constantes.URL_MODIFICAROFERTA,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String respuesta){
                                AdministradorPeticiones.getInstance(context).cancelAll("peticion");
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                //
                            }
                        }) {
                    @Nullable
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> parametros = new HashMap<>();
                        parametros.put("IdOferta", idOferta);
                        parametros.put("IdCoche", idCoche);
                        parametros.put("IdComunidad", idComunidad);
                        parametros.put("FechaHoraInicio", fechaHoraInicio);
                        parametros.put("FechaHoraFin", fechaHoraFinal);

                        return parametros;
                    }
                };

                peticion.setTag("peticion");
                AdministradorPeticiones.getInstance(this).addToRequestQueue(peticion);
            }else{
                StringRequest peticion = new StringRequest(Request.Method.POST,
                        Constantes.URL_CREAROFERTA,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String respuesta) {
                                try {
                                    JSONObject objetoJSON = new JSONObject(respuesta);

                                    if (objetoJSON.getString("error").equals("true")){
                                        avisoCrearOfertaTextView.setVisibility(View.VISIBLE);
                                        avisoCrearOfertaTextView.setText("Ya existe una oferta dentro de ese rango de fechas");
                                    }else{
                                        avisoCrearOfertaTextView.setVisibility(View.GONE);
                                        setResult(3);
                                        finish();
                                    }

                                    AdministradorPeticiones.getInstance(context).cancelAll("peticion");
                                } catch (JSONException e) {
                                    //throw new RuntimeException(e);
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                //
                            }
                        }) {
                    @Nullable
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> parametros = new HashMap<>();
                        parametros.put("IdCoche", idCoche);
                        parametros.put("IdComunidad", idComunidad);
                        parametros.put("FechaHoraInicio", fechaHoraInicio);
                        parametros.put("FechaHoraFin", fechaHoraFinal);

                        return parametros;
                    }
                };

                peticion.setTag("peticion");
                AdministradorPeticiones.getInstance(this).addToRequestQueue(peticion);
            }


        } catch (ParseException e) {
            //e.printStackTrace();
        }

    }
}