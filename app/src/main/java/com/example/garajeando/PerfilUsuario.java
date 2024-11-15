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
import java.util.HashMap;
import java.util.Map;

public class PerfilUsuario extends AppCompatActivity {

    private Activity activity = this;
    private Context context = this;

    String usuario, idComunidad, idUsuarioPerfil, administrador;

    private ImageView fotoPerfilUsuarioImageView;
    private TextView nombreUsuarioTextView, apellidosTextView, correoTextView, direccionTextView, puntosTextView;
    private Button modificarDatosButton;

    String nombre, apellidos, correo, direccion, puntos, nombreFotoPrincipal;

    private static final String URL_BASE_FOTOS = "http://ec2-51-20-10-72.eu-north-1.compute.amazonaws.com/imagenes/fotosperfil/";

    private JSONArray respuestaUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Preferencias.aplicarTema(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil_usuario);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        usuario = getIntent().getExtras().getString("usuario");
        idUsuarioPerfil = getIntent().getExtras().getString("idUsuarioPerfil");
        administrador = getIntent().getExtras().getString("Administrador");
        idComunidad = getIntent().getExtras().getString("IdComunidad");

        fotoPerfilUsuarioImageView = findViewById(R.id.fotoPerfilUsuarioImageView);

        nombreUsuarioTextView = findViewById(R.id.nombreUsuarioTextView);
        apellidosTextView = findViewById(R.id.apellidosTextView);
        correoTextView = findViewById(R.id.correoTextView);
        direccionTextView = findViewById(R.id.direccionTextView);
        puntosTextView = findViewById(R.id.puntosTextView);

        modificarDatosButton = findViewById(R.id.modificarDatosButton);

        obtenerInfoUsuario();

        modificarDatosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PerfilUsuario.this, ModificarPerfil.class);
                intent.putExtra("usuario", usuario);
                startActivityForResult(intent,1);
            }
        });
    }

    private void obtenerInfoUsuario(){
        String idUsuarioPerfilEncoded = "";
        try {
            idUsuarioPerfilEncoded = URLEncoder.encode(idUsuarioPerfil, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            //e.printStackTrace();
        }

        StringRequest peticion = new StringRequest(Request.Method.GET,
                Constantes.URL_OBTENERUSUARIO+"?IdUsuario="+idUsuarioPerfilEncoded,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String respuesta) {
                        try {
                            JSONObject objetoJSON = new JSONObject(respuesta);

                            respuestaUsuario = objetoJSON.getJSONArray("Usuario");

                            guardarInfoUsuario();

                            setSupportActionBar(findViewById(R.id.perfilUsuarioToolbar));
                            getSupportActionBar().setTitle(nombre + " " + apellidos);
                            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                            getSupportActionBar().setDisplayShowHomeEnabled(true);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Adjust visibility based on conditions
        menu.findItem(R.id.BuscarToolbarItem).setVisible(false);
        menu.findItem(R.id.PerfilToobarItem).setVisible(false);
        //menu.findItem(R.id.TemaToobarItem).setVisible(condition3);
        menu.findItem(R.id.AdministradorToobarItem).setVisible(false);
        //menu.findItem(R.id.CerrarSesionToobarItem).setVisible(CerrarSesionToobarItem);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.TemaToobarItem) {
            boolean esOscuro = Preferencias.esTemaOscuro(this);
            Preferencias.setTemaOscuro(this, !esOscuro);
            Intent intentTema = getIntent();
            finish();
            startActivity(intentTema);
            return true;
        } else if (itemId == R.id.CerrarSesionToobarItem) {
            Intent intentCerrarSesion = new Intent(PerfilUsuario.this, IniciarSesion.class);
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

            if(usuario.equals(idUsuarioPerfil) || administrador.equals("Administrador")){
                direccionTextView.setVisibility(View.VISIBLE);
                modificarDatosButton.setVisibility(View.VISIBLE);
            }
            else{
                direccionTextView.setVisibility(View.GONE);
                findViewById(R.id.direccionTituloTextView).setVisibility(View.GONE);
                modificarDatosButton.setVisibility(View.GONE);
            }

            if (administrador.equals("Administrador")){modificarDatosButton.setVisibility(View.GONE);}
        }catch (Exception e){
            //no hace nada
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == 3){
            obtenerInfoUsuario();
        }
    }
}