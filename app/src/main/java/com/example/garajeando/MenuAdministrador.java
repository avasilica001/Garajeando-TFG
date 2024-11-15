package com.example.garajeando;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

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

public class MenuAdministrador extends AppCompatActivity {

    private final Activity activity=this;
    private final Context context = this;

    String usuario, idComunidad;

    RecyclerView usuariosAceptarRecyclerView;

    JSONArray respuestaUsuariosAceptar;

    private final ArrayList<Usuario> usuariosAceptar = new ArrayList<Usuario>();

    LinearLayoutManager linearLayoutManagerUsuariosAceptar;
    private ListaUsuariosAdapter adapterUsuariosAceptar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu_administrador);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        usuario = getIntent().getExtras().getString("usuario");
        idComunidad = getIntent().getExtras().getString("idComunidad");

        obtenerInfoMenuAdministradorComunidad();

        setSupportActionBar(findViewById(R.id.menuAdministradorToolbar));
        getSupportActionBar().setTitle("MENÚ ADMINISTRADOR COMUNIDAD");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        usuariosAceptarRecyclerView = findViewById(R.id.usuariosAceptarRecyclerView);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("usuario", usuario);
        outState.putString("idComunidad", idComunidad);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        usuario = savedInstanceState.getString("usuario");
        idComunidad = savedInstanceState.getString("idComunidad");

        obtenerInfoMenuAdministradorComunidad();
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

        if (itemId == R.id.BuscarToolbarItem) {
            Intent intentBuscar = new Intent(this, MenuAdministrador.class);
            intentBuscar.putExtra("idComunidad", idComunidad);
            intentBuscar.putExtra("usuario", usuario);
            startActivity(intentBuscar);
            return true;
        } else if (itemId == R.id.PerfilToobarItem) {
            Intent intentPerfil = new Intent(MenuAdministrador.this, PerfilUsuario.class);
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
        } else if (itemId == R.id.AdministradorToobarItem) {
            Intent intentAdministrador = new Intent(MenuAdministrador.this, MenuAdministrador.class);
            intentAdministrador.putExtra("usuario", usuario);
            intentAdministrador.putExtra("idComunidad", idComunidad);
            startActivityForResult(intentAdministrador,1);
            return true;
        } else if (itemId == R.id.CerrarSesionToobarItem) {
            Intent intentCerrarSesion = new Intent(MenuAdministrador.this, IniciarSesion.class);
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

    private void obtenerInfoMenuAdministradorComunidad(){
        String idComunidadEncoded = "";
        try {
            idComunidadEncoded = URLEncoder.encode(idComunidad, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            //e.printStackTrace();
        }

        StringRequest peticion = new StringRequest(Request.Method.GET,
                Constantes.URL_OBTENERINFOCOMUNIDAD+"?IdComunidad="+idComunidadEncoded,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String respuesta) {
                        try {
                            JSONObject objetoJSON = new JSONObject(respuesta);
                            //limpiarArrayLists();
                            respuestaUsuariosAceptar = objetoJSON.getJSONArray("UsuariosAceptar");
                            //respuestaCochesOtrasComunidades = objetoJSON.getJSONArray("cochesOtrasComunidades");
                            //respuestaOfertasFuturas = objetoJSON.getJSONArray("ofertasFuturas");
                            //respuestaOfertasPasadas = objetoJSON.getJSONArray("ofertasPasadas");
                            //respuestaReservasAceptar = objetoJSON.getJSONArray("reservasPendientes");
                            guardarUsuariosAceptar();
                            //guardarCochesOtrasComunidades();
                            //guardarOfertasFuturas();
                            //guardarOfertasPasadas();
                            //guardarReservasAceptar();

                            linearLayoutManagerUsuariosAceptar = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false);
                            adapterUsuariosAceptar = new ListaUsuariosAdapter(activity, activity, usuariosAceptar, usuario, idComunidad);
                            usuariosAceptarRecyclerView.setLayoutManager(linearLayoutManagerUsuariosAceptar);
                            usuariosAceptarRecyclerView.setAdapter(adapterUsuariosAceptar);
                            adapterUsuariosAceptar.notifyDataSetChanged();

                            /*linearLayoutManagerOfertasFuturas = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false);
                            adapterOfertasFuturas = new ListaOfertasAdapter(activity, activity, ofertasFuturas, usuario, idComunidad, "futuras");
                            misOfertasFuturasRecyclerView.setLayoutManager(linearLayoutManagerOfertasFuturas);
                            misOfertasFuturasRecyclerView.setAdapter(adapterOfertasFuturas);
                            adapterOfertasFuturas.notifyDataSetChanged();

                            linearLayoutManagerOfertasPasadas = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false);
                            adapterOfertasPasadas = new ListaOfertasAdapter(activity, activity, ofertasPasadas, usuario, idComunidad, "pasadas");
                            misOfertasPasadasRecyclerView.setLayoutManager(linearLayoutManagerOfertasPasadas);
                            misOfertasPasadasRecyclerView.setAdapter(adapterOfertasPasadas);
                            adapterOfertasPasadas.notifyDataSetChanged();

                            linearLayoutManagerReservasAceptar = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false);
                            adapterReservasAceptar = new ListaReservasAceptarAdapter(activity, activity, reservasAceptar, usuario, idComunidad);
                            misReservasAceptarRecyclerView.setLayoutManager(linearLayoutManagerReservasAceptar);
                            misReservasAceptarRecyclerView.setAdapter(adapterReservasAceptar);
                            adapterReservasAceptar.notifyDataSetChanged();*/

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

    private void guardarUsuariosAceptar(){
        try{
            for (int j = 0; j < respuestaUsuariosAceptar.length(); j++){
                JSONObject jsonUsuariosAceptar = respuestaUsuariosAceptar.getJSONObject(j);

                usuariosAceptar.add(new Usuario(jsonUsuariosAceptar.getString("IdUsuario"),
                        jsonUsuariosAceptar.getString("CorreoElectronico"),
                        jsonUsuariosAceptar.getString("Nombre"),
                        jsonUsuariosAceptar.getString("Apellidos"),
                        jsonUsuariosAceptar.getString("Direccion"),
                        jsonUsuariosAceptar.getString("FotoPerfil")));
            }

            if (respuestaUsuariosAceptar.length() == 0){
                findViewById(R.id.usuariosAceptarTextView).setVisibility(View.GONE);
            }
        }catch (Exception e){
            //no hace nada
        }
    }
}