package com.example.garajeando;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.regex.Pattern;

public class PruebaBBDD extends AppCompatActivity {

    EditText correoElectronicoEditText, contrasenaEditText, repetirContrasenaEditText, nombreEditText, apellidosEditText;
    Button registrarseButton;
    TextView avisoTextView;

    String correoElectronico, contrasena, repetirContrasena, nombre, apellidos;

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

        correoElectronicoEditText = (EditText) findViewById(R.id.correoElectronicoEditText);
        contrasenaEditText = (EditText) findViewById(R.id.contrasenaEditText);
        repetirContrasenaEditText = (EditText) findViewById(R.id.repetirContrasenaEditText);
        nombreEditText = (EditText) findViewById(R.id.nombreEditText);
        apellidosEditText = (EditText) findViewById(R.id.apellidosEditText);

        avisoTextView= (TextView) findViewById(R.id.avisoRegistrarseTextView);

        registrarseButton = (Button) findViewById(R.id.registrarseBoton);
        registrarseButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                crearUsuario();
            }
        });
    }

    private void crearUsuario() {
        correoElectronico = correoElectronicoEditText.getText().toString().trim();
        contrasena = contrasenaEditText.getText().toString().trim();
        nombre = nombreEditText.getText().toString().trim();
        apellidos = apellidosEditText.getText().toString().trim();

        if(validarCredenciales()){
            peticionCrearUsuario();
        }

    }

    private void peticionCrearUsuario(){
        StringRequest peticion = new StringRequest(Request.Method.POST,
                Constantes.URL_CREARUSUARIO,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String respuesta) {
                        avisoTextView.setVisibility(View.VISIBLE);

                        try {
                            JSONObject objetoJSON = new JSONObject(respuesta);
                            avisoTextView.setText(objetoJSON.getString("mensaje"));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        avisoTextView.setVisibility(View.VISIBLE);
                        avisoTextView.setText("ERROR");

                    }
                }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map <String,String> parametros = new HashMap<>();
                parametros.put("CorreoElectronico", correoElectronico);
                parametros.put("Contrasena", contrasena);
                parametros.put("Nombre", nombre);
                parametros.put("Apellidos", apellidos);

                return parametros;
            }
        };

        AdministradorPeticiones.getInstance(this).addToRequestQueue(peticion);
    }

    private Boolean validarCredenciales(){
        correoElectronico = correoElectronicoEditText.getText().toString().trim();
        contrasena = contrasenaEditText.getText().toString().trim();
        repetirContrasena = repetirContrasenaEditText.getText().toString().trim();

        Boolean camposValidos = true;

        if(!contrasena.isEmpty() && Pattern.compile("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-._¿¡]).{8,}$").matcher(contrasena).matches()){
            /*At least one upper case English letter, (?=.*?[A-Z])
            At least one lower case English letter, (?=.*?[a-z])
            At least one digit, (?=.*?[0-9])
            At least one special character, (?=.*?[#?!@$%^&*-])
            Minimum eight in length .{8,} (with the anchors)*/
            //contrasena correcta
            if(contrasena.equals(repetirContrasena)){
                //contrasenas coinciden
            }else{
                //no coinciden
                camposValidos = false;
                avisoTextView.setVisibility(View.VISIBLE);
                avisoTextView.setText("Contraseñas no coinciden entre sí.");
            }
        }else{
            //contrasena incorrecta
            camposValidos = false;
            avisoTextView.setVisibility(View.VISIBLE);
            avisoTextView.setText("La contraseña no posee el formato correcto.");
        }

        if(!correoElectronico.isEmpty() && Pattern.compile("[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", Pattern.CASE_INSENSITIVE).matcher(correoElectronico).matches()){
            //CORREO BIEN
        }else{
            camposValidos = false;
            avisoTextView.setVisibility(View.VISIBLE);
            avisoTextView.setText("La dirección de correo electrónico no posee el formato correcto.");
        }

        if(correoElectronico.isEmpty() || contrasena.isEmpty() || nombre.isEmpty() || apellidos.isEmpty()){
            camposValidos = false;
            avisoTextView.setVisibility(View.VISIBLE);
            avisoTextView.setText("Rellene los campos obligatorios.");
        }

        return camposValidos;
    }
}