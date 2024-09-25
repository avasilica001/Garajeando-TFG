package com.example.garajeando;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ModificarCoche extends AppCompatActivity {

    Context context = this;

    EditText matriculaEditText, marcaEditText, modeloEditText, plazasEditText, puertasEditText, descripcionEditText;
    TextView informacionDatosCocheTextView;
    RadioGroup transmisionRadioGroup, combustibleRadioGroup;
    RadioButton automaticoRadioButton, manualRadioButton, dieselRadioButton, gasolinaRadioButton, electricoRadioButton;
    CheckBox aireAcondicionadoCheckBox, bluetoothCheckBox, gpsCheckBox;
    ImageView imagenPrincipalImageView;
    GridView fotosGridView;
    Button guardarInformacion;

    String idComunidad, usuario, idCoche, accion, matricula, marca, modelo, descripcion, transmision, combustible;
    Integer plazas, puertas, numFotos;
    Boolean aireAcondicionado, bluetooth, gps;

    JSONArray respuestaFotos, respuestaInfo;
    private String[] nombreFotosCoche = new String[9];
    private static String URL_BASE_FOTOS = "http://ec2-51-20-10-72.eu-north-1.compute.amazonaws.com/imagenes/fotoscarnet/";
    FotosCocheAdapter fotosCocheAdapter;
    String nombreFotoPrincipal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_modificar_coche);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        usuario = getIntent().getExtras().getString("usuario");
        idComunidad = getIntent().getExtras().getString("idComunidad");
        accion = getIntent().getExtras().getString("accion");
        idCoche = getIntent().getExtras().getString("idCoche");

        matriculaEditText = (EditText) findViewById(R.id.matriculaEditText);
        marcaEditText = (EditText) findViewById(R.id.marcaEditText);
        modeloEditText = (EditText) findViewById(R.id.modeloEditText);
        plazasEditText = (EditText) findViewById(R.id.plazasEditText);
        puertasEditText = (EditText) findViewById(R.id.puertasEditText);
        descripcionEditText = (EditText) findViewById(R.id.descripcionEditText);
        informacionDatosCocheTextView = (TextView) findViewById(R.id.informacionDatosCocheTextView);
        informacionDatosCocheTextView.setVisibility(View.GONE);

        transmisionRadioGroup = (RadioGroup) findViewById(R.id.transmisionRadioGroup);
        automaticoRadioButton = (RadioButton) findViewById(R.id.automaticoRadioButton);
        manualRadioButton = (RadioButton) findViewById(R.id.manualRadioButton);

        combustibleRadioGroup = (RadioGroup) findViewById(R.id.combustibleRadioGroup);
        dieselRadioButton = (RadioButton) findViewById(R.id.dieselRadioButton);
        gasolinaRadioButton = (RadioButton) findViewById(R.id.gasolinaRadioButton);
        electricoRadioButton = (RadioButton) findViewById(R.id.electricoRadioButton);

        aireAcondicionadoCheckBox = (CheckBox) findViewById(R.id.aireAcondicionadoCheckBox);
        bluetoothCheckBox = (CheckBox) findViewById(R.id.bluetoothCheckBox);
        gpsCheckBox = (CheckBox) findViewById(R.id.gpsCheckBox);

        imagenPrincipalImageView = (ImageView) findViewById(R.id.imagenPrincipalCocheImageView);

        fotosGridView = (GridView) findViewById(R.id.imagenesSecundariasCocheGridView);

        guardarInformacion = (Button) findViewById(R.id.guardarInformacionButton);

        setSupportActionBar(findViewById(R.id.matriculaCocheElegidoToolbar));

        if (accion.equals("modificar")){
            idCoche = getIntent().getExtras().getString("idCoche");
            matriculaEditText.setVisibility(View.GONE);

            getSupportActionBar().setTitle(matricula);

            obtenerInfoCoche();
        }else{
            matriculaEditText.setVisibility(View.VISIBLE);

            getSupportActionBar().setTitle("Añadir nuevo coche");

            fotosCocheAdapter = new FotosCocheAdapter(context, nombreFotosCoche, numFotos);
            fotosGridView.setAdapter(fotosCocheAdapter);
        }

        guardarInformacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (accion.equals("modificar")){
                    actualizarDatosCoche();
                }else{
                    //anadirCoche();
                }
            }
        });

    }

    private void obtenerInfoCoche(){
        StringRequest peticion = new StringRequest(Request.Method.POST,
                Constantes.URL_OBTENERINFOCOCHE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String respuesta) {
                        try {
                            JSONObject objetoJSON = new JSONObject(respuesta);
                            numFotos = Integer.parseInt(String.valueOf(objetoJSON.getJSONArray("Fotos").length()));

                            respuestaFotos = objetoJSON.getJSONArray("Fotos");
                            respuestaInfo = objetoJSON.getJSONArray("Coche");
                            guardarInfo();

                            fotosCocheAdapter = new FotosCocheAdapter(context, nombreFotosCoche, numFotos);
                            fotosGridView.setAdapter(fotosCocheAdapter);
                            //fotosCocheAdapter.notifyDataSetChanged();

                            setSupportActionBar(findViewById(R.id.matriculaCocheElegidoToolbar));
                            getSupportActionBar().setTitle(matricula);

                            marcaEditText.setText(marca);
                            modeloEditText.setText(modelo);
                            plazasEditText.setText(String.valueOf(plazas));
                            puertasEditText.setText(String.valueOf(puertas));
                            if(transmision.equals("Automático")){
                                automaticoRadioButton.setChecked(true);
                            }else if (transmision.equals("Manual")){
                                manualRadioButton.setChecked(true);
                            }

                            if(combustible.equals("Diesel")){
                                dieselRadioButton.setChecked(true);
                            }else if (combustible.equals("Gasolina")){
                                gasolinaRadioButton.setChecked(true);
                            }
                            else if (combustible.equals("Eléctrico")){
                                electricoRadioButton.setChecked(true);
                            }
                            if(aireAcondicionado){aireAcondicionadoCheckBox.setChecked(true);}else{aireAcondicionadoCheckBox.setChecked(false);}
                            if(bluetooth){bluetoothCheckBox.setChecked(true);}else{bluetoothCheckBox.setChecked(false);}
                            if(gps){gpsCheckBox.setChecked(true);}else{gpsCheckBox.setChecked(false);}
                            descripcionEditText.setText(descripcion);

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
                parametros.put("IdCoche", idCoche);

                return parametros;
            }
        };

        peticion.setTag("peticion");
        AdministradorPeticiones.getInstance(this).addToRequestQueue(peticion);
    }

    private void guardarInfo(){
        try{
            nombreFotoPrincipal = respuestaFotos.getJSONObject(0).getString("FotoCoche");
            Glide.with(this.context).load(URL_BASE_FOTOS+nombreFotoPrincipal).into(imagenPrincipalImageView);
            for (int i = 1; i < respuestaFotos.length(); i++)
            {
                JSONObject jsonCoches = respuestaFotos.getJSONObject(i);

                nombreFotosCoche[i] = jsonCoches.getString("FotoCoche");
            }
            matricula = respuestaInfo.getJSONObject(0).getString("Matricula");
            marca = respuestaInfo.getJSONObject(0).getString("Marca");
            modelo = respuestaInfo.getJSONObject(0).getString("Modelo");
            plazas = Integer.parseInt(respuestaInfo.getJSONObject(0).getString("Plazas"));
            puertas = Integer.parseInt(respuestaInfo.getJSONObject(0).getString("Puertas"));
            transmision = respuestaInfo.getJSONObject(0).getString("Transmision");
            combustible = respuestaInfo.getJSONObject(0).getString("Combustible");
            if(respuestaInfo.getJSONObject(0).getString("AireAcondicionado").equals("1")){aireAcondicionado = true;}else{aireAcondicionado = false;}
            if(respuestaInfo.getJSONObject(0).getString("Bluetooth").equals("1")){bluetooth = true;} else{bluetooth = false;}
            if(respuestaInfo.getJSONObject(0).getString("GPS").equals("1")){gps = true;} else{gps = false;}
            descripcion = respuestaInfo.getJSONObject(0).getString("Descripcion");
        }catch (Exception e){
            //no hace nada
        }
    }

    private void actualizarDatosCoche(){
        marca = marcaEditText.getText().toString().trim();
        modelo = modeloEditText.getText().toString().trim();
        plazas = Integer.parseInt(plazasEditText.getText().toString().trim());
        puertas = Integer.parseInt(puertasEditText.getText().toString().trim());
        if(manualRadioButton.isChecked()){
            transmision = "Manual";
        }else if(automaticoRadioButton.isChecked()){
            transmision = "Automático";
        }
        if(dieselRadioButton.isChecked()){
            combustible = "Diesel";
        }else if(gasolinaRadioButton.isChecked()){
            combustible = "Gasolina";
        }else if(electricoRadioButton.isChecked()){
            combustible = "Eléctrico";
        }
        String aireAcondicionadoString;
        if(aireAcondicionadoCheckBox.isChecked()){aireAcondicionadoString="1";}else{aireAcondicionadoString="0";}
        String bluetoothString;
        if(bluetoothCheckBox.isChecked()){bluetoothString="1";}else{bluetoothString="0";}
        String gpsString;
        if(gpsCheckBox.isChecked()){gpsString="1";}else{gpsString="0";}
        descripcion = descripcionEditText.getText().toString().trim();

        if(!marca.isEmpty() && !modelo.isEmpty() && plazas > 0 && puertas > 0 && !transmision.isEmpty() && !combustible.isEmpty() && !aireAcondicionadoString.isEmpty() && !bluetoothString.isEmpty() && !gpsString.isEmpty() && !descripcion.isEmpty()){
            StringRequest peticion = new StringRequest(Request.Method.POST,
                    Constantes.URL_ACTUALIZARINFOCOCHE,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String respuesta) {
                            try {
                                JSONObject objetoJSON = new JSONObject(respuesta);

                                Intent intentResultado = new Intent();
                                setResult(2, intentResultado);
                                finish();
                                //devolver a la otra actividad

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
                    parametros.put("IdCoche", idCoche);
                    parametros.put("Marca", marca);
                    parametros.put("Modelo", modelo);
                    parametros.put("Plazas", String.valueOf(plazas));
                    parametros.put("Puertas", String.valueOf(puertas));
                    parametros.put("Transmision", transmision);
                    parametros.put("Combustible", combustible);
                    parametros.put("AireAcondicionado", aireAcondicionadoString);
                    parametros.put("Bluetooth", bluetoothString);
                    parametros.put("GPS", gpsString);
                    parametros.put("Descripcion", descripcion);

                    return parametros;
                }
            };

            peticion.setTag("peticion");
            AdministradorPeticiones.getInstance(this).addToRequestQueue(peticion);

        }
        else{
            informacionDatosCocheTextView.setText("Debe rellenar todos los campos para poder continuar.");
            informacionDatosCocheTextView.setVisibility(View.VISIBLE);
        }
    }
}