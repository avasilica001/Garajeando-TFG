package com.example.garajeando;

import android.annotation.SuppressLint;
import android.location.GnssAntennaInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.animation.AnimatableView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PruebaBBDD extends AppCompatActivity {

    EditText idUsuarioEditText, ContrasenaEditText, NombreEditText, ApellidoEditText;
    Button RegistrarseButton;
    TextView AvisoTextView;

    String idUsuario, Contrasena, Nombre, Apellido;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_prueba_bbdd);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        idUsuarioEditText = (EditText) findViewById(R.id.IdUsuario);
        ContrasenaEditText = (EditText) findViewById(R.id.Contrasena);
        NombreEditText = (EditText) findViewById(R.id.Nombre);
        ApellidoEditText = (EditText) findViewById(R.id.Apellido);

        AvisoTextView= (TextView) findViewById(R.id.Aviso);

        RegistrarseButton = (Button) findViewById(R.id.Registrarse);
        RegistrarseButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                crearUsuario();
            }
        });
    }

    private void crearUsuario() {
        idUsuario = idUsuarioEditText.getText().toString().trim();
        Contrasena = ContrasenaEditText.getText().toString().trim();
        Nombre = NombreEditText.getText().toString().trim();
        Apellido = ApellidoEditText.getText().toString().trim();

        peticionCrearUsuario();

    }

    private void peticionCrearUsuario(){
        StringRequest peticion = new StringRequest(Request.Method.POST,
                Constantes.URL_CREARUSUARIO,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String respuesta) {
                        AvisoTextView.setVisibility(View.VISIBLE);

                        try {
                            JSONObject objetoJSON = new JSONObject(respuesta);
                            AvisoTextView.setText(objetoJSON.getString("mensaje"));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        AvisoTextView.setVisibility(View.VISIBLE);
                        AvisoTextView.setText("ERROR");

                    }
                }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map <String,String> parametros = new HashMap<>();
                parametros.put("IdUsuario", idUsuario);
                parametros.put("Contrasena", Contrasena);
                parametros.put("Nombre", Nombre);
                parametros.put("Apellido", Apellido);

                return parametros;
            }
        };

        AdministradorPeticiones.getInstance(this).addToRequestQueue(peticion);
    }
}