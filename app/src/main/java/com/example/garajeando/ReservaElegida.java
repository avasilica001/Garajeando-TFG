package com.example.garajeando;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
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
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class ReservaElegida extends AppCompatActivity {

    String usuario, idComunidad, idCoche, idReserva, fechaHoraInicio, fechaHoraFin, usuarioReserva;

    private Activity activity = this;
    private Context context = this;

    private ImageView imagenPrincipalImageView;
    private TextView fechaHoraInicioTextView, fechaHoraFinTextView, propietarioTextView, usuarioReservaTextView, marcaTextView, modeloTextView, plazasTextView,puertasTextView, transmisionTextView, combustibleTextView, aireAcondicionadoTextView, bluetoothTextView, gpsTextView, descripcionTextView, avisoVerReservaTextView, puntosUsuarioTituloTextView, puntosUsuarioTextView, puntosPropietarioTituloTextView, puntosPropietarioTextView;
    private Button verPerfilUsuarioButton, aceptarReservaButton, denegarReservaButton, cancelarReservaButton, puntuarReservaButton;

    private String propietario, nombrePropietario, apellidosPropietario, matricula, marca, modelo, transmision, combustible, descripcion, nombreFotoPrincipal, aceptada, nombreUsuarioReserva, apellidosUsuarioReserva, tipoReserva;
    private Integer plazas, puertas;
    private Boolean aireAcondicionado, bluetooth, gps;

    private static final String URL_BASE_FOTOS = "http://ec2-51-20-10-72.eu-north-1.compute.amazonaws.com/imagenes/fotoscoches/";

    private JSONArray respuestaFotos, respuestaInfoCoche, respuestaInfoReserva;
    private String[] nombreFotosCoche = new String[9];

    TimeZone zonaHorariaMovil = TimeZone.getDefault();
    TimeZone zonaHorariaLondres = TimeZone.getTimeZone("Europe/London");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Preferencias.aplicarTema(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reserva_elegida);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        usuario = getIntent().getExtras().getString("usuario");
        idComunidad = getIntent().getExtras().getString("idComunidad");
        idCoche = getIntent().getExtras().getString("idCoche");
        idReserva = getIntent().getExtras().getString("idReserva");
        tipoReserva = getIntent().getExtras().getString("tipoReserva");
        propietario = getIntent().getExtras().getString("propietario");

        imagenPrincipalImageView = findViewById(R.id.imagenPrincipalCocheReservaImageView);
        fechaHoraInicioTextView = findViewById(R.id.fechayHoraInicioReservaTextView);
        fechaHoraFinTextView = findViewById(R.id.fechayHoraFinReservaTextView);
        propietarioTextView = findViewById(R.id.propietarioReservaTextView);
        marcaTextView = findViewById(R.id.marcaReservaTextView);
        modeloTextView = findViewById(R.id.modeloReservaTextView);
        plazasTextView = findViewById(R.id.plazasReservaTextView);
        puertasTextView = findViewById(R.id.puertasReservaTextView);
        transmisionTextView = findViewById(R.id.transmisionReservaTextView);
        combustibleTextView = findViewById(R.id.combustibleReservaTextView);
        aireAcondicionadoTextView = findViewById(R.id.aireAcondicionadoReservaTextView);
        bluetoothTextView = findViewById(R.id.bluetoothReservaTextView);
        gpsTextView = findViewById(R.id.gpsReservaTextView);
        descripcionTextView = findViewById(R.id.descripcionReservaTextView);
        usuarioReservaTextView = findViewById(R.id.usuarioReservaTextView);
        avisoVerReservaTextView = findViewById(R.id.avisoVerReservaTextView);
        avisoVerReservaTextView.setVisibility(View.GONE);
        puntosPropietarioTextView = findViewById(R.id.puntosPropietarioReservaTextView);
        puntosPropietarioTextView.setVisibility(View.GONE);
        puntosPropietarioTituloTextView = findViewById(R.id.puntosPropietarioTituloReservaTextView);
        puntosPropietarioTituloTextView.setVisibility(View.GONE);
        puntosUsuarioTituloTextView = findViewById(R.id.puntosUsuarioTituloReservaTextView);
        puntosUsuarioTituloTextView.setVisibility(View.GONE);

        verPerfilUsuarioButton = findViewById(R.id.verPerfilusuarioReservaButton);
        aceptarReservaButton = findViewById(R.id.aceptarReservaButton);
        denegarReservaButton = findViewById(R.id.denegarReservaButton);
        cancelarReservaButton = findViewById(R.id.cancelarReservaButton);
        puntuarReservaButton = findViewById(R.id.puntuarReservaButton);

        obtenerInfoReserva();

        OnBackPressedCallback volverActividadAnterior = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intentResultado = new Intent();
                setResult(3, intentResultado);
                finish();
            }
        };

        getOnBackPressedDispatcher().addCallback(this, volverActividadAnterior);

        aceptarReservaButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                aceptarReserva();
            }
        });

        denegarReservaButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                denegarReserva();
            }
        });

        cancelarReservaButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                denegarReserva();
            }
        });

        verPerfilUsuarioButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReservaElegida.this, PerfilUsuario.class);
                intent.putExtra("usuario", usuario);
                intent.putExtra("idComunidad", idComunidad);
                intent.putExtra("idUsuarioPerfil", usuarioReserva);
                intent.putExtra("Administrador", "No");
                startActivityForResult(intent,1);
            }
        });

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
            Intent intentPerfil = new Intent(ReservaElegida.this, PerfilUsuario.class);
            intentPerfil.putExtra("usuario", usuario);
            intentPerfil.putExtra("idComunidad", idComunidad);
            intentPerfil.putExtra("idUsuarioPerfil", usuario);
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
            Intent intentCerrarSesion = new Intent(ReservaElegida.this, IniciarSesion.class);
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

    private void obtenerInfoReserva(){
        String idCocheEncoded = "";
        String idReservaEncoded = "";
        try {
            idCocheEncoded = URLEncoder.encode(idCoche, "UTF-8");
            idReservaEncoded = URLEncoder.encode(idReserva, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            //e.printStackTrace();
        }

        StringRequest peticion = new StringRequest(Request.Method.GET,
                Constantes.URL_OBTENERINFORESERVA+"?IdCoche="+idCocheEncoded+"&IdReserva="+idReservaEncoded,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String respuesta) {
                        try {
                            JSONObject objetoJSON = new JSONObject(respuesta);

                            respuestaFotos = objetoJSON.getJSONArray("Fotos");
                            respuestaInfoCoche = objetoJSON.getJSONArray("Coche");
                            respuestaInfoReserva = objetoJSON.getJSONArray("Reserva");
                            guardarInfo();

                            //fotosCocheAdapter = new FotosCocheAdapter(context, nombreFotosCoche, numFotos);
                            //fotosGridView.setAdapter(fotosCocheAdapter);
                            //fotosCocheAdapter.notifyDataSetChanged();

                            setSupportActionBar(findViewById(R.id.matriculaReservaElegidaToolbar));
                            getSupportActionBar().setTitle(matricula);
                            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                            getSupportActionBar().setDisplayShowHomeEnabled(true);

                            propietarioTextView.setText(nombrePropietario + " " + apellidosPropietario);
                            marcaTextView.setText(marca);
                            modeloTextView.setText(modelo);
                            plazasTextView.setText(String.valueOf(plazas));
                            puertasTextView.setText(String.valueOf(puertas));
                            transmisionTextView.setText(transmision);
                            combustibleTextView.setText(combustible);
                            if(aireAcondicionado){aireAcondicionadoTextView.setText("Sí");}else{aireAcondicionadoTextView.setText("No");}
                            if(bluetooth){bluetoothTextView.setText("Sí");}else{bluetoothTextView.setText("No");}
                            if(gps){gpsTextView.setText("Sí");}else{gpsTextView.setText("No");}
                            descripcionTextView.setText(descripcion);
                            usuarioReservaTextView.setText(nombreUsuarioReserva + " " + apellidosUsuarioReserva);
                            fechaHoraInicioTextView.setText(fechaHoraInicio);
                            fechaHoraFinTextView.setText(fechaHoraFin);

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
    }

    private void guardarInfo(){
        try{
            nombreFotoPrincipal = respuestaFotos.getJSONObject(0).getString("FotoCoche");
            Glide.with(this.context).load(URL_BASE_FOTOS+nombreFotoPrincipal).into(imagenPrincipalImageView);
            for (int i = 1; i < respuestaFotos.length(); i++)
            {
                JSONObject jsonCoches = respuestaFotos.getJSONObject(i);

                nombreFotosCoche[i] = jsonCoches.getString("FotoCoche");
            }

            propietario = respuestaInfoCoche.getJSONObject(0).getString("Propietario");
            nombrePropietario = respuestaInfoCoche.getJSONObject(0).getString("NombrePropietario");
            apellidosPropietario = respuestaInfoCoche.getJSONObject(0).getString("ApellidosPropietario");
            matricula = respuestaInfoCoche.getJSONObject(0).getString("Matricula");
            marca = respuestaInfoCoche.getJSONObject(0).getString("Marca");
            modelo = respuestaInfoCoche.getJSONObject(0).getString("Modelo");
            plazas = Integer.parseInt(respuestaInfoCoche.getJSONObject(0).getString("Plazas"));
            puertas = Integer.parseInt(respuestaInfoCoche.getJSONObject(0).getString("Puertas"));
            transmision = respuestaInfoCoche.getJSONObject(0).getString("Transmision");
            combustible = respuestaInfoCoche.getJSONObject(0).getString("Combustible");
            aireAcondicionado = respuestaInfoCoche.getJSONObject(0).getString("AireAcondicionado").equals("1");
            bluetooth = respuestaInfoCoche.getJSONObject(0).getString("Bluetooth").equals("1");
            gps = respuestaInfoCoche.getJSONObject(0).getString("GPS").equals("1");
            descripcion = respuestaInfoCoche.getJSONObject(0).getString("Descripcion");
            aceptada = respuestaInfoReserva.getJSONObject(0).getString("Aprobada");
            usuarioReserva = respuestaInfoReserva.getJSONObject(0).getString("IdUsuario");
            nombreUsuarioReserva = respuestaInfoReserva.getJSONObject(0).getString("Nombre");
            apellidosUsuarioReserva = respuestaInfoReserva.getJSONObject(0).getString("Apellidos");

            SimpleDateFormat formatoOriginal = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            formatoOriginal.setTimeZone(zonaHorariaLondres);

            try{
                Date fechaHoraInicioFormatoOriginal = formatoOriginal.parse(respuestaInfoReserva.getJSONObject(0).getString("FechaHoraInicio"));

                SimpleDateFormat formatoMovilInicio = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                formatoMovilInicio.setTimeZone(zonaHorariaMovil);

                Date fechaHoraFinalFormatoOriginal = formatoOriginal.parse(respuestaInfoReserva.getJSONObject(0).getString("FechaHoraFin"));

                SimpleDateFormat formatoMovilFin = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                formatoMovilFin.setTimeZone(zonaHorariaMovil);

                fechaHoraInicioTextView.setText(formatoMovilInicio.format(fechaHoraInicioFormatoOriginal));
                fechaHoraFinTextView.setText(formatoMovilFin.format(fechaHoraFinalFormatoOriginal));

                fechaHoraInicio = formatoMovilInicio.format(fechaHoraInicioFormatoOriginal);
                fechaHoraFin = formatoMovilFin.format(fechaHoraFinalFormatoOriginal);

                Calendar calendario = Calendar.getInstance();
                Date fechaFinOferta = formatoMovilFin.parse(formatoMovilFin.format(fechaHoraFinalFormatoOriginal));
                Date fechaActual = calendario.getTime();
            } catch (ParseException e) {
                //e.printStackTrace();
            }

            if((propietario.equals(usuario) || usuarioReserva.equals(usuario)) && aceptada.equals("1")){
                cancelarReservaButton.setVisibility(View.VISIBLE);
            }

            if(tipoReserva.equals("aceptar")){
                puntuarReservaButton.setVisibility(View.GONE);
                if(propietario.equals(usuario)) {
                    aceptarReservaButton.setVisibility(View.VISIBLE);
                    denegarReservaButton.setVisibility(View.VISIBLE);
                    cancelarReservaButton.setVisibility(View.GONE);
                }else{
                    aceptarReservaButton.setVisibility(View.GONE);
                    denegarReservaButton.setVisibility(View.GONE);
                    cancelarReservaButton.setVisibility(View.VISIBLE);
                }
            }else if(tipoReserva.equals("futuras")){
                aceptarReservaButton.setVisibility(View.GONE);
                denegarReservaButton.setVisibility(View.GONE);
                puntuarReservaButton.setVisibility(View.GONE);
                if(propietario.equals(usuario)){
                    cancelarReservaButton.setVisibility(View.VISIBLE);
                }else{
                    cancelarReservaButton.setVisibility(View.GONE);
                }
            }else if(tipoReserva.equals("pasadas")){
                aceptarReservaButton.setVisibility(View.GONE);
                denegarReservaButton.setVisibility(View.GONE);
                cancelarReservaButton.setVisibility(View.GONE);

                puntosPropietarioTituloTextView.setVisibility(View.VISIBLE);
                puntosPropietarioTextView.setVisibility(View.VISIBLE);
                puntosUsuarioTituloTextView.setVisibility(View.VISIBLE);
                puntosUsuarioTextView.setVisibility(View.VISIBLE);
                if(propietario.equals(usuario)){
                    puntuarReservaButton.setVisibility(View.VISIBLE);
                }else{
                    puntuarReservaButton.setVisibility(View.GONE);
                }
            }

        }catch (Exception e){
            //no hace nada
        }
    }

    private void aceptarReserva(){
        StringRequest peticion = new StringRequest(Request.Method.POST,
                Constantes.URL_ACEPTARRESERVA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String respuesta) {
                        try {
                            JSONObject objetoJSON = new JSONObject(respuesta);

                            setResult(3);

                            finish();

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
                parametros.put("IdReserva", idReserva);

                return parametros;
            }
        };

        peticion.setTag("peticion");
        AdministradorPeticiones.getInstance(context).addToRequestQueue(peticion);
    }

    private void denegarReserva(){
        StringRequest peticion = new StringRequest(Request.Method.POST,
                Constantes.URL_DENEGARRESERVA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String respuesta) {
                        try {
                            JSONObject objetoJSON = new JSONObject(respuesta);

                            setResult(3);

                            finish();

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
                parametros.put("IdReserva", idReserva);

                return parametros;
            }
        };

        peticion.setTag("peticion");
        AdministradorPeticiones.getInstance(context).addToRequestQueue(peticion);
    }
}