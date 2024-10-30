package com.example.garajeando;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
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
    private  TextView fechaHoraInicioTextView, fechaHoraFinTextView, propietarioTextView, marcaTextView, modeloTextView, plazasTextView,puertasTextView, transmisionTextView, combustibleTextView, aireAcondicionadoTextView, bluetoothTextView, gpsTextView, descripcionTextView;
    private  Button modificarOfertaButton, eliminarOfertaButton;

    private  String usuario, idComunidad, idCoche, idOferta, fechaHoraInicio, fechaHoraFin;
    Boolean fechaAnterior = false;

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

        modificarOfertaButton = findViewById(R.id.modificarOfertaButton);
        eliminarOfertaButton = findViewById(R.id.eliminarOfertaButton);

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

    private void obtenerInfoOferta(){
        StringRequest peticion = new StringRequest(Request.Method.POST,
                Constantes.URL_OBTENERINFOOFERTA,
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
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("IdCoche", idCoche);
                parametros.put("IdOferta", idOferta);

                return parametros;
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
                    fechaAnterior = true;
                }
            } catch (ParseException e) {
                //e.printStackTrace();
            }

            if(propietario.equals(usuario) && fechaAnterior){
                modificarOfertaButton.setVisibility(View.VISIBLE);
                eliminarOfertaButton.setVisibility(View.VISIBLE);
            }else{
                modificarOfertaButton.setVisibility(View.GONE);
                eliminarOfertaButton.setVisibility(View.GONE);
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

        AlertDialog dialogo = builder.create();
        dialogo.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == 3){
            obtenerInfoOferta();
        }
    }
}