package com.example.garajeando;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
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

    String idUsuario;
    TextView pruebaTextView;
    Context context = this;
    int numComunidades;

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

        pruebaTextView = (TextView) findViewById(R.id.prueba);
        pruebaTextView.setText(String.valueOf(idUsuario));

        obtenerComunidades();
        numComunidades = Integer.parseInt(String.valueOf(pruebaTextView.getText()));

        if (numComunidades == 0){

        }
    }

    public void obtenerComunidades(){
        StringRequest peticion = new StringRequest(Request.Method.POST,
                Constantes.URL_OBTENERCOMUNIDADES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String respuesta) {
                        try {
                            JSONObject objetoJSON = new JSONObject(respuesta);
                            pruebaTextView.setText(String.valueOf(objetoJSON.getJSONArray("mensaje").length()));
                            AdministradorPeticiones.getInstance(context).cancelAll("peticion");
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pruebaTextView.setVisibility(View.VISIBLE);
                        pruebaTextView.setText("ERROR");

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
}