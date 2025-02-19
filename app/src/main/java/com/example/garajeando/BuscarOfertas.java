package com.example.garajeando;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class BuscarOfertas extends AppCompatActivity {

    private final Activity activity=this;
    private final Context context = this;

    private String usuario, idComunidad, fechaInicio, fechaFinal, horaInicio, horaFinal;

    private EditText fechaInicioEditText, fechaFinalEditText, horaInicioEditText, horaFinalEditText;
    private TextView avisoBuscarOfertaTextView;
    private Button buscarOfertaButton;
    private ListView ofertasListView;

    private Calendar inicioCalendario, finalCalendario;
    private TimeZone zonaHorariaMovil = TimeZone.getDefault();
    private TimeZone zonaHorariaLondres = TimeZone.getTimeZone("Europe/London");

    private JSONArray respuestaOfertas;

    private final ArrayList<Oferta> ofertas=new ArrayList<Oferta>();

    private ListaOfertasBusquedaAdapter adapterO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Preferencias.aplicarTema(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_buscar_ofertas);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        usuario = getIntent().getExtras().getString("usuario");
        idComunidad = getIntent().getExtras().getString("idComunidad");

        setSupportActionBar(findViewById(R.id.buscarOfertasToolbar));
        getSupportActionBar().setTitle("BÚSQUEDA DE COCHES");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        fechaInicioEditText = findViewById(R.id.fechaInicioBuscarEditText);
        fechaFinalEditText = findViewById(R.id.fechaFinalBuscarEditText);

        horaInicioEditText = findViewById(R.id.horaInicioBuscarEditText);
        horaFinalEditText = findViewById(R.id.horaFinalBuscarEditText);

        buscarOfertaButton = findViewById(R.id.buscarOfertaButton);

        avisoBuscarOfertaTextView = findViewById(R.id.avisoBuscarOfertaTextView);
        avisoBuscarOfertaTextView.setVisibility(View.GONE);

        inicioCalendario = Calendar.getInstance(zonaHorariaMovil);
        finalCalendario = Calendar.getInstance(zonaHorariaMovil);

        ofertasListView = findViewById(R.id.ofertasBusquedaListView);

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

        buscarOfertaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarFechas();
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("aviso", avisoBuscarOfertaTextView.getText().toString().trim());
        outState.putBoolean("avisoVisible",avisoBuscarOfertaTextView.getVisibility() == View.VISIBLE);
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

        avisoBuscarOfertaTextView.setText(String.valueOf(savedInstanceState.getString("aviso")));
        if(savedInstanceState.getBoolean("avisoVisible")){avisoBuscarOfertaTextView.setVisibility(View.VISIBLE);}

        fechaInicioEditText.setText(String.valueOf(savedInstanceState.getString("fechaInicio")));
        fechaFinalEditText.setText(String.valueOf(savedInstanceState.getString("fechaFinal")));
        horaInicioEditText.setText(String.valueOf(savedInstanceState.getString("horaInicio")));
        horaFinalEditText.setText(String.valueOf(savedInstanceState.getString("horaFinal")));

        inicioCalendario.setTimeInMillis(savedInstanceState.getLong("inicioCalendario"));
        finalCalendario.setTimeInMillis(savedInstanceState.getLong("finalCalendario"));

        guardarFechas();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Adjust visibility based on conditions
        menu.findItem(R.id.BuscarToolbarItem).setVisible(false);
        //menu.findItem(R.id.PerfilToobarItem).setVisible(condition2);
        //menu.findItem(R.id.PreferenciasToobarItem).setVisible(condition3);
        menu.findItem(R.id.AdministradorToobarItem).setVisible(false);
        //menu.findItem(R.id.CerrarSesionToobarItem).setVisible(CerrarSesionToobarItem);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.PerfilToobarItem) {
            Intent intentPerfil = new Intent(BuscarOfertas.this, PerfilUsuario.class);
            intentPerfil.putExtra("usuario", usuario);
            intentPerfil.putExtra("idComunidad", idComunidad);
            intentPerfil.putExtra("idUsuarioPerfil", usuario);
            intentPerfil.putExtra("Administrador", "No");
            startActivityForResult(intentPerfil,1);
            return true;
        } else if (itemId == R.id.TemaToobarItem) {
            boolean esOscuro = Preferencias.esTemaOscuro(this);
            Preferencias.setTemaOscuro(this, !esOscuro);
            Intent intentTema = getIntent();
            finish();
            startActivity(intentTema);
            return true;
        } else if (itemId == R.id.CerrarSesionToobarItem) {
            Intent intentCerrarSesion = new Intent(BuscarOfertas.this, IniciarSesion.class);
            // Clear all activities in the current task stack
            intentCerrarSesion.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intentCerrarSesion);
            finish();
            return true;
        }

        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void mostrarDialogoFecha(Calendar calendario, EditText editText, boolean comienzo) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(BuscarOfertas.this,
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
                            avisoBuscarOfertaTextView.setVisibility(View.VISIBLE);
                            avisoBuscarOfertaTextView.setText("La fecha final no puede ser anterior a la fecha inicial");
                        }

                        if (comienzo && !finalCalendario.equals(Calendar.getInstance())  && !fechaFinalEditText.getText().toString().trim().isEmpty()) {
                            if (calendario.getTime().after(finalCalendario.getTime())) {
                                avisoBuscarOfertaTextView.setVisibility(View.VISIBLE);
                                avisoBuscarOfertaTextView.setText("La fecha inical no puede ser posterior a la fecha final");
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

        TimePickerDialog timePickerDialog = new TimePickerDialog(BuscarOfertas.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int horaDia, int minutoDia) {
                        if (comienzo && esHoy(calendario)) {
                            if (horaDia < horaActual.get(Calendar.HOUR_OF_DAY) ||
                                    (horaDia == horaActual.get(Calendar.HOUR_OF_DAY) && minutoDia < horaActual.get(Calendar.MINUTE))) {
                                avisoBuscarOfertaTextView.setVisibility(View.VISIBLE);
                                avisoBuscarOfertaTextView.setText("La hora inicial no puede ser anterior a la hora actual");
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
                                avisoBuscarOfertaTextView.setVisibility(View.VISIBLE);
                                avisoBuscarOfertaTextView.setText("La hora final no puede ser igual o anterior a la hora inicial si se trata del mismo día");
                            }
                        }

                        if (comienzo && !finalCalendario.equals(Calendar.getInstance())) {
                            if (calendario.getTime().after(finalCalendario.getTime()) && !horaFinalEditText.getText().toString().trim().isEmpty()) {
                                avisoBuscarOfertaTextView.setVisibility(View.VISIBLE);
                                avisoBuscarOfertaTextView.setText("La hora inicial no puede ser posterior a la hora final si se trata del mismo día");
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
            avisoBuscarOfertaTextView.setVisibility(View.VISIBLE);
            avisoBuscarOfertaTextView.setText("Debe rellenar todos los campos para poder continuar");
            return;
        }

        if (inicioCalendario.before(tiempoActual)) {
            avisoBuscarOfertaTextView.setVisibility(View.VISIBLE);
            avisoBuscarOfertaTextView.setText("La fecha y hora de inicio no pueden estar en el pasado");
            return;
        }

        if (finalCalendario.before(tiempoActual)) {
            avisoBuscarOfertaTextView.setVisibility(View.VISIBLE);
            avisoBuscarOfertaTextView.setText("La fecha y hora finales no pueden estar en el pasado");
            return;
        }

        if (finalCalendario.before(inicioCalendario)) {
            avisoBuscarOfertaTextView.setVisibility(View.VISIBLE);
            avisoBuscarOfertaTextView.setText("La fecha final no puede ser anterior a la fecha inicial");
            return;
        }

        long diferenciaMillis = finalCalendario.getTimeInMillis() - inicioCalendario.getTimeInMillis();
        long diferenciaHoras = diferenciaMillis / (1000 * 60 * 60);

        if (diferenciaHoras < 1) {
            avisoBuscarOfertaTextView.setVisibility(View.VISIBLE);
            avisoBuscarOfertaTextView.setText("La diferencia mínima de oferta debe ser de una hora");
            return;
        }
        //caso cuando es todo ok
        avisoBuscarOfertaTextView.setVisibility(View.GONE);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        sdf.setTimeZone(zonaHorariaLondres);

        SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        sdf2.setTimeZone(zonaHorariaLondres);

        String fechaHoraInicioOriginal = fechaInicioEditText.getText().toString().trim() + " " + horaInicioEditText.getText().toString().trim();
        String fechaHoraFinalOriginal = fechaFinalEditText.getText().toString().trim() + " " + horaFinalEditText.getText().toString().trim();
        SimpleDateFormat formatoOriginal = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        formatoOriginal.setTimeZone(zonaHorariaMovil);

        try {
            Date fechaHoraInicioFormatoOriginal = formatoOriginal.parse(fechaHoraInicioOriginal);

            SimpleDateFormat formatoLondresInicio = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            formatoLondresInicio.setTimeZone(zonaHorariaLondres);

            Date fechaHoraFinalFormatoOriginal = formatoOriginal.parse(fechaHoraFinalOriginal);

            SimpleDateFormat formatoLondresFinal = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            formatoLondresFinal.setTimeZone(zonaHorariaLondres);

            String fechaHoraInicio = formatoLondresInicio.format(fechaHoraInicioFormatoOriginal);
            String fechaHoraFinal = formatoLondresFinal.format(fechaHoraFinalFormatoOriginal);


            String idComunidadEncoded = URLEncoder.encode(idComunidad, "UTF-8");
            String idUsuarioEncoded = URLEncoder.encode(usuario, "UTF-8");
            String fechaHoraInicioEncoded = URLEncoder.encode(fechaHoraInicio, "UTF-8");
            String fechaHoraFinEncoded = URLEncoder.encode(fechaHoraFinal, "UTF-8");

            StringRequest peticion = new StringRequest(Request.Method.GET,
                    Constantes.URL_OBTENEROFERTAS+"?IdComunidad="+idComunidadEncoded+"&IdUsuario="+idUsuarioEncoded+"&FechaHoraInicio="+fechaHoraInicioEncoded+"&FechaHoraFin="+fechaHoraFinEncoded,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String respuesta) {
                            try {
                                JSONObject objetoJSON = new JSONObject(respuesta);

                                if (objetoJSON.getString("error").equals("true") || objetoJSON.getJSONArray("mensaje").length() == 0){
                                    avisoBuscarOfertaTextView.setVisibility(View.VISIBLE);
                                    avisoBuscarOfertaTextView.setText("No se han encontrado ofertas entre ese rango de fechas.");
                                    ofertasListView.setVisibility(View.GONE);
                                }else{
                                    avisoBuscarOfertaTextView.setVisibility(View.GONE);
                                    ofertasListView.setVisibility(View.VISIBLE);
                                    //ver ofertas

                                    respuestaOfertas = objetoJSON.getJSONArray("mensaje");

                                    //float factor = context.getResources().getDisplayMetrics().density;
                                    //ofertasListView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) ((150 + (objetoJSON.getJSONArray("mensaje").length()-1)*160) * factor)));

                                    guardarOfertas();

                                    adapterO = new ListaOfertasBusquedaAdapter(activity, activity, usuario, idComunidad, ofertas, fechaHoraInicio, fechaHoraFinal);
                                    ofertasListView.setAdapter(adapterO);
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
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("Content-Type","application/x-www-form-urlencoded");
                    return params;
                }
            };

            peticion.setTag("peticion");
            AdministradorPeticiones.getInstance(this).addToRequestQueue(peticion);



        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    private void guardarOfertas(){
        try{
            ofertas.clear();
            for (int j = 0; j < respuestaOfertas.length(); j++){
                JSONObject jsonOfertas = respuestaOfertas.getJSONObject(j);

                ofertas.add(new Oferta(jsonOfertas.getString("IdOferta"),
                        jsonOfertas.getString("IdCoche"),
                        jsonOfertas.getString("IdComunidad"),
                        jsonOfertas.getString("FechaHoraInicio"),
                        jsonOfertas.getString("FechaHoraFin"),
                        jsonOfertas.getString("FotoCoche"),
                        jsonOfertas.getString("Matricula"),
                        jsonOfertas.getString("Reservada")));
            }

            if(respuestaOfertas.length() == 0){
                findViewById(R.id.ofertasBusquedaListView).setVisibility(View.GONE);
            }
        }catch (Exception e){
            //no hace nada
        }
    }
}