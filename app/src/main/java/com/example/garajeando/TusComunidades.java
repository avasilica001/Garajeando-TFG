package com.example.garajeando;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import java.util.ArrayList;

public class TusComunidades extends AppCompatActivity {

    private String idUsuario, codInvitacion, nombreComunidad, codInvitacionComunidadCreada;

    private TextView avisoUnirseComunidadTextView, codigoComunidadCreadaTextView, avisoCodInvitacionTextView, sinComunidadesTextView;
    private EditText codigoInvitacionEditText, nombreComunidadEditText;
    private Button unirseComunidadButton, crearComunidadButton;
    private ListView comunidadesListView;

    private final Activity activity=this;
    private Context context = this;

    int numComunidades = -1;
    JSONArray respuestaComunidades;

    //arraylist con las columnas de la tabla comunidades
    private ArrayList<String> p_idComunidad=new ArrayList<>();
    private ArrayList<String> p_nombreComunidad=new ArrayList<>();
    private ArrayList<String> p_codInvitacionComunidad=new ArrayList<>();
    private ArrayList<String> p_rolComunidad=new ArrayList<>();

    private ListaComunidadesAdapter adapterC;

    private Boolean comunidadCreada = false;

    ListView l;

    Bundle savedIS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tuscomunidades);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.crearComunidad), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        idUsuario = getIntent().getExtras().getString("idUsuario");

        savedIS = savedInstanceState;
        limpiarArrayLists();
        obtenerComunidades();

        comunidadesListView = (ListView) findViewById(R.id.listaComunidadesListViewC);

        avisoUnirseComunidadTextView = (TextView) findViewById(R.id.avisoUnirseComunidadTextView);

        sinComunidadesTextView = (TextView) findViewById(R.id.SinComunidadesTextView);
        avisoCodInvitacionTextView = (TextView) findViewById(R.id.avisoCodInvitacionTextView);

        codigoComunidadCreadaTextView = (TextView) findViewById(R.id.codigoInvitacionComunidadCreadaTextView);


        codigoInvitacionEditText = (EditText) findViewById(R.id.invitacionComunidadEditText);
        nombreComunidadEditText = (EditText) findViewById(R.id.nombreComunidadEditText);

        unirseComunidadButton = (Button) findViewById(R.id.unirseComunidadButton);
        unirseComunidadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unirseAComunidad();
            }
        });

        crearComunidadButton = (Button) findViewById(R.id.crearComunidadButton);
        crearComunidadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crearComunidad();
                codigoComunidadCreadaTextView.setVisibility(View.VISIBLE);
                avisoCodInvitacionTextView.setVisibility(View.VISIBLE);
                sinComunidadesTextView.setVisibility(View.VISIBLE);
                codigoInvitacionEditText.setVisibility(View.VISIBLE);
                unirseComunidadButton.setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("numComunidades", numComunidades);
        outState.putBoolean("comunidadCreada", comunidadCreada);
        outState.putBoolean("avisoCodInvitacion", avisoCodInvitacionTextView.getVisibility() == View.VISIBLE);
        outState.putBoolean("codigoComunidadCreada", codigoComunidadCreadaTextView.getVisibility() == View.VISIBLE);
        outState.putBoolean("sinComunidades", sinComunidadesTextView.getVisibility() == View.VISIBLE);
        outState.putBoolean("codigoInvitacion", codigoInvitacionEditText.getVisibility() == View.VISIBLE);
        outState.putBoolean("unirseComunidad", unirseComunidadButton.getVisibility() == View.VISIBLE);
        outState.putBoolean("nombreComunidad", nombreComunidadEditText.getVisibility() == View.VISIBLE);
        outState.putBoolean("crearComunidad", crearComunidadButton.getVisibility() == View.VISIBLE);
        outState.putBoolean("avisoUnirseComunidad", avisoUnirseComunidadTextView.getVisibility() == View.VISIBLE);
        outState.putString("avisoUnirseComunidadText", avisoUnirseComunidadTextView.getText().toString().trim());
        outState.putString("codigoComunidadCreadaText", codigoComunidadCreadaTextView.getText().toString().trim());
        outState.putString("avisoCodInvitacionText", avisoCodInvitacionTextView.getText().toString().trim());
        outState.putString("sinComunidadesText", sinComunidadesTextView.getText().toString().trim());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        numComunidades = savedInstanceState.getInt("numComunidades");
        comunidadCreada = savedInstanceState.getBoolean("comunidadCreada");

        avisoUnirseComunidadTextView.setText(savedInstanceState.getString("avisoUnirseComunidadText"));
        codigoComunidadCreadaTextView.setText(savedInstanceState.getString("codigoComunidadCreadaText"));
        avisoCodInvitacionTextView.setText(savedInstanceState.getString("avisoCodInvitacionText"));
        sinComunidadesTextView.setText(savedInstanceState.getString("sinComunidadesText"));

        if(savedInstanceState.getBoolean("avisoCodInvitacion")){avisoCodInvitacionTextView.setVisibility(View.VISIBLE);}else{avisoCodInvitacionTextView.setVisibility(View.GONE);}
        if(savedInstanceState.getBoolean("codigoComunidadCreada")){codigoComunidadCreadaTextView.setVisibility(View.VISIBLE);}else{codigoComunidadCreadaTextView.setVisibility(View.GONE);}
        if(savedInstanceState.getBoolean("sinComunidades")){sinComunidadesTextView.setVisibility(View.VISIBLE);}else{sinComunidadesTextView.setVisibility(View.GONE);}
        if(savedInstanceState.getBoolean("codigoInvitacion")){codigoInvitacionEditText.setVisibility(View.VISIBLE);}else{codigoInvitacionEditText.setVisibility(View.GONE);}
        if(savedInstanceState.getBoolean("unirseComunidad")){unirseComunidadButton.setVisibility(View.VISIBLE);}else{unirseComunidadButton.setVisibility(View.GONE);}
        if(savedInstanceState.getBoolean("nombreComunidad")){nombreComunidadEditText.setVisibility(View.VISIBLE);}else{nombreComunidadEditText.setVisibility(View.GONE);}
        if(savedInstanceState.getBoolean("crearComunidad")){crearComunidadButton.setVisibility(View.VISIBLE);}else{crearComunidadButton.setVisibility(View.GONE);}
        if(savedInstanceState.getBoolean("avisoCodInvitacion")){avisoCodInvitacionTextView.setVisibility(View.VISIBLE);}else{avisoCodInvitacionTextView.setVisibility(View.GONE);}
        if(savedInstanceState.getBoolean("avisoUnirseComunidad")){avisoUnirseComunidadTextView.setVisibility(View.VISIBLE);}else{avisoUnirseComunidadTextView.setVisibility(View.GONE);}
    }

    public void obtenerComunidades(){
        StringRequest peticion = new StringRequest(Request.Method.POST,
                Constantes.URL_OBTENERCOMUNIDADES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String respuesta) {
                        try {
                            JSONObject objetoJSON = new JSONObject(respuesta);
                            numComunidades = Integer.parseInt(String.valueOf(objetoJSON.getJSONArray("mensaje").length()));

                            float factor = context.getResources().getDisplayMetrics().density;

                            if(numComunidades <6){
                                comunidadesListView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) ((55 + (numComunidades-1)*65) * factor)));
                            }
                            else{
                                comunidadesListView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) ((55 + (4.5)*65) * factor)));
                            }

                            limpiarArrayLists();
                            respuestaComunidades = objetoJSON.getJSONArray("mensaje");
                            guardarComunidades();

                            //se crea el adaptar propio
                            adapterC = new ListaComunidadesAdapter(activity, activity, p_idComunidad, p_nombreComunidad, p_codInvitacionComunidad, p_rolComunidad, idUsuario);
                            l = (ListView) findViewById(R.id.listaComunidadesListViewC);
                            l.setAdapter(adapterC);

                            //cuando se modifican datos notificar para que actualice
                            adapterC.notifyDataSetChanged();

                                if (comunidadCreada) {
                                    codigoComunidadCreadaTextView.setVisibility(View.VISIBLE);
                                    avisoCodInvitacionTextView.setVisibility(View.VISIBLE);
                                } else{
                                    codigoComunidadCreadaTextView.setVisibility(View.GONE);
                                    avisoCodInvitacionTextView.setVisibility(View.GONE);
                                }

                            AdministradorPeticiones.getInstance(context).cancelAll("peticion");
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
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
                parametros.put("IdUsuario", idUsuario);

                return parametros;
            }
        };

        peticion.setTag("peticion");
        AdministradorPeticiones.getInstance(this).addToRequestQueue(peticion);
    }

    public void unirseAComunidad() {
        codInvitacion = codigoInvitacionEditText.getText().toString().trim();
        if (!String.valueOf(codInvitacion).isEmpty()) {
            StringRequest peticion = new StringRequest(Request.Method.POST,
                    Constantes.URL_UNIRSEACOMUNIDAD,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String respuesta) {
                            try {
                                JSONObject objetoJSON = new JSONObject(respuesta);
                                avisoUnirseComunidadTextView.setText(objetoJSON.getString("mensaje"));
                                AdministradorPeticiones.getInstance(context).cancelAll("peticion");
                            } catch (JSONException e) {
                                //
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
                    parametros.put("IdUsuario", idUsuario);
                    parametros.put("CodInvitacion", codInvitacion);

                    return parametros;
                }
            };

            peticion.setTag("peticion");
            AdministradorPeticiones.getInstance(this).addToRequestQueue(peticion);
        }else{
            avisoUnirseComunidadTextView.setVisibility(View.VISIBLE);
            avisoUnirseComunidadTextView.setText("Es necesario introducir un código para poder unirse a una comunidad.");
        }
    }

    public void crearComunidad(){
        nombreComunidad = nombreComunidadEditText.getText().toString().trim();
        if (!String.valueOf(nombreComunidad).isEmpty()) {
            StringRequest peticion = new StringRequest(Request.Method.POST,
                    Constantes.URL_CREARCOMUNIDAD,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String respuesta) {
                            try {
                                JSONObject objetoJSON = new JSONObject(respuesta);
                                if (String.valueOf(objetoJSON.getJSONObject("mensaje")).startsWith("{")){
                                    codInvitacionComunidadCreada = String.valueOf(objetoJSON.getJSONObject("mensaje").getString("CodInvitacion"));
                                }
                                avisoCodInvitacionTextView.setText("Invita a más personas a la comunidad utilizando el siguiente código:");
                                codigoComunidadCreadaTextView.setText(String.valueOf(codInvitacionComunidadCreada));
                                AdministradorPeticiones.getInstance(context).cancelAll("peticion");
                                comunidadCreada = true;
                                obtenerComunidades();
                            } catch (JSONException e) {
                                avisoCodInvitacionTextView.setText("Este nombre ya está en uso.");
                                codigoComunidadCreadaTextView.setVisibility(View.GONE);
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
                    parametros.put("IdUsuario", idUsuario);
                    parametros.put("NombreComunidad", nombreComunidad);

                    return parametros;
                }
            };

            peticion.setTag("peticion");
            AdministradorPeticiones.getInstance(this).addToRequestQueue(peticion);

            codigoComunidadCreadaTextView.setVisibility(View.VISIBLE);
            avisoCodInvitacionTextView.setVisibility(View.VISIBLE);


        }else{
            avisoCodInvitacionTextView.setVisibility(View.VISIBLE);
            codigoComunidadCreadaTextView.setVisibility(View.GONE);
            avisoCodInvitacionTextView.setText("Es necesario introducir un nombre para poder crear una comunidad.");
        }
    }

    private void guardarComunidades(){
        try{
            for (int i = 0; i < respuestaComunidades.length(); i++)
            {
                JSONObject json = respuestaComunidades.getJSONObject(i);
                //se obtienen los datos del json y se setean para poder verse
                p_idComunidad.add(json.getString("IdComunidad"));
                p_nombreComunidad.add(json.getString("Nombre"));
                p_codInvitacionComunidad.add(json.getString("CodInvitacion"));
                p_rolComunidad.add(json.getString("Rol"));
            }
        }catch (Exception e){
            //no hace nada
        }
    }

    //vaciar elementos de los arraylists
    public void limpiarArrayLists(){
        //vaciar todos los elementos del array
        p_idComunidad.clear();
        p_nombreComunidad.clear();
        p_codInvitacionComunidad.clear();
        p_rolComunidad.clear();
    }
}