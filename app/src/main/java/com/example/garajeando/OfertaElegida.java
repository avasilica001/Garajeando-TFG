package com.example.garajeando;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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

public class OfertaElegida extends AppCompatActivity {

    private  Activity activity = this;
    private  Context context = this;

    private  ImageView imagenPrincipalImageView;
    private  TextView fechaHoraInicioTextView, fechaHoraFinTextView, propietarioTextView, marcaTextView, modeloTextView, plazasTextView,puertasTextView, transmisionTextView, combustibleTextView, aireAcondicionadoTextView, bluetoothTextView, gpsTextView, descripcionTextView, avisoVerOfertaTextView;
    private  Button modificarOfertaButton, eliminarOfertaButton, reservarOfertaButton, verPerfilUsuarioButton;

    private  String usuario, idComunidad, idCoche, idOferta, fechaHoraInicio, fechaHoraFin, reservada;
    Boolean fechaFutura = false;

    private String propietario, nombrePropietario, apellidosPropietario, matricula, marca, modelo, transmision, combustible, descripcion, nombreFotoPrincipal;
    private Integer plazas, puertas;
    private Boolean aireAcondicionado, bluetooth, gps;

    private GridView fotosGridView;
    private String[] nombreFotosCoche = new String[9];
    private static final String URL_BASE_FOTOS = "http://ec2-51-20-10-72.eu-north-1.compute.amazonaws.com/imagenes/fotoscoches/";
    private FotosCocheAdapter fotosCocheAdapter;
    private Integer numFotos;
    private JSONArray respuestaFotos, respuestaInfoCoche, respuestaInfoOferta;

    TimeZone zonaHorariaMovil = TimeZone.getDefault();
    TimeZone zonaHorariaLondres = TimeZone.getTimeZone("Europe/London");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Preferencias.aplicarTema(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_oferta_elegida);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        usuario = getIntent().getExtras().getString("usuario");
        idComunidad = getIntent().getExtras().getString("idComunidad");
        idCoche = getIntent().getExtras().getString("idCoche");
        idOferta = getIntent().getExtras().getString("idOferta");

        obtenerInfoOferta();

        imagenPrincipalImageView = findViewById(R.id.imagenPrincipalCocheOfertaImageView);
        fechaHoraInicioTextView = findViewById(R.id.fechayHoraInicioOfertaTextView);
        fechaHoraFinTextView = findViewById(R.id.fechayHoraFinOfertaTextView);
        propietarioTextView = findViewById(R.id.propietarioOfertaTextView);
        marcaTextView = findViewById(R.id.marcaOfertaTextView);
        modeloTextView = findViewById(R.id.modeloOfertaTextView);
        plazasTextView = findViewById(R.id.plazasOfertaTextView);
        puertasTextView = findViewById(R.id.puertasOfertaTextView);
        transmisionTextView = findViewById(R.id.transmisionOfertaTextView);
        combustibleTextView = findViewById(R.id.combustibleOfertaTextView);
        aireAcondicionadoTextView = findViewById(R.id.aireAcondicionadoOfertaTextView);
        bluetoothTextView = findViewById(R.id.bluetoothOfertaTextView);
        gpsTextView = findViewById(R.id.gpsOfertaTextView);
        descripcionTextView = findViewById(R.id.descripcionOfertaTextView);
        avisoVerOfertaTextView = findViewById(R.id.avisoVerOfertaTextView);
        avisoVerOfertaTextView.setVisibility(View.GONE);

        modificarOfertaButton = findViewById(R.id.modificarOfertaButton);
        eliminarOfertaButton = findViewById(R.id.eliminarOfertaButton);
        reservarOfertaButton = findViewById(R.id.reservarOfertaButton);
        verPerfilUsuarioButton = findViewById(R.id.verPerfilDuenoOfertaButton);

        modificarOfertaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CrearOferta.class);
                intent.putExtra("idComunidad", idComunidad);
                intent.putExtra("usuario", usuario);
                intent.putExtra("idCoche", idCoche);
                intent.putExtra("accion", "modificar");
                intent.putExtra("fechaHoraInicio", fechaHoraInicio);
                intent.putExtra("fechaHoraFin", fechaHoraFin);
                intent.putExtra("idOferta", idOferta);

                activity.startActivityForResult(intent, 2);
            }
        });

        eliminarOfertaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                asegurarseEliminar();
            }
        });

        reservarOfertaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preguntarReserva();
            }
        });

        verPerfilUsuarioButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OfertaElegida.this, PerfilUsuario.class);
                intent.putExtra("usuario", usuario);
                intent.putExtra("idComunidad", idComunidad);
                intent.putExtra("idUsuarioPerfil", propietario);
                intent.putExtra("Administrador", "No");
                startActivityForResult(intent,1);
            }
        });

        OnBackPressedCallback volverActividadAnterior = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intentResultado = new Intent();
                setResult(3, intentResultado);
                finish();
            }
        };

        getOnBackPressedDispatcher().addCallback(this, volverActividadAnterior);

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
            Intent intentPerfil = new Intent(OfertaElegida.this, PerfilUsuario.class);
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
            Intent intentCerrarSesion = new Intent(OfertaElegida.this, IniciarSesion.class);
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

    private void obtenerInfoOferta(){
        String idCocheEncoded = "";
        String idOfertaEncoded = "";
        try {
            idCocheEncoded = URLEncoder.encode(idCoche, "UTF-8");
            idOfertaEncoded = URLEncoder.encode(idOferta, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            //e.printStackTrace();
        }

        StringRequest peticion = new StringRequest(Request.Method.GET,
                Constantes.URL_OBTENERINFOOFERTA+"?IdCoche="+idCocheEncoded+"&IdOferta="+idOfertaEncoded,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String respuesta) {
                        try {
                            JSONObject objetoJSON = new JSONObject(respuesta);

                            limpiarInfo();
                            respuestaFotos = objetoJSON.getJSONArray("Fotos");
                            numFotos = respuestaFotos.length();
                            respuestaInfoCoche = objetoJSON.getJSONArray("Coche");
                            respuestaInfoOferta = objetoJSON.getJSONArray("Oferta");
                            guardarInfo();

                            //fotosCocheAdapter = new FotosCocheAdapter(context, nombreFotosCoche, numFotos);
                            //fotosGridView.setAdapter(fotosCocheAdapter);
                            //fotosCocheAdapter.notifyDataSetChanged();

                            setSupportActionBar(findViewById(R.id.matriculaOfertaElegidaToolbar));
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

    private void limpiarInfo(){
        imagenPrincipalImageView.setImageResource(R.drawable.coche);
        nombreFotosCoche = new String[9];

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
            reservada = respuestaInfoOferta.getJSONObject(0).getString("Reservada");

            SimpleDateFormat formatoOriginal = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            formatoOriginal.setTimeZone(zonaHorariaLondres);

            try{
                Date fechaHoraInicioFormatoOriginal = formatoOriginal.parse(respuestaInfoOferta.getJSONObject(0).getString("FechaHoraInicio"));

                SimpleDateFormat formatoMovilInicio = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                formatoMovilInicio.setTimeZone(zonaHorariaMovil);

                Date fechaHoraFinalFormatoOriginal = formatoOriginal.parse(respuestaInfoOferta.getJSONObject(0).getString("FechaHoraFin"));

                SimpleDateFormat formatoMovilFin = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                formatoMovilFin.setTimeZone(zonaHorariaMovil);

                fechaHoraInicioTextView.setText(formatoMovilInicio.format(fechaHoraInicioFormatoOriginal));
                fechaHoraFinTextView.setText(formatoMovilFin.format(fechaHoraFinalFormatoOriginal));

                fechaHoraInicio = formatoMovilInicio.format(fechaHoraInicioFormatoOriginal);
                fechaHoraFin = formatoMovilFin.format(fechaHoraFinalFormatoOriginal);

                Calendar calendario = Calendar.getInstance();
                Date fechaFinOferta = formatoMovilFin.parse(formatoMovilFin.format(fechaHoraFinalFormatoOriginal));
                Date fechaActual = calendario.getTime();

                if (fechaActual.before(fechaFinOferta) || fechaFinOferta.compareTo(fechaActual) == 0) {
                    fechaFutura = true;
                }
            } catch (ParseException e) {
                //e.printStackTrace();
            }

            if(propietario.equals(usuario) && fechaFutura){
                modificarOfertaButton.setVisibility(View.VISIBLE);
                eliminarOfertaButton.setVisibility(View.VISIBLE);
                reservarOfertaButton.setVisibility(View.GONE);
            }else{
                modificarOfertaButton.setVisibility(View.GONE);
                eliminarOfertaButton.setVisibility(View.GONE);
                reservarOfertaButton.setVisibility(View.GONE);
            }

            if(!propietario.equals(usuario) && fechaFutura && reservada.equals("0")){
                reservarOfertaButton.setVisibility(View.VISIBLE);
            }

        }catch (Exception e){
            //no hace nada
        }
    }

    private void asegurarseEliminar(){
        AlertDialog.Builder builder = new AlertDialog.Builder(OfertaElegida.this);
        builder.setTitle("Elige una opción")
                .setMessage("¿Estás seguro de que quieres eliminar la oferta?")

                // Positive button action
                .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogo, int which) {
                        StringRequest peticion = new StringRequest(Request.Method.POST,
                                Constantes.URL_ELIMINAROFERTA,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String respuesta){

                                        Intent intentResultado = new Intent();
                                        setResult(3, intentResultado);
                                        finish();
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

                                return parametros;
                            }
                        };

                        peticion.setTag("peticion");
                        AdministradorPeticiones.getInstance(context).addToRequestQueue(peticion);
                    }
                })

                // Negative button action
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogo, int which) {
                        dialogo.dismiss();
                    }
                });

        Preferencias.setTemaAlertDialogPositivoNegativo(builder, context);
    }

    private void preguntarReserva(){
        AlertDialog.Builder builder = new AlertDialog.Builder(OfertaElegida.this);
        builder.setTitle("Elige una opción")
                .setMessage("¿Estás seguro de que quieres reservar este coche?")

                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogo, int which) {
                        String fechaHoraInicioOriginal = getIntent().getExtras().getString("fechaHoraInicio");
                        String fechaHoraFinalOriginal = getIntent().getExtras().getString("fechaHoraFin");
                        SimpleDateFormat formatoOriginal = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        formatoOriginal.setTimeZone(zonaHorariaMovil);

                        try {
                            Date  fechaHoraInicioFormatoOriginal = formatoOriginal.parse(fechaHoraInicioOriginal);

                            SimpleDateFormat formatoLondresInicio = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                            formatoLondresInicio.setTimeZone(zonaHorariaLondres);

                            Date fechaHoraFinalFormatoOriginal = formatoOriginal.parse(fechaHoraFinalOriginal);

                            SimpleDateFormat formatoLondresFinal = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                            formatoLondresFinal.setTimeZone(zonaHorariaLondres);

                            String fechaHoraInicioReserva = formatoLondresInicio.format(fechaHoraInicioFormatoOriginal);
                            String fechaHoraFinalReserva = formatoLondresFinal.format(fechaHoraFinalFormatoOriginal);

                            StringRequest peticion = new StringRequest(Request.Method.POST,
                                    Constantes.URL_CREARRESERVA,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String respuesta){
                                            try {
                                                JSONObject objetoJSON = new JSONObject(respuesta);

                                                if (objetoJSON.getString("error").equals("false")) {
                                                    Intent intentResultado = new Intent();
                                                    setResult(3, intentResultado);
                                                    finish();
                                                    Toast.makeText(OfertaElegida.this, "¡Ahora solo queda esperar a que el dueño del coche acepte la reserva!", Toast.LENGTH_LONG).show();
                                                }else{
                                                    avisoVerOfertaTextView.setVisibility(View.VISIBLE);
                                                    avisoVerOfertaTextView.setText(objetoJSON.getString("mensaje"));
                                                }
                                            } catch (JSONException e) {
                                                //throw new RuntimeException(e);
                                            }
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
                                    parametros.put("IdUsuario", usuario);
                                    parametros.put("IdComunidad", idComunidad);
                                    parametros.put("IdCoche", idCoche);
                                    parametros.put("FechaHoraInicio", fechaHoraInicioReserva);
                                    parametros.put("FechaHoraFin", fechaHoraFinalReserva);
                                    parametros.put("IdOferta", idOferta);

                                    return parametros;
                                }
                            };

                            peticion.setTag("peticion");
                            AdministradorPeticiones.getInstance(context).addToRequestQueue(peticion);
                        } catch (ParseException e) {
                            //e.printStackTrace();
                        }
                    }
                })

                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogo, int which) {
                        dialogo.dismiss();
                    }
                });

        Preferencias.setTemaAlertDialogPositivoNegativo(builder,context);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == 3){
            obtenerInfoOferta();
        }
    }
}