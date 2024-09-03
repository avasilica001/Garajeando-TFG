package com.example.garajeando;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
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
import java.util.Objects;
import java.util.regex.Pattern;

public class Registrarse extends AppCompatActivity {

    private EditText correoElectronicoEditText, contrasenaEditText, repetirContrasenaEditText, nombreEditText, apellidosEditText;
    private Button registrarseButton;
    private TextView avisoTextView;
    Toolbar barraSuperiorRegistrarseToolbar;

    private String correoElectronico, contrasena, repetirContrasena, nombre, apellidos, aviso, contrasenaEncriptada;

    public static Boolean contrasenaVisible;


    @SuppressLint({"MissingInflatedId", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registrarse);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.RegistroLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Setear barra superior
        setSupportActionBar(findViewById(R.id.BarraSuperiorRegistrarseToolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        correoElectronicoEditText = (EditText) findViewById(R.id.correoElectronicoEditTextR);
        contrasenaEditText = (EditText) findViewById(R.id.contrasenaEditTextR);
        contrasenaVisible = false;
        repetirContrasenaEditText = (EditText) findViewById(R.id.repetirContrasenaEditText);
        nombreEditText = (EditText) findViewById(R.id.nombreEditText);
        apellidosEditText = (EditText) findViewById(R.id.apellidosEditText);

        avisoTextView = (TextView) findViewById(R.id.avisoTextViewR);

        registrarseButton = (Button) findViewById(R.id.registrarseBoton);
        registrarseButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                crearUsuario();
                finish();
            }
        });

        contrasenaEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= contrasenaEditText.getRight() - contrasenaEditText.getCompoundDrawables()[2].getBounds().width()) {
                        int seleccion = contrasenaEditText.getSelectionEnd();
                        if (contrasenaVisible) {
                            contrasenaEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ojo_cerrado, 0);
                            contrasenaEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            contrasenaVisible = false;
                        } else {
                            contrasenaEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ojo_abierto, 0);
                            contrasenaEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            contrasenaVisible = true;
                        }
                        contrasenaEditText.setSelection(seleccion);
                        return true;
                    }
                }

                return false;
            }
        });

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        aviso = avisoTextView.getText().toString().trim();
        outState.putString("aviso", aviso);
        outState.putBoolean("avisoVisible",avisoTextView.getVisibility() == View.VISIBLE);
        outState.putBoolean("contrasenaVisible", contrasenaVisible);
        contrasenaEditText = (EditText) findViewById(R.id.contrasenaEditTextR);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        aviso = savedInstanceState.getString("aviso");
        avisoTextView.setText(String.valueOf(aviso));
        if(savedInstanceState.getBoolean("avisoVisible")){avisoTextView.setVisibility(View.VISIBLE);}

        contrasenaVisible = savedInstanceState.getBoolean("contrasenaVisible");
        int seleccion = contrasenaEditText.getSelectionEnd();
        if (contrasenaVisible) {
            contrasenaEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ojo_abierto, 0);
            contrasenaEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            contrasenaVisible = true;
        } else {
            contrasenaEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ojo_cerrado, 0);
            contrasenaEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            contrasenaVisible = false;
        }
        contrasenaEditText.setSelection(seleccion);
        contrasenaEditText = (EditText) findViewById(R.id.contrasenaEditTextR);
    }

    private void crearUsuario(){
        correoElectronico = correoElectronicoEditText.getText().toString().trim();
        contrasena = contrasenaEditText.getText().toString().trim();
        nombre = nombreEditText.getText().toString().trim();
        apellidos = apellidosEditText.getText().toString().trim();

        if (validarCredencialesR()) {
            try {
                contrasenaEncriptada = Encriptador.encrypt(contrasena);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            peticionCrearUsuario();
        }
    }

    private void peticionCrearUsuario() {
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
                }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("CorreoElectronico", correoElectronico);
                parametros.put("Contrasena", contrasenaEncriptada);
                parametros.put("Nombre", nombre);
                parametros.put("Apellidos", apellidos);

                return parametros;
            }
        };

        AdministradorPeticiones.getInstance(this).addToRequestQueue(peticion);
    }

    private Boolean validarCredencialesR() {
        correoElectronico = correoElectronicoEditText.getText().toString().trim();
        contrasena = contrasenaEditText.getText().toString().trim();
        repetirContrasena = repetirContrasenaEditText.getText().toString().trim();

        Boolean camposValidos = true;

        if (!contrasena.isEmpty() && Pattern.compile("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-._¿¡]).{8,}$").matcher(contrasena).matches()) {
            /*At least one upper case English letter, (?=.*?[A-Z])
            At least one lower case English letter, (?=.*?[a-z])
            At least one digit, (?=.*?[0-9])
            At least one special character, (?=.*?[#?!@$%^&*-])
            Minimum eight in length .{8,} (with the anchors)*/
            //contrasena correcta
            if (contrasena.equals(repetirContrasena)) {
                //contrasenas coinciden
            } else {
                //no coinciden
                camposValidos = false;
                avisoTextView.setVisibility(View.VISIBLE);
                avisoTextView.setText("Contraseñas no coinciden entre sí.");
            }
        } else {
            //contrasena incorrecta
            camposValidos = false;
            avisoTextView.setVisibility(View.VISIBLE);
            avisoTextView.setText("La contraseña no posee el formato correcto.");
        }

        if (!correoElectronico.isEmpty() && Pattern.compile("[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", Pattern.CASE_INSENSITIVE).matcher(correoElectronico).matches()) {
            //correcto

        }else{
            camposValidos = false;
            avisoTextView.setVisibility(View.VISIBLE);
            avisoTextView.setText("El correo electrónico no posee un formato correcto.");
        }

        if (correoElectronico.isEmpty() || contrasena.isEmpty() || nombre.isEmpty() || apellidos.isEmpty()) {
            camposValidos = false;
            avisoTextView.setVisibility(View.VISIBLE);
            avisoTextView.setText("Rellene todos los campos antes de continuar.");
        }

        return camposValidos;
    }
}