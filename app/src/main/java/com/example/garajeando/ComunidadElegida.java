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

    private ArrayList<Coche> coches = new ArrayList<Coche>();
    private ArrayList<Coche> cochesOtrasComunidades = new ArrayList<Coche>();

    private ListaCochesAdapter adapterCo;

    private final Activity activity=this;
    private Context context = this;

    private Toolbar miComunidadToolbar;
    private RecyclerView misCochesRecyclerView;

    JSONArray respuestaCoches, respuestaCochesOtrasComunidades;

    ListView l_coches;

    LinearLayoutManager linearLayoutManagerCoches;

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

        misCochesRecyclerView = (RecyclerView) findViewById(R.id.misCochesRecyclerView);

        limpiarArrayListsCoches();
        obtenerCoches();

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

    private void limpiarArrayListsCoches(){
        coches.clear();
        cochesOtrasComunidades.clear();
    }

    private void guardarCoches(){
        try{
            for (int i = 0; i < respuestaCoches.length(); i++)
            {
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

            for (int i = 0; i < respuestaCochesOtrasComunidades.length(); i++)
            {
                JSONObject jsonCoches = respuestaCochesOtrasComunidades.getJSONObject(i);

                cochesOtrasComunidades.add(new Coche(jsonCoches.getString("IdCoche"),
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

    private void obtenerCoches(){
        StringRequest peticion = new StringRequest(Request.Method.POST,
                Constantes.URL_OBTENERCOCHES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String respuesta) {
                        try {
                            JSONObject objetoJSON = new JSONObject(respuesta);
                            numCoches = Integer.parseInt(String.valueOf(objetoJSON.getJSONArray("mensaje").length()));
                            numCochesOtrasComunidades = Integer.parseInt(String.valueOf(objetoJSON.getJSONArray("cochesOtrasComunidades").length()));

                            limpiarArrayListsCoches();
                            respuestaCoches = objetoJSON.getJSONArray("mensaje");
                            respuestaCochesOtrasComunidades = objetoJSON.getJSONArray("cochesOtrasComunidades");;
                            guardarCoches();


                            linearLayoutManagerCoches = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false);
                            adapterCo = new ListaCochesAdapter(activity, activity, coches, usuario, idComunidad, numCoches, opciones);
                            misCochesRecyclerView.setLayoutManager(linearLayoutManagerCoches);
                            misCochesRecyclerView.setAdapter(adapterCo);
                            adapterCo.notifyDataSetChanged();

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
                        adapterCo.notifyDataSetChanged();
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
            obtenerCoches();
        }
    }
}