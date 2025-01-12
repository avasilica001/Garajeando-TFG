package com.example.garajeando;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HistorialPuntos extends AppCompatActivity {

    private String usuario;
    private final ArrayList<Puntos> puntos = new ArrayList<Puntos>();

    private ListaPuntosAdapter adapterPuntos;

    private final Activity activity=this;
    private final Context context = this;

    private Toolbar historialToolbar;
    private RecyclerView misPuntosRecyclerView;

    private JSONArray respuestaPuntos;

    ListView l_puntos;

    LinearLayoutManager linearLayoutManagerPuntos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Preferencias.aplicarTema(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_historial_puntos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        usuario = getIntent().getExtras().getString("usuario");

        setSupportActionBar(findViewById(R.id.barraSuperiorHistorialPuntosToolbar));
        getSupportActionBar().setTitle("HISTORIAL DE PUNTOS");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        misPuntosRecyclerView = findViewById(R.id.historialPuntosRecyclerView);

        obtenerHistorialPuntos();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("usuario", usuario);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        usuario = savedInstanceState.getString("usuario");

        obtenerHistorialPuntos();
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
        //menu.findItem(R.id.PreferenciasToobarItem).setVisible(condition3);
        menu.findItem(R.id.AdministradorToobarItem).setVisible(false);
        //menu.findItem(R.id.CerrarSesionToobarItem).setVisible(CerrarSesionToobarItem);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.BuscarToolbarItem) {
            return true;
        } else if (itemId == R.id.PerfilToobarItem) {
            return true;
        } else if (itemId == R.id.TemaToobarItem) {
            boolean esOscuro = Preferencias.esTemaOscuro(this);
            Preferencias.setTemaOscuro(this, !esOscuro);
            Intent intentTema = getIntent();
            finish();
            startActivity(intentTema);
            return true;
        } else if (itemId == R.id.AdministradorToobarItem) {
            return true;
        } else if (itemId == R.id.CerrarSesionToobarItem) {
            Intent intentCerrarSesion = new Intent(HistorialPuntos.this, IniciarSesion.class);
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

    private void obtenerHistorialPuntos(){
        String idUsuarioEncoded = "";
        try {
            idUsuarioEncoded = URLEncoder.encode(usuario, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            //e.printStackTrace();
        }

        StringRequest peticion = new StringRequest(Request.Method.GET,
                Constantes.URL_OBTENERPUNTOS+"?IdUsuario="+idUsuarioEncoded,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String respuesta) {
                        try {
                            JSONObject objetoJSON = new JSONObject(respuesta);

                            limpiarArrayLists();
                            respuestaPuntos = objetoJSON.getJSONArray("puntos");
                            guardarPuntos();

                            linearLayoutManagerPuntos = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false);
                            adapterPuntos = new ListaPuntosAdapter(activity, activity, puntos, usuario);
                            misPuntosRecyclerView.setLayoutManager(linearLayoutManagerPuntos);
                            misPuntosRecyclerView.setAdapter(adapterPuntos);
                            adapterPuntos.notifyDataSetChanged();

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

    private void limpiarArrayLists(){
        puntos.clear();
    }

    private void guardarPuntos(){
        try{
            for (int i = 0; i < respuestaPuntos.length(); i++) {
                JSONObject jsonPuntos = respuestaPuntos.getJSONObject(i);

                puntos.add(new Puntos(jsonPuntos.getString("IdUsuario"),
                        jsonPuntos.getString("FechaHora"),
                        jsonPuntos.getString("Puntos"),
                        jsonPuntos.getString("Descripcion")));
            }
        }catch (Exception e){
            //no hace nada
        }
    }
}