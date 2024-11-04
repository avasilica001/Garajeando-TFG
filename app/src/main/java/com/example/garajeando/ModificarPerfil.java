package com.example.garajeando;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ModificarPerfil extends AppCompatActivity {

    private Activity activity = this;
    private Context context = this;

    String usuario, idComunidad, idUsuarioPerfil;

    String nombre, apellidos, correo, direccion, puntos, nombreFotoPrincipal, b64p;

    EditText nombreEditText, apellidosEditText, correoEditText, direccionEditText;
    TextView avisoModificarUsuarioTextView;
    ImageView fotoPerfilUsuarioModificarImageView;
    Button actualizarInformacionUsuarioButton;

    private static final String URL_BASE_FOTOS = "http://ec2-51-20-10-72.eu-north-1.compute.amazonaws.com/imagenes/fotosperfil/";
    Uri imagenPrincipalUriCambio, imagenDialogoUri, imagen;

    private static final int IMAGE_CODE=112;
    private static final int PHOTO_CODE=111;

    ActivityResultLauncher<String> activityResultLauncherElegirFoto;
    ActivityResultLauncher<Uri> activityResultLauncherSacarFoto;

    private JSONArray respuestaUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_modificar_perfil);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        nombreEditText = findViewById(R.id.nombreEditText);
        apellidosEditText = findViewById(R.id.apellidosEditText);
        correoEditText = findViewById(R.id.correoEditText);
        direccionEditText = findViewById(R.id.direccionEditText);
        avisoModificarUsuarioTextView = findViewById(R.id.avisoModificarUsuarioTextView);
        avisoModificarUsuarioTextView.setVisibility(View.GONE);

        actualizarInformacionUsuarioButton = findViewById(R.id.actualizarInformacionUsuarioButton);

        actualizarInformacionUsuarioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                nombre = nombreEditText.getText().toString().trim();
                apellidos = apellidosEditText.getText().toString().trim();
                correo = correoEditText.getText().toString().trim();
                direccion = direccionEditText.getText().toString().trim();

                if(imagenPrincipalUriCambio != null){
                    try {
                        Bitmap bprincipal = MediaStore.Images.Media.getBitmap(getContentResolver(),imagenPrincipalUriCambio);
                        ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
                        bprincipal.compress(Bitmap.CompressFormat.PNG, 100, baos1);
                        byte[] b1= baos1.toByteArray();
                        b64p = Base64.encodeToString(b1, Base64.DEFAULT);
                        String tiempo = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                        nombreFotoPrincipal = "IMG_" + tiempo + "_P.png";
                    } catch (IOException e) {}
                }else{
                    nombreFotoPrincipal = "none";
                    b64p = "none";
                }

                if(!nombre.isEmpty() && !apellidos.isEmpty() && !correo.isEmpty() && !direccion.isEmpty()) {
                    actualizarInfoUsuario();
                }
                else{
                    avisoModificarUsuarioTextView.setText("Debe rellenar todos los campos para poder continuar.");
                    avisoModificarUsuarioTextView.setVisibility(View.VISIBLE);
                }
            }
        });
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
                Glide.with(this.context).load(URL_BASE_FOTOS+nombreFotoPrincipal).into(fotoPerfilUsuarioModificarImageView);
            }

            nombre = respuestaUsuario.getJSONObject(0).getString("Nombre");
            apellidos = respuestaUsuario.getJSONObject(0).getString("Apellidos");
            correo = respuestaUsuario.getJSONObject(0).getString("CorreoElectronico");
            direccion = respuestaUsuario.getJSONObject(0).getString("Direccion");
            puntos = respuestaUsuario.getJSONObject(0).getString("PuntosTotales");

            nombreEditText.setText(nombre);
            apellidosEditText.setText(apellidos);
            correoEditText.setText(correo);
            direccionEditText.setText(direccion);

        }catch (Exception e){
            //no hace nada
        }
    }

    private void actualizarInfoUsuario(){
        StringRequest peticion = new StringRequest(Request.Method.POST,
                Constantes.URL_ACTUALIZARINFOUSUARIO,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String respuesta) {
                        try {
                            JSONObject objetoJSON = new JSONObject(respuesta);

                            respuestaUsuario = objetoJSON.getJSONArray("mensaje");

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
                parametros.put("IdUsuario", usuario);
                parametros.put("Nombre", nombre);
                parametros.put("Apellidos", apellidos);
                parametros.put("CorreoElectronico", correo);
                parametros.put("Direccion", direccion);
                parametros.put("NombreFoto", nombreFotoPrincipal);
                parametros.put("FotoPerfil", b64p);

                return parametros;
            }
        };

        peticion.setTag("peticion");
        AdministradorPeticiones.getInstance(this).addToRequestQueue(peticion);
    }
}