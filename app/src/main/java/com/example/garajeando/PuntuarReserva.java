package com.example.garajeando;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
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

public class PuntuarReserva extends AppCompatActivity {

    private Activity activity = this;
    private Context context = this;

    private String usuario, idComunidad, idUsuarioReserva, idReserva, idCoche, nombreFotoPrincipal;

    private ImageView imagenPrincipalCocheResenaImageView;

    private static final String URL_BASE_FOTOS = "http://ec2-51-20-10-72.eu-north-1.compute.amazonaws.com/imagenes/fotoscoches/";

    private JSONArray respuestaFotos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Preferencias.aplicarTema(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_puntuar_reserva);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        usuario = getIntent().getExtras().getString("usuario");
        idComunidad = getIntent().getExtras().getString("idComunidad");
        idUsuarioReserva = getIntent().getExtras().getString("idUsuarioReserva");
        idReserva = getIntent().getExtras().getString("idReserva");
        idCoche = getIntent().getExtras().getString("idCoche");

        setSupportActionBar(findViewById(R.id.puntuarReservaToolbar));
        getSupportActionBar().setTitle("PUNTUAR RESERVA");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        imagenPrincipalCocheResenaImageView = findViewById(R.id.imagenPrincipalCocheResenaImageView);

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
            Intent intentPerfil = new Intent(PuntuarReserva.this, PerfilUsuario.class);
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
            Intent intentCerrarSesion = new Intent(PuntuarReserva.this, IniciarSesion.class);
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
                            guardarInfo();

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
            Glide.with(this.context).load(URL_BASE_FOTOS+nombreFotoPrincipal).into(imagenPrincipalCocheResenaImageView);

        }catch (Exception e){
            //no hace nada
        }
    }
}