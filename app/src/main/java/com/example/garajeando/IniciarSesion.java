package com.example.garajeando;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
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
import java.util.regex.Pattern;

public class IniciarSesion extends AppCompatActivity {

    EditText correoElectronicoEditText, contrasenaEditText;
    TextView avisoTextView;
    Button iniciarSesionButton, registrarseButtonIS;

    Boolean contrasenaVisible;

    String aviso, correoElectronico, contrasena, contrasenaEncriptada;

    @SuppressLint({"ClickableViewAccessibility", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_iniciar_sesion);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.IniciarSesionLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Setear barra superior
        setSupportActionBar(findViewById(R.id.iniciarSesionToolbar));

        correoElectronicoEditText = findViewById(R.id.correoElectronicoEditTextIS);
        contrasenaEditText = findViewById(R.id.contrasenaEditTextIS);
        contrasenaVisible = false;

        avisoTextView = findViewById(R.id.avisoTextViewIS);

        iniciarSesionButton = findViewById(R.id.iniciarSesionButton);
        iniciarSesionButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                iniciarSesion();
            }
        });

        registrarseButtonIS = findViewById(R.id.registrarseButtonIS);
        registrarseButtonIS.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(IniciarSesion.this, Registrarse.class));
            }
        });

        contrasenaEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= contrasenaEditText.getRight() - contrasenaEditText.getCompoundDrawables()[2].getBounds().width() - 60) {
                        int seleccion = contrasenaEditText.getSelectionEnd();
                        if (contrasenaVisible) {
                            contrasenaEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.candado_cerrado, 0, R.drawable.ojo_cerrado, 0);
                            contrasenaEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            contrasenaVisible = false;
                        } else {
                            contrasenaEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.candado_cerrado, 0, R.drawable.ojo_abierto, 0);
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
        contrasenaEditText = findViewById(R.id.contrasenaEditTextIS);
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
            contrasenaEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.candado_cerrado, 0, R.drawable.ojo_abierto, 0);
            contrasenaEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            contrasenaVisible = true;
        } else {
            contrasenaEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.candado_cerrado, 0, R.drawable.ojo_cerrado, 0);
            contrasenaEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            contrasenaVisible = false;
        }
        contrasenaEditText.setSelection(seleccion);
        contrasenaEditText = findViewById(R.id.contrasenaEditTextIS);
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
        menu.findItem(R.id.PreferenciasToobarItem).setVisible(true);
        menu.findItem(R.id.AdministradorToobarItem).setVisible(false);
        menu.findItem(R.id.CerrarSesionToobarItem).setVisible(false);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.PreferenciasToobarItem) {
            //Intent intentThree = new Intent(this, ActivityThree.class);
            //startActivity(intentThree);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void iniciarSesion(){
        correoElectronico = correoElectronicoEditText.getText().toString().trim();
        contrasena = contrasenaEditText.getText().toString().trim();

        if (validarCredencialesIS()) {
            try {
                contrasenaEncriptada = Encriptador.encrypt(contrasena);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            peticionVerificarUsuario();
        }
    }

    private Boolean validarCredencialesIS(){
        correoElectronico = correoElectronicoEditText.getText().toString().trim();
        contrasena = contrasenaEditText.getText().toString().trim();

        Boolean camposValidos = true;

        if (!correoElectronico.isEmpty() && Pattern.compile("[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", Pattern.CASE_INSENSITIVE).matcher(correoElectronico).matches()) {
            //correcto

        }else{
            camposValidos = false;
            avisoTextView.setVisibility(View.VISIBLE);
            avisoTextView.setText("El correo electrónico no posee un formato correcto.");
        }

        if (correoElectronico.isEmpty() || contrasena.isEmpty()) {
            camposValidos = false;
            avisoTextView.setVisibility(View.VISIBLE);
            avisoTextView.setText("Rellene todos los campos antes de continuar.");
        }

        return camposValidos;
    }

    private void peticionVerificarUsuario() {
        StringRequest peticion = new StringRequest(Request.Method.POST,
                Constantes.URL_VERIFICARUSUARIO,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String respuesta) {
                        avisoTextView.setVisibility(View.VISIBLE);

                        try {
                            JSONObject objetoJSON = new JSONObject(respuesta);

                            if(objetoJSON.getString("error").equals("false")){
                                Intent intent = new Intent(IniciarSesion.this, TusComunidades.class);
                                intent.putExtra("idUsuario", objetoJSON.getString("mensaje"));
                                startActivityForResult(intent,1);
                            }else {
                                avisoTextView.setText("El correo electrónico o la contraseña introducidos son incorrectos.");
                            }

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

                return parametros;
            }
        };

        AdministradorPeticiones.getInstance(this).addToRequestQueue(peticion);
    }

}