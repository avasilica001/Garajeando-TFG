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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.widget.Toast;

import java.util.ArrayList;

public class TusComunidades extends AppCompatActivity {

    private String idUsuario, codInvitacion, nombreComunidad, codInvitacionComunidadCreada;

    private TextView avisoUnirseComunidadTextView, codigoComunidadCreadaTextView, avisoCodInvitacionTextView, sinComunidadesTextView;
    private EditText codigoInvitacionEditText, nombreComunidadEditText;
    private Button unirseComunidadButton, crearComunidadButton;
    private ListView comunidadesListView;

    private final Activity activity=this;
    private final Context context = this;

    int numComunidades = -1;
    JSONArray respuestaComunidades;

    private final ArrayList<Comunidad> comunidades=new ArrayList<Comunidad>();

    private ListaComunidadesAdapter adapterC;

    private Boolean comunidadCreada = false;

    ListView l;

    Bundle savedIS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Preferencias.aplicarTema(this);
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

        setSupportActionBar(findViewById(R.id.tusComunidadesToolabr));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        comunidadesListView = findViewById(R.id.listaComunidadesListViewC);

        avisoUnirseComunidadTextView = findViewById(R.id.avisoUnirseComunidadTextView);

        sinComunidadesTextView = findViewById(R.id.SinComunidadesTextView);
        avisoCodInvitacionTextView = findViewById(R.id.avisoCodInvitacionTextView);

        codigoComunidadCreadaTextView = findViewById(R.id.codigoInvitacionComunidadCreadaTextView);


        codigoInvitacionEditText = findViewById(R.id.invitacionComunidadEditText);
        nombreComunidadEditText = findViewById(R.id.nombreComunidadEditText);

        unirseComunidadButton = findViewById(R.id.unirseComunidadButton);
        unirseComunidadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unirseAComunidad();
            }
        });

        crearComunidadButton = findViewById(R.id.crearComunidadButton);
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

        outState.putString("usuario", idUsuario);
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

        idUsuario = savedInstanceState.getString("usuario");
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
        menu.findItem(R.id.TemaToobarItem).setVisible(true);
        menu.findItem(R.id.AdministradorToobarItem).setVisible(false);
        menu.findItem(R.id.CerrarSesionToobarItem).setVisible(true);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.PerfilToobarItem) {
            Intent intentPerfil = new Intent(TusComunidades.this, PerfilUsuario.class);
            intentPerfil.putExtra("usuario", idUsuario);
            intentPerfil.putExtra("idUsuarioPerfil", idUsuario);
            startActivityForResult(intentPerfil,1);
            return true;
        } else if (itemId == R.id.TemaToobarItem) {
            boolean esOscuro = Preferencias.esTemaOscuro(this);
            Preferencias.setTemaOscuro(this, !esOscuro);
            Intent intent = getIntent();
            finish();
            startActivity(intent);
            return true;
        } else if (itemId == R.id.CerrarSesionToobarItem) {
            Intent intentCerrarSesion = new Intent(TusComunidades.this, IniciarSesion.class);
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


    public void obtenerComunidades(){
        String idUsuarioEncoded = "";
        try {
            idUsuarioEncoded = URLEncoder.encode(idUsuario, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            //e.printStackTrace();
        }

        StringRequest peticion = new StringRequest(Request.Method.GET,
                Constantes.URL_OBTENERCOMUNIDADES+"?IdUsuario="+idUsuarioEncoded,
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
                            adapterC = new ListaComunidadesAdapter(activity, activity, comunidades, idUsuario);
                            l = findViewById(R.id.listaComunidadesListViewC);
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

    public void unirseAComunidad() {
        codInvitacion = codigoInvitacionEditText.getText().toString().trim();
        if (!codInvitacion.isEmpty()) {
            StringRequest peticion = new StringRequest(Request.Method.POST,
                    Constantes.URL_UNIRSEACOMUNIDAD,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String respuesta) {
                            try {
                                JSONObject objetoJSON = new JSONObject(respuesta);
                                avisoUnirseComunidadTextView.setText(objetoJSON.getString("mensaje"));

                                if(objetoJSON.getString("error").equals("false")){
                                    Toast.makeText(TusComunidades.this, "¡Ahora solo queda esperar a que un administrador acepte tu solicitud!", Toast.LENGTH_LONG).show();
                                }
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
        if (!nombreComunidad.isEmpty()) {
            StringRequest peticion = new StringRequest(Request.Method.POST,
                    Constantes.URL_CREARCOMUNIDAD,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String respuesta) {
                            try {
                                JSONObject objetoJSON = new JSONObject(respuesta);
                                if (String.valueOf(objetoJSON.getJSONObject("mensaje")).startsWith("{")){
                                    codInvitacionComunidadCreada = objetoJSON.getJSONObject("mensaje").getString("CodInvitacion");
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
                comunidades.add(new Comunidad(json.getString("IdComunidad"),json.getString("Nombre"),json.getString("CodInvitacion"),json.getString("Rol")));
            }
        }catch (Exception e){
            //no hace nada
        }
    }

    //vaciar elementos de los arraylists
    public void limpiarArrayLists(){
        //vaciar todos los elementos del array
        comunidades.clear();
    }
}