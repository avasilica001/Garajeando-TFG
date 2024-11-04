package com.example.garajeando;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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

    private ListaCochesAdapter adapterCoches;
    private ListaOfertasAdapter adapterOfertasFuturas, adapterOfertasPasadas;
    private ListaReservasAceptarAdapter adapterReservasAceptar;

    private final Activity activity=this;
    private final Context context = this;

    private Toolbar miComunidadToolbar;
    private RecyclerView misCochesRecyclerView, misOfertasFuturasRecyclerView, misOfertasPasadasRecyclerView, misReservasAceptarRecyclerView;

    JSONArray respuestaCoches, respuestaCochesOtrasComunidades, respuestaOfertasFuturas, respuestaOfertasPasadas, respuestaReservasAceptar;

    ListView l_coches;

    LinearLayoutManager linearLayoutManagerCoches, linearLayoutManagerOfertasFuturas, linearLayoutManagerOfertasPasadas, linearLayoutManagerReservasAceptar;

    String[] opciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        misCochesRecyclerView = findViewById(R.id.misCochesRecyclerView);
        misOfertasFuturasRecyclerView = findViewById(R.id.misOfertasFuturasRecyclerView);
        misOfertasPasadasRecyclerView = findViewById(R.id.misOfertasPasadasRecyclerView);
        misReservasAceptarRecyclerView = findViewById(R.id.misReservasPorAceptarRecyclerView);

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
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
            //Intent intentTwo = new Intent(this, ActivityTwo.class);
            //startActivity(intentTwo);
            return true;
        } else if (itemId == R.id.PreferenciasToobarItem) {
            //Intent intentThree = new Intent(this, ActivityThree.class);
            //startActivity(intentThree);
            return true;
        } else if (itemId == R.id.AdministradorToobarItem) {
            //Intent intentThree = new Intent(this, ActivityThree.class);
            //startActivity(intentThree);
            return true;
        } else if (itemId == R.id.CerrarSesionToobarItem) {
            //Intent intentThree = new Intent(this, ActivityThree.class);
            //startActivity(intentThree);
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
                        jsonReservasAceptar.getString("Nombre") + " " + jsonReservasAceptar.getString("Apellidos")));
            }

            if (respuestaReservasAceptar.length() == 0){
                findViewById(R.id.misReservasPorAceptarTextView).setVisibility(View.GONE);
            }
        }catch (Exception e){
            //no hace nada
        }
    }

    private void obtenerInfoPrincipalUsuario(){
        StringRequest peticion = new StringRequest(Request.Method.POST,
                Constantes.URL_OBTENERINFOPRINCIPALUSUARIO,
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
                            guardarCochesEstaComunidad();
                            guardarCochesOtrasComunidades();
                            guardarOfertasFuturas();
                            guardarOfertasPasadas();
                            guardarReservasAceptar();

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
                            adapterReservasAceptar = new ListaReservasAceptarAdapter(activity, activity, reservasAceptar, usuario, idComunidad);
                            misReservasAceptarRecyclerView.setLayoutManager(linearLayoutManagerReservasAceptar);
                            misReservasAceptarRecyclerView.setAdapter(adapterReservasAceptar);
                            adapterReservasAceptar.notifyDataSetChanged();

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
                parametros.put("IdComunidad", idComunidad);

                return parametros;
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
            builder.create().show();
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
            builder.create().show();
        } else {
            //
        }
    }
}