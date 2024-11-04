package com.example.garajeando;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PerfilUsuario extends AppCompatActivity {

    private Activity activity = this;
    private Context context = this;

    String usuario, idComunidad, idUsuarioPerfil;

    private ImageView fotoPerfilUsuarioImageView;
    private TextView nombreUsuarioTextView, apellidosTextView, correoTextView, direccionTextView, puntosTextView;
    private Button modificarDatosButton;

    String nombre, apellidos, correo, direccion, puntos, nombreFotoPrincipal;

    private static final String URL_BASE_FOTOS = "http://ec2-51-20-10-72.eu-north-1.compute.amazonaws.com/imagenes/fotosperfil/";

    private JSONArray respuestaUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil_usuario);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        usuario = getIntent().getExtras().getString("usuario");
        idComunidad = getIntent().getExtras().getString("idComunidad");
        idUsuarioPerfil = getIntent().getExtras().getString("idUsuarioPerfil");

        fotoPerfilUsuarioImageView = findViewById(R.id.fotoPerfilUsuarioImageView);

        nombreUsuarioTextView = findViewById(R.id.nombreUsuarioTextView);
        apellidosTextView = findViewById(R.id.apellidosTextView);
        correoTextView = findViewById(R.id.correoTextView);
        direccionTextView = findViewById(R.id.direccionTextView);
        puntosTextView = findViewById(R.id.puntosTextView);

        modificarDatosButton = findViewById(R.id.modificarDatosButton);

        obtenerInfoUsuario();
    }

    private void obtenerInfoUsuario(){
        StringRequest peticion = new StringRequest(Request.Method.POST,
                Constantes.URL_OBTENERUSUARIO,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String respuesta) {
                        try {
                            JSONObject objetoJSON = new JSONObject(respuesta);

                            respuestaUsuario = objetoJSON.getJSONArray("Usuario");

                            guardarInfoUsuario();

                            setSupportActionBar(findViewById(R.id.perfilUsuarioToolbar));
                            getSupportActionBar().setTitle(nombre + " " + apellidos);

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
                parametros.put("IdUsuario", idUsuarioPerfil);

                return parametros;
            }
        };

        peticion.setTag("peticion");
        AdministradorPeticiones.getInstance(this).addToRequestQueue(peticion);
    }

    private void guardarInfoUsuario(){
        try{

            nombreFotoPrincipal = respuestaUsuario.getJSONObject(0).getString("FotoPerfil");
            if(!nombreFotoPrincipal.equals("")){
                Glide.with(this.context).load(URL_BASE_FOTOS+nombreFotoPrincipal).into(fotoPerfilUsuarioImageView);
            }

            nombre = respuestaUsuario.getJSONObject(0).getString("Nombre");
            apellidos = respuestaUsuario.getJSONObject(0).getString("Apellidos");
            correo = respuestaUsuario.getJSONObject(0).getString("CorreoElectronico");
            direccion = respuestaUsuario.getJSONObject(0).getString("Direccion");
            puntos = respuestaUsuario.getJSONObject(0).getString("PuntosTotales");

            nombreUsuarioTextView.setText(nombre);
            apellidosTextView.setText(apellidos);
            correoTextView.setText(correo);
            direccionTextView.setText(direccion);
            puntosTextView.setText(puntos);

            if(usuario.equals(idUsuarioPerfil)){
                direccionTextView.setVisibility(View.VISIBLE);
                modificarDatosButton.setVisibility(View.VISIBLE);

            }
            else{
                direccionTextView.setVisibility(View.GONE);
                findViewById(R.id.direccionTituloTextView).setVisibility(View.GONE);
                modificarDatosButton.setVisibility(View.GONE);
            }
        }catch (Exception e){
            //no hace nada
        }
    }
}