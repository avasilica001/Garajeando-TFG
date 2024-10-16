package com.example.garajeando;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

    private ListaCochesAdapter adapterCoches;
    private ListaOfertasAdapter adapterOfertasFuturas, adapterOfertasPasadas;

    private final Activity activity=this;
    private final Context context = this;

    private Toolbar miComunidadToolbar;
    private RecyclerView misCochesRecyclerView, misOfertasFuturasRecyclerView, misOfertasPasadasRecyclerView;

    JSONArray respuestaCoches, respuestaCochesOtrasComunidades, respuestaOfertasFuturas, respuestaOfertasPasadas;

    ListView l_coches;

    LinearLayoutManager linearLayoutManagerCoches, linearLayoutManagerOfertasFuturas, linearLayoutManagerOfertasPasadas;

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

    private void limpiarArrayLists(){
        coches.clear();
        cochesOtrasComunidades.clear();
        ofertasFuturas.clear();
        ofertasPasadas.clear();
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
                        jsonOfertasFuturas.getString("Matricula")));
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
                        jsonOfertasPasadas.getString("Matricula")));
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
                            guardarCochesEstaComunidad();
                            guardarCochesOtrasComunidades();
                            guardarOfertasFuturas();
                            guardarOfertasPasadas();

                            linearLayoutManagerCoches = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false);
                            adapterCoches = new ListaCochesAdapter(activity, activity, coches, usuario, idComunidad, numCoches, opciones);
                            misCochesRecyclerView.setLayoutManager(linearLayoutManagerCoches);
                            misCochesRecyclerView.setAdapter(adapterCoches);
                            adapterCoches.notifyDataSetChanged();

                            linearLayoutManagerOfertasFuturas = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false);
                            adapterOfertasFuturas = new ListaOfertasAdapter(activity, activity, ofertasFuturas, usuario, idComunidad);
                            misOfertasFuturasRecyclerView.setLayoutManager(linearLayoutManagerOfertasFuturas);
                            misOfertasFuturasRecyclerView.setAdapter(adapterOfertasFuturas);
                            adapterOfertasFuturas.notifyDataSetChanged();

                            linearLayoutManagerOfertasPasadas = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false);
                            adapterOfertasPasadas = new ListaOfertasAdapter(activity, activity, ofertasPasadas, usuario, idComunidad);
                            misOfertasPasadasRecyclerView.setLayoutManager(linearLayoutManagerOfertasPasadas);
                            misOfertasPasadasRecyclerView.setAdapter(adapterOfertasPasadas);
                            adapterOfertasPasadas.notifyDataSetChanged();

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
}