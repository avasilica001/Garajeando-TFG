package com.example.garajeando;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Principal extends AppCompatActivity {

    String idUsuario, codInvitacion, nombreComunidad, codInvitacionComunidadCreada;

    TextView avisoUnirseComunidadTextView, codigoComunidadCreadaTextView, avisoCodInvitacionTextView, sinComunidadesTextView;
    EditText codigoInvitacionEditText, nombreComunidadEditText;
    Button unirseComunidadButton, crearUnaComunidadButton, crearComunidadButton;

    Context context = this;

    int numComunidades = -1;
    Boolean numComunidadesObtenido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_principal);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.crearComunidad), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        idUsuario = getIntent().getExtras().getString("idUsuario");

        if(numComunidades == -1){
            obtenerComunidades();
        }

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

        crearUnaComunidadButton = (Button) findViewById(R.id.crearUnaComunidadButton);
        crearUnaComunidadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sinComunidadesTextView.setVisibility(View.GONE);
                codigoInvitacionEditText.setVisibility(View.GONE);
                unirseComunidadButton.setVisibility(View.GONE);
                crearUnaComunidadButton.setVisibility(View.GONE);
                nombreComunidadEditText.setVisibility(View.VISIBLE);
                crearComunidadButton.setVisibility(View.VISIBLE);
                avisoUnirseComunidadTextView.setVisibility(View.GONE);
            }
        });

        crearComunidadButton = (Button) findViewById(R.id.crearComunidadButton);
        crearComunidadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crearComunidad();
            }
        });

        if(numComunidades > 0){
            avisoCodInvitacionTextView.setVisibility(View.GONE);
            codigoComunidadCreadaTextView.setVisibility(View.GONE);
            nombreComunidadEditText.setVisibility(View.GONE);
            crearComunidadButton.setVisibility(View.GONE);
            crearUnaComunidadButton.setVisibility(View.GONE);
            sinComunidadesTextView.setVisibility(View.GONE);
            codigoInvitacionEditText.setVisibility(View.GONE);
            unirseComunidadButton.setVisibility(View.GONE);
            avisoUnirseComunidadTextView.setVisibility(View.GONE);
        }
        else{
            avisoCodInvitacionTextView.setVisibility(View.GONE);
            codigoComunidadCreadaTextView.setVisibility(View.GONE);
            sinComunidadesTextView.setVisibility(View.VISIBLE);
            codigoInvitacionEditText.setVisibility(View.VISIBLE);
            unirseComunidadButton.setVisibility(View.VISIBLE);
            crearUnaComunidadButton.setVisibility(View.VISIBLE);
            nombreComunidadEditText.setVisibility(View.GONE);
            crearComunidadButton.setVisibility(View.GONE);
            avisoUnirseComunidadTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("numComunidades", numComunidades);
        outState.putBoolean("avisoCodInvitacion", avisoCodInvitacionTextView.getVisibility() == View.VISIBLE);
        outState.putBoolean("codigoComunidadCreada", codigoComunidadCreadaTextView.getVisibility() == View.VISIBLE);
        outState.putBoolean("sinComunidades", sinComunidadesTextView.getVisibility() == View.VISIBLE);
        outState.putBoolean("codigoInvitacion", codigoInvitacionEditText.getVisibility() == View.VISIBLE);
        outState.putBoolean("unirseComunidad", unirseComunidadButton.getVisibility() == View.VISIBLE);
        outState.putBoolean("crearUnaComunidad", crearUnaComunidadButton.getVisibility() == View.VISIBLE);
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

        avisoUnirseComunidadTextView.setText(savedInstanceState.getString("avisoUnirseComunidadText"));
        codigoComunidadCreadaTextView.setText(savedInstanceState.getString("codigoComunidadCreadaText"));
        avisoCodInvitacionTextView.setText(savedInstanceState.getString("avisoCodInvitacionText"));
        sinComunidadesTextView.setText(savedInstanceState.getString("sinComunidadesText"));

        if(savedInstanceState.getBoolean("avisoCodInvitacion")){avisoCodInvitacionTextView.setVisibility(View.VISIBLE);}else{avisoCodInvitacionTextView.setVisibility(View.GONE);}
        if(savedInstanceState.getBoolean("codigoComunidadCreada")){codigoComunidadCreadaTextView.setVisibility(View.VISIBLE);}else{codigoComunidadCreadaTextView.setVisibility(View.GONE);}
        if(savedInstanceState.getBoolean("sinComunidades")){sinComunidadesTextView.setVisibility(View.VISIBLE);}else{sinComunidadesTextView.setVisibility(View.GONE);}
        if(savedInstanceState.getBoolean("codigoInvitacion")){codigoInvitacionEditText.setVisibility(View.VISIBLE);}else{codigoInvitacionEditText.setVisibility(View.GONE);}
        if(savedInstanceState.getBoolean("unirseComunidad")){unirseComunidadButton.setVisibility(View.VISIBLE);}else{unirseComunidadButton.setVisibility(View.GONE);}
        if(savedInstanceState.getBoolean("crearUnaComunidad")){crearUnaComunidadButton.setVisibility(View.VISIBLE);}else{crearUnaComunidadButton.setVisibility(View.GONE);}
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
}