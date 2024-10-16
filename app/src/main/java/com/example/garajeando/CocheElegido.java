package com.example.garajeando;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CocheElegido extends AppCompatActivity {

    Activity activity = this;

    private String usuario, idComunidad;

    String idCoche, propietario, nombrePropietario, apellidosPropietario, matricula, marca, modelo, transmision, combustible, descripcion;
    Integer plazas, puertas;
    Boolean aireAcondicionado, bluetooth, gps;

    private GridView fotosGridView;
    private String[] nombreFotosCoche = new String[9];
    private static final String URL_BASE_FOTOS = "http://ec2-51-20-10-72.eu-north-1.compute.amazonaws.com/imagenes/fotoscoches/";
    private FotosCocheAdapter fotosCocheAdapter;
    private final Context context = this;
    private Integer numFotos;
    private JSONArray respuestaFotos, respuestaInfo;

    ImageView imagenPrincipalImageView;
    TextView propietarioTextView, marcaTextView, modeloTextView, plazasTextView, puertasTextView, transmisionTextView, combustibleTextView, aireAcondicionadoTextView, bluetoothTextView, gpsTextView, descripcionTextView;
    Button modificarInformacionButton, crearOfertaButton;
    String nombreFotoPrincipal;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_coche_elegido);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        usuario = getIntent().getExtras().getString("usuario");
        idComunidad = getIntent().getExtras().getString("idComunidad");
        idCoche = getIntent().getExtras().getString("idCoche");

        obtenerInfoCoche();

        imagenPrincipalImageView = findViewById(R.id.imagenPrincipalCocheImageView);
        propietarioTextView = findViewById(R.id.propietarioTextView);
        marcaTextView = findViewById(R.id.marcaTextView);
        modeloTextView = findViewById(R.id.modeloTextView);
        plazasTextView = findViewById(R.id.plazasTextView);
        puertasTextView = findViewById(R.id.puertasTextView);
        transmisionTextView = findViewById(R.id.transmisionTextView);
        combustibleTextView = findViewById(R.id.combustibleTextView);
        aireAcondicionadoTextView = findViewById(R.id.aireAcondicionadoTextView);
        bluetoothTextView = findViewById(R.id.bluetoothTextView);
        gpsTextView = findViewById(R.id.gpsTextView);
        descripcionTextView = findViewById(R.id.descripcionTextView);

        fotosGridView = findViewById(R.id.imagenesSecundariasCocheGridView);

        modificarInformacionButton = findViewById(R.id.modificarDatosCocheButton);
        modificarInformacionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //se pasan todos los datos para ver la pelicula
                Intent intent = new Intent(context, ModificarCoche.class);
                intent.putExtra("idComunidad", idComunidad);
                intent.putExtra("usuario", usuario);
                intent.putExtra("idCoche", idCoche);
                intent.putExtra("accion", "modificar");

                activity.startActivityForResult(intent, 2);
            }
        });

        crearOfertaButton = findViewById(R.id.crearOfertaButton);
        crearOfertaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CrearOferta.class);
                intent.putExtra("idComunidad", idComunidad);
                intent.putExtra("usuario", usuario);
                intent.putExtra("idCoche", idCoche);

                activity.startActivityForResult(intent, 3);
            }
        });

        OnBackPressedCallback volverActividadAnterior = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                setResult(3);

                finish();
            }
        };

        getOnBackPressedDispatcher().addCallback(this, volverActividadAnterior);
    }

    private void obtenerInfoCoche(){
        StringRequest peticion = new StringRequest(Request.Method.POST,
                Constantes.URL_OBTENERINFOCOCHE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String respuesta) {
                        try {
                            JSONObject objetoJSON = new JSONObject(respuesta);
                            numFotos = Integer.parseInt(String.valueOf(objetoJSON.getJSONArray("Fotos").length()));

                            limpiarInfo();
                            respuestaFotos = objetoJSON.getJSONArray("Fotos");
                            respuestaInfo = objetoJSON.getJSONArray("Coche");
                            guardarInfo();

                            fotosCocheAdapter = new FotosCocheAdapter(context, nombreFotosCoche, numFotos);
                            fotosGridView.setAdapter(fotosCocheAdapter);
                            //fotosCocheAdapter.notifyDataSetChanged();

                            setSupportActionBar(findViewById(R.id.matriculaCocheElegidoToolbar));
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

            propietario = respuestaInfo.getJSONObject(0).getString("Propietario");
            nombrePropietario = respuestaInfo.getJSONObject(0).getString("NombrePropietario");
            apellidosPropietario = respuestaInfo.getJSONObject(0).getString("ApellidosPropietario");
            matricula = respuestaInfo.getJSONObject(0).getString("Matricula");
            marca = respuestaInfo.getJSONObject(0).getString("Marca");
            modelo = respuestaInfo.getJSONObject(0).getString("Modelo");
            plazas = Integer.parseInt(respuestaInfo.getJSONObject(0).getString("Plazas"));
            puertas = Integer.parseInt(respuestaInfo.getJSONObject(0).getString("Puertas"));
            transmision = respuestaInfo.getJSONObject(0).getString("Transmision");
            combustible = respuestaInfo.getJSONObject(0).getString("Combustible");
            aireAcondicionado = respuestaInfo.getJSONObject(0).getString("AireAcondicionado").equals("1");
            bluetooth = respuestaInfo.getJSONObject(0).getString("Bluetooth").equals("1");
            gps = respuestaInfo.getJSONObject(0).getString("GPS").equals("1");
            descripcion = respuestaInfo.getJSONObject(0).getString("Descripcion");

            if(propietario.equals(usuario)){
                modificarInformacionButton.setVisibility(View.VISIBLE);
                crearOfertaButton.setVisibility(View.VISIBLE);
            }else{
                modificarInformacionButton.setVisibility(View.GONE);
                crearOfertaButton.setVisibility(View.GONE);
            }

        }catch (Exception e){
            //no hace nada
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == 2){
             obtenerInfoCoche();
        }
    }

}