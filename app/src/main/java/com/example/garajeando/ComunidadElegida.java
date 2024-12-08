package com.example.garajeando;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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

public class ComunidadElegida extends AppCompatActivity {

    private String usuario, idComunidad, nombreComunidad, codInvitacion, rolComunidad;

    private int numCoches, numCochesOtrasComunidades;

    private final ArrayList<Coche> coches = new ArrayList<Coche>();
    private final ArrayList<Coche> cochesOtrasComunidades = new ArrayList<Coche>();
    private final ArrayList<Oferta> ofertasFuturas = new ArrayList<Oferta>();
    private final ArrayList<Oferta> ofertasPasadas = new ArrayList<Oferta>();
    private final ArrayList<Reserva> reservasAceptar = new ArrayList<Reserva>();
    private final ArrayList<Reserva> reservasFuturas = new ArrayList<Reserva>();
    private final ArrayList<Reserva> reservasPasadas = new ArrayList<Reserva>();
    private final ArrayList<Reserva> reservasResenar = new ArrayList<Reserva>();

    private ListaCochesAdapter adapterCoches;
    private ListaOfertasAdapter adapterOfertasFuturas, adapterOfertasPasadas;
    private ListaReservasAdapter adapterReservasAceptar, adapterReservasFuturas, adapterReservasPasadas, adapterReservasResenar;

    private final Activity activity=this;
    private final Context context = this;

    private Toolbar miComunidadToolbar;
    private RecyclerView misCochesRecyclerView, misOfertasFuturasRecyclerView, misOfertasPasadasRecyclerView, misReservasAceptarRecyclerView, misReservasFuturasRecyclerView, misReservasPasadasRecyclerView, misReservasResenarRecyclerView;

    JSONArray respuestaCoches, respuestaCochesOtrasComunidades, respuestaOfertasFuturas, respuestaOfertasPasadas, respuestaReservasAceptar, respuestaReservasFuturas, respuestaReservasPasadas, respuestaReservasResenar;

    ListView l_coches;

    LinearLayoutManager linearLayoutManagerCoches, linearLayoutManagerOfertasFuturas, linearLayoutManagerOfertasPasadas, linearLayoutManagerReservasAceptar, linearLayoutManagerReservasFuturas, linearLayoutManagerReservasPasadas, linearLayoutManagerReservasResenar;

    String[] opciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Preferencias.aplicarTema(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_comunidad_elegida);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        usuario = getIntent().getExtras().getString("usuario");
        idComunidad = getIntent().getExtras().getString("idComunidad");
        nombreComunidad = getIntent().getExtras().getString("nombreComunidad");
        codInvitacion = getIntent().getExtras().getString("codInvitacion");
        rolComunidad = getIntent().getExtras().getString("rolComunidad");

        setSupportActionBar(findViewById(R.id.ComunidadElegidaToolbar));
        getSupportActionBar().setTitle(nombreComunidad);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        misCochesRecyclerView = findViewById(R.id.misCochesRecyclerView);
        misOfertasFuturasRecyclerView = findViewById(R.id.misOfertasFuturasRecyclerView);
        misOfertasPasadasRecyclerView = findViewById(R.id.misOfertasPasadasRecyclerView);
        misReservasAceptarRecyclerView = findViewById(R.id.misReservasPorAceptarRecyclerView);
        misReservasFuturasRecyclerView = findViewById(R.id.misReservasFuturasRecyclerView);
        misReservasPasadasRecyclerView = findViewById(R.id.misReservasPasadasRecyclerView);
        misReservasResenarRecyclerView = findViewById(R.id.misReservasPorResenarRecyclerView);

        limpiarArrayLists();
        obtenerInfoPrincipalUsuario();

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("usuario", usuario);
        outState.putString("idComunidad", idComunidad);
        outState.putString("nombreComunidad", nombreComunidad);
        outState.putString("codInvitacion", codInvitacion);
        outState.putString("rolComunidad", rolComunidad);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        usuario = savedInstanceState.getString("usuario");
        idComunidad = savedInstanceState.getString("idComunidad");
        nombreComunidad = savedInstanceState.getString("nombreComunidad");
        codInvitacion = savedInstanceState.getString("codInvitacion");
        rolComunidad = savedInstanceState.getString("rolComunidad");

        obtenerInfoPrincipalUsuario();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Adjust visibility based on conditions
        //menu.findItem(R.id.BuscarToolbarItem).setVisible(condition1);
        //menu.findItem(R.id.PerfilToobarItem).setVisible(condition2);
        //menu.findItem(R.id.PreferenciasToobarItem).setVisible(condition3);
        menu.findItem(R.id.AdministradorToobarItem).setVisible(rolComunidad.equals("Administrador"));
        //menu.findItem(R.id.CerrarSesionToobarItem).setVisible(CerrarSesionToobarItem);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.BuscarToolbarItem) {
            Intent intentBuscar = new Intent(this, BuscarOfertas.class);
            intentBuscar.putExtra("idComunidad", idComunidad);
            intentBuscar.putExtra("usuario", usuario);
            startActivity(intentBuscar);
            return true;
        } else if (itemId == R.id.PerfilToobarItem) {
            Intent intentPerfil = new Intent(ComunidadElegida.this, PerfilUsuario.class);
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
        } else if (itemId == R.id.AdministradorToobarItem) {
            Intent intentAdministrador = new Intent(ComunidadElegida.this, MenuAdministrador.class);
            intentAdministrador.putExtra("usuario", usuario);
            intentAdministrador.putExtra("idComunidad", idComunidad);
            startActivityForResult(intentAdministrador,1);
            return true;
        } else if (itemId == R.id.CerrarSesionToobarItem) {
            Intent intentCerrarSesion = new Intent(ComunidadElegida.this, IniciarSesion.class);
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

    private void limpiarArrayLists(){
        coches.clear();
        cochesOtrasComunidades.clear();
        ofertasFuturas.clear();
        ofertasPasadas.clear();
        reservasAceptar.clear();
        reservasFuturas.clear();
        reservasPasadas.clear();
        reservasResenar.clear();
    }

    private void guardarCochesEstaComunidad(){
        try{
            for (int i = 0; i < respuestaCoches.length(); i++) {
                JSONObject jsonCoches = respuestaCoches.getJSONObject(i);

                coches.add(new Coche(jsonCoches.getString("IdCoche"),
                        jsonCoches.getString("Propietario"),
                        jsonCoches.getString("NombrePropietario"),
                        jsonCoches.getString("ApellidosPropietario"),
                        jsonCoches.getString("Matricula"),
                        jsonCoches.getString("Marca"),
                        jsonCoches.getString("Modelo"),
                        jsonCoches.getString("Transmision"),
                        jsonCoches.getString("Combustible"),
                        jsonCoches.getString("Descripcion"),
                        Integer.parseInt(jsonCoches.getString("Plazas")),
                        Integer.parseInt(jsonCoches.getString("Puertas")),
                        Boolean.parseBoolean(jsonCoches.getString("AireAcondicionado")),
                        Boolean.parseBoolean(jsonCoches.getString("Bluetooth")),
                        Boolean.parseBoolean(jsonCoches.getString("GPS")),
                        jsonCoches.getString("FotoCochePrincipal")));
            }
        }catch (Exception e){
            //no hace nada
        }
    }

    private void guardarCochesOtrasComunidades(){
        try{
            for (int j = 0; j < respuestaCochesOtrasComunidades.length(); j++){
                JSONObject jsonCochesOtrasComunidades = respuestaCochesOtrasComunidades.getJSONObject(j);

                cochesOtrasComunidades.add(new Coche(jsonCochesOtrasComunidades.getString("IdCoche"),
                jsonCochesOtrasComunidades.getString("Propietario"),
                jsonCochesOtrasComunidades.getString("NombrePropietario"),
                jsonCochesOtrasComunidades.getString("ApellidosPropietario"),
                jsonCochesOtrasComunidades.getString("Matricula"),
                jsonCochesOtrasComunidades.getString("Marca"),
                jsonCochesOtrasComunidades.getString("Modelo"),
                jsonCochesOtrasComunidades.getString("Transmision"),
                jsonCochesOtrasComunidades.getString("Combustible"),
                jsonCochesOtrasComunidades.getString("Descripcion"),
                Integer.parseInt(jsonCochesOtrasComunidades.getString("Plazas")),
                Integer.parseInt(jsonCochesOtrasComunidades.getString("Puertas")),
                Boolean.parseBoolean(jsonCochesOtrasComunidades.getString("AireAcondicionado")),
                Boolean.parseBoolean(jsonCochesOtrasComunidades.getString("Bluetooth")),
                Boolean.parseBoolean(jsonCochesOtrasComunidades.getString("GPS")),
                jsonCochesOtrasComunidades.getString("FotoCochePrincipal")));
            }
        }catch (Exception e){
            //no hace nada
        }
    }

    private void guardarOfertasFuturas(){
        try{
            for (int j = 0; j < respuestaOfertasFuturas.length(); j++){
                JSONObject jsonOfertasFuturas = respuestaOfertasFuturas.getJSONObject(j);

                ofertasFuturas.add(new Oferta(jsonOfertasFuturas.getString("IdOferta"),
                        jsonOfertasFuturas.getString("IdCoche"),
                        jsonOfertasFuturas.getString("IdComunidad"),
                        jsonOfertasFuturas.getString("FechaHoraInicio"),
                        jsonOfertasFuturas.getString("FechaHoraFin"),
                        jsonOfertasFuturas.getString("FotoCoche"),
                        jsonOfertasFuturas.getString("Matricula"),
                        jsonOfertasFuturas.getString("Reservada")));
            }
        }catch (Exception e){
            //no hace nada
        }
    }

    private void guardarOfertasPasadas(){
        try{
            for (int j = 0; j < respuestaOfertasPasadas.length(); j++){
                JSONObject jsonOfertasPasadas = respuestaOfertasPasadas.getJSONObject(j);

                ofertasPasadas.add(new Oferta(jsonOfertasPasadas.getString("IdOferta"),
                        jsonOfertasPasadas.getString("IdCoche"),
                        jsonOfertasPasadas.getString("IdComunidad"),
                        jsonOfertasPasadas.getString("FechaHoraInicio"),
                        jsonOfertasPasadas.getString("FechaHoraFin"),
                        jsonOfertasPasadas.getString("FotoCoche"),
                        jsonOfertasPasadas.getString("Matricula"),
                        jsonOfertasPasadas.getString("Reservada")));
            }

            if (respuestaOfertasPasadas.length() == 0){
                findViewById(R.id.misOfertasPasadasTextView).setVisibility(View.GONE);
            }
        }catch (Exception e){
            //no hace nada
        }
    }

    private void guardarReservasAceptar(){
        try{
            for (int j = 0; j < respuestaReservasAceptar.length(); j++){
                JSONObject jsonReservasAceptar = respuestaReservasAceptar.getJSONObject(j);

                reservasAceptar.add(new Reserva(jsonReservasAceptar.getString("IdReserva"),
                        jsonReservasAceptar.getString("IdCoche"),
                        jsonReservasAceptar.getString("IdUsuario"),
                        jsonReservasAceptar.getString("IdComunidad"),
                        jsonReservasAceptar.getString("FechaHoraInicio"),
                        jsonReservasAceptar.getString("FechaHoraFin"),
                        jsonReservasAceptar.getString("FotoCoche"),
                        jsonReservasAceptar.getString("Matricula"),
                        jsonReservasAceptar.getString("Aprobada"),
                        jsonReservasAceptar.getString("Propietario"),
                        jsonReservasAceptar.getString("Nombre") + " " + jsonReservasAceptar.getString("Apellidos"),
                        jsonReservasAceptar.getString("PuntosUsuario"),
                        jsonReservasAceptar.getString("PuntosPropietario")));
            }

            if (respuestaReservasAceptar.length() == 0){
                findViewById(R.id.misReservasPorAceptarTextView).setVisibility(View.GONE);
            }
        }catch (Exception e){
            //no hace nada
        }
    }

    private void guardarReservasFuturas(){
        try{
            for (int j = 0; j < respuestaReservasFuturas.length(); j++){
                JSONObject jsonReservasFuturas = respuestaReservasFuturas.getJSONObject(j);

                reservasFuturas.add(new Reserva(jsonReservasFuturas.getString("IdReserva"),
                        jsonReservasFuturas.getString("IdCoche"),
                        jsonReservasFuturas.getString("IdUsuario"),
                        jsonReservasFuturas.getString("IdComunidad"),
                        jsonReservasFuturas.getString("FechaHoraInicio"),
                        jsonReservasFuturas.getString("FechaHoraFin"),
                        jsonReservasFuturas.getString("FotoCoche"),
                        jsonReservasFuturas.getString("Matricula"),
                        jsonReservasFuturas.getString("Aprobada"),
                        jsonReservasFuturas.getString("Propietario"),
                        jsonReservasFuturas.getString("Nombre") + " " + jsonReservasFuturas.getString("Apellidos"),
                        jsonReservasFuturas.getString("PuntosUsuario"),
                        jsonReservasFuturas.getString("PuntosPropietario")));
            }
        }catch (Exception e){
            //no hace nada
        }
    }

    private void guardarReservasPasadas(){
        try{
            for (int j = 0; j < respuestaReservasPasadas.length(); j++){
                JSONObject jsonReservasPasadas = respuestaReservasPasadas.getJSONObject(j);

                reservasPasadas.add(new Reserva(jsonReservasPasadas.getString("IdReserva"),
                        jsonReservasPasadas.getString("IdCoche"),
                        jsonReservasPasadas.getString("IdUsuario"),
                        jsonReservasPasadas.getString("IdComunidad"),
                        jsonReservasPasadas.getString("FechaHoraInicio"),
                        jsonReservasPasadas.getString("FechaHoraFin"),
                        jsonReservasPasadas.getString("FotoCoche"),
                        jsonReservasPasadas.getString("Matricula"),
                        jsonReservasPasadas.getString("Aprobada"),
                        jsonReservasPasadas.getString("Propietario"),
                        jsonReservasPasadas.getString("Nombre") + " " + jsonReservasPasadas.getString("Apellidos"),
                        jsonReservasPasadas.getString("PuntosUsuario"),
                        jsonReservasPasadas.getString("PuntosPropietario")));
            }
        }catch (Exception e){
            //no hace nada
        }
    }

    private void guardarReservasResenar(){
        try{
            for (int j = 0; j < respuestaReservasResenar.length(); j++){
                JSONObject jsonReservasResenar = respuestaReservasResenar.getJSONObject(j);

                reservasResenar.add(new Reserva(jsonReservasResenar.getString("IdReserva"),
                        jsonReservasResenar.getString("IdCoche"),
                        jsonReservasResenar.getString("IdUsuario"),
                        jsonReservasResenar.getString("IdComunidad"),
                        jsonReservasResenar.getString("FechaHoraInicio"),
                        jsonReservasResenar.getString("FechaHoraFin"),
                        jsonReservasResenar.getString("FotoCoche"),
                        jsonReservasResenar.getString("Matricula"),
                        jsonReservasResenar.getString("Aprobada"),
                        jsonReservasResenar.getString("Propietario"),
                        jsonReservasResenar.getString("Nombre") + " " + jsonReservasResenar.getString("Apellidos"),
                        jsonReservasResenar.getString("PuntosUsuario"),
                        jsonReservasResenar.getString("PuntosPropietario")));
            }

            if (respuestaReservasResenar.length() == 0){
                findViewById(R.id.misReservasPorResenarTextView).setVisibility(View.GONE);
            }
        }catch (Exception e){
            //no hace nada
        }
    }

    private void obtenerInfoPrincipalUsuario(){
        String idUsuarioEncoded = "";
        String idComunidadEncoded = "";
        try {
            idUsuarioEncoded = URLEncoder.encode(usuario, "UTF-8");
            idComunidadEncoded = URLEncoder.encode(idComunidad, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            //e.printStackTrace();
        }

        StringRequest peticion = new StringRequest(Request.Method.GET,
                Constantes.URL_OBTENERINFOPRINCIPALUSUARIO+"?IdUsuario="+idUsuarioEncoded+"&IdComunidad="+idComunidadEncoded,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String respuesta) {
                        try {
                            JSONObject objetoJSON = new JSONObject(respuesta);
                            numCoches = Integer.parseInt(String.valueOf(objetoJSON.getJSONArray("mensaje").length()));
                            numCochesOtrasComunidades = Integer.parseInt(String.valueOf(objetoJSON.getJSONArray("cochesOtrasComunidades").length()));

                            limpiarArrayLists();
                            respuestaCoches = objetoJSON.getJSONArray("mensaje");
                            respuestaCochesOtrasComunidades = objetoJSON.getJSONArray("cochesOtrasComunidades");
                            respuestaOfertasFuturas = objetoJSON.getJSONArray("ofertasFuturas");
                            respuestaOfertasPasadas = objetoJSON.getJSONArray("ofertasPasadas");
                            respuestaReservasAceptar = objetoJSON.getJSONArray("reservasPendientes");
                            respuestaReservasFuturas = objetoJSON.getJSONArray("reservasFuturas");
                            respuestaReservasPasadas = objetoJSON.getJSONArray("reservasPasadas");
                            respuestaReservasResenar = objetoJSON.getJSONArray("reservasResenar");
                            guardarCochesEstaComunidad();
                            guardarCochesOtrasComunidades();
                            guardarOfertasFuturas();
                            guardarOfertasPasadas();
                            guardarReservasAceptar();
                            guardarReservasFuturas();
                            guardarReservasPasadas();
                            guardarReservasResenar();

                            linearLayoutManagerCoches = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false);
                            adapterCoches = new ListaCochesAdapter(activity, activity, coches, usuario, idComunidad, numCoches, opciones);
                            misCochesRecyclerView.setLayoutManager(linearLayoutManagerCoches);
                            misCochesRecyclerView.setAdapter(adapterCoches);
                            adapterCoches.notifyDataSetChanged();

                            linearLayoutManagerOfertasFuturas = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false);
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
                            adapterReservasAceptar = new ListaReservasAdapter(activity, activity, reservasAceptar, usuario, idComunidad, "aceptar");
                            misReservasAceptarRecyclerView.setLayoutManager(linearLayoutManagerReservasAceptar);
                            misReservasAceptarRecyclerView.setAdapter(adapterReservasAceptar);
                            adapterReservasAceptar.notifyDataSetChanged();

                            linearLayoutManagerReservasFuturas = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false);
                            adapterReservasFuturas = new ListaReservasAdapter(activity, activity, reservasFuturas, usuario, idComunidad, "futuras");
                            misReservasFuturasRecyclerView.setLayoutManager(linearLayoutManagerReservasFuturas);
                            misReservasFuturasRecyclerView.setAdapter(adapterReservasFuturas);
                            adapterReservasFuturas.notifyDataSetChanged();

                            linearLayoutManagerReservasPasadas = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false);
                            adapterReservasPasadas = new ListaReservasAdapter(activity, activity, reservasPasadas, usuario, idComunidad, "pasadas");
                            misReservasPasadasRecyclerView.setLayoutManager(linearLayoutManagerReservasPasadas);
                            misReservasPasadasRecyclerView.setAdapter(adapterReservasPasadas);
                            adapterReservasPasadas.notifyDataSetChanged();

                            linearLayoutManagerReservasResenar = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false);
                            adapterReservasResenar = new ListaReservasAdapter(activity, activity, reservasResenar, usuario, idComunidad, "pasadas");
                            misReservasResenarRecyclerView.setLayoutManager(linearLayoutManagerReservasResenar);
                            misReservasResenarRecyclerView.setAdapter(adapterReservasResenar);
                            adapterReservasResenar.notifyDataSetChanged();

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

    public void dialogoAnadirCoche(){
        opciones = new String[numCochesOtrasComunidades+1];
        opciones[0] = "Añade un coche nuevo";

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (numCochesOtrasComunidades > 0){
            for (int i = 1; i < numCochesOtrasComunidades+1; i++) {
                opciones[i] = cochesOtrasComunidades.get(i-1).getMatricula();
            }

            builder.setTitle("Selecciona un coche ya existente o crea uno nuevo");
            builder.setItems(opciones, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if(i == 0){
                        //se pasan todos los datos para ver la pelicula
                        Intent intent = new Intent(context, ModificarCoche.class);
                        intent.putExtra("idComunidad", idComunidad);
                        intent.putExtra("usuario", usuario);
                        intent.putExtra("accion", "aniadir");

                        activity.startActivityForResult(intent, 3);
                    } else {
                        anadirCocheAComunidad(cochesOtrasComunidades.get(i-1).getIdCoche());
                        coches.add(cochesOtrasComunidades.get(i-1));
                        cochesOtrasComunidades.remove(i-1);
                        numCochesOtrasComunidades=numCochesOtrasComunidades-1;
                        adapterCoches.notifyDataSetChanged();
                    }
                }});

            Preferencias.setTemaAlertDialogConOpciones(builder, context);
        } else {
            //se pasan todos los datos para ver el coche
            Intent intent = new Intent(context, ModificarCoche.class);
            intent.putExtra("idComunidad", idComunidad);
            intent.putExtra("usuario", usuario);
            intent.putExtra("accion", "aniadir");

            activity.startActivityForResult(intent, 3);
        }
    }

    private void anadirCocheAComunidad(String idCocheAnadido){
        StringRequest peticion = new StringRequest(Request.Method.POST,
                Constantes.URL_ANADIRCOCHEACOMUNIDAD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String respuesta) {
                        try {
                            JSONObject objetoJSON = new JSONObject(respuesta);


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
                parametros.put("IdCoche", idCocheAnadido);
                parametros.put("IdComunidad", idComunidad);

                return parametros;
            }
        };

        peticion.setTag("peticion");
        AdministradorPeticiones.getInstance(this).addToRequestQueue(peticion);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == 3){
            obtenerInfoPrincipalUsuario();
        }
    }

    public void dialogoAnadirOferta(){
        opciones = new String[numCoches];

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (numCoches > 0){
            for (int i = 0; i < numCoches; i++) {
                opciones[i] = coches.get(i).getMatricula();
            }

            builder.setTitle("Selecciona un coche al que asociar la oferta");
            builder.setItems(opciones, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //se pasan todos los datos para asociar la oferta al coche
                    Intent intent = new Intent(context, CrearOferta.class);
                    intent.putExtra("idComunidad", idComunidad);
                    intent.putExtra("usuario", usuario);
                    intent.putExtra("idCoche", coches.get(i).getIdCoche());
                    intent.putExtra("accion", "aniadir");

                    activity.startActivityForResult(intent, 3);
                }});

            Preferencias.setTemaAlertDialogConOpciones(builder, context);
        } else {
            //
        }
    }
}