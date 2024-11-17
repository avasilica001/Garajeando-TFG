package com.example.garajeando;

import static android.icu.lang.UCharacter.toUpperCase;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
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

    String idComunidad, usuario, idCoche, accion, matricula, marca, modelo, descripcion, transmision, combustible, aireAcondicionadoString, bluetoothString, gpsString, b64p, nombreFotoPrincipal;
    Integer plazas, puertas, numFotos;
    Boolean aireAcondicionado, bluetooth, gps;

    JSONArray respuestaFotos, respuestaInfo;
    private final String[] nombreFotosCoche = new String[9];
    private static final String URL_BASE_FOTOS = "http://ec2-51-20-10-72.eu-north-1.compute.amazonaws.com/imagenes/fotoscoches/";
    FotosCocheAdapter fotosCocheAdapter;

    Uri imagenPrincipalUriCambio, imagenDialogoUri, imagen;

    private static final int IMAGE_CODE=112;
    private static final int PHOTO_CODE=111;

    ActivityResultLauncher<String> activityResultLauncherElegirFoto;
    ActivityResultLauncher<Uri> activityResultLauncherSacarFoto;

    private final int F_PRINCIPAL = 0;
    private final int F_FRONTAL = 1;
    private final int F_REVERSO = 2;
    private int target;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Preferencias.aplicarTema(this);
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

        setSupportActionBar(findViewById(R.id.modificarCocheToolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        matriculaEditText = findViewById(R.id.matriculaEditText);
        marcaEditText = findViewById(R.id.marcaEditText);
        modeloEditText = findViewById(R.id.modeloEditText);
        plazasEditText = findViewById(R.id.plazasEditText);
        puertasEditText = findViewById(R.id.puertasEditText);
        descripcionEditText = findViewById(R.id.descripcionEditText);
        informacionDatosCocheTextView = findViewById(R.id.informacionDatosCocheTextView);
        informacionDatosCocheTextView.setVisibility(View.GONE);

        transmisionRadioGroup = findViewById(R.id.transmisionRadioGroup);
        automaticoRadioButton = findViewById(R.id.automaticoRadioButton);
        manualRadioButton = findViewById(R.id.manualRadioButton);

        combustibleRadioGroup = findViewById(R.id.combustibleRadioGroup);
        dieselRadioButton = findViewById(R.id.dieselRadioButton);
        gasolinaRadioButton = findViewById(R.id.gasolinaRadioButton);
        electricoRadioButton = findViewById(R.id.electricoRadioButton);

        aireAcondicionadoCheckBox = findViewById(R.id.aireAcondicionadoCheckBox);
        bluetoothCheckBox = findViewById(R.id.bluetoothCheckBox);
        gpsCheckBox = findViewById(R.id.gpsCheckBox);

        imagenPrincipalImageView = findViewById(R.id.imagenPrincipalCocheImageView);

        fotosGridView = findViewById(R.id.imagenesSecundariasCocheGridView);

        guardarInformacion = findViewById(R.id.guardarInformacionButton);

        if (accion.equals("modificar")){
            idCoche = getIntent().getExtras().getString("idCoche");
            matriculaEditText.setVisibility(View.GONE);

            getSupportActionBar().setTitle(matricula);

            obtenerInfoCoche();
        }else{
            matriculaEditText.setVisibility(View.VISIBLE);

            getSupportActionBar().setTitle("Añadir nuevo coche");

            fotosCocheAdapter = new FotosCocheAdapter(context, nombreFotosCoche, 9);
            fotosGridView.setAdapter(fotosCocheAdapter);

            //float factor = context.getResources().getDisplayMetrics().density;
            //fotosGridView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            //imagenPrincipalImageView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        guardarInformacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                matricula = matriculaEditText.getText().toString().trim();
                marca = marcaEditText.getText().toString().trim();
                modelo = modeloEditText.getText().toString().trim();

                if(plazasEditText.getText().toString().trim().equals("")){plazas = 0;} else{plazas = Integer.parseInt(plazasEditText.getText().toString().trim());}
                if(puertasEditText.getText().toString().trim().equals("")){puertas = 0;} else{puertas = Integer.parseInt(puertasEditText.getText().toString().trim());}

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
                if(aireAcondicionadoCheckBox.isChecked()){aireAcondicionadoString="1";}else{aireAcondicionadoString="0";}
                if(bluetoothCheckBox.isChecked()){bluetoothString="1";}else{bluetoothString="0";}
                if(gpsCheckBox.isChecked()){gpsString="1";}else{gpsString="0";}
                descripcion = descripcionEditText.getText().toString().trim();

                if(imagenPrincipalUriCambio != null){
                    try {
                        Bitmap bprincipal = MediaStore.Images.Media.getBitmap(getContentResolver(),imagenPrincipalUriCambio);
                        ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
                        bprincipal.compress(Bitmap.CompressFormat.PNG, 100, baos1);
                        byte[] b1= baos1.toByteArray();
                        b64p = Base64.encodeToString(b1, Base64.DEFAULT);
                        String tiempo = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                        nombreFotoPrincipal = "IMG_" + tiempo + "_P.png";
                    } catch (IOException e) {}
                }else{
                    nombreFotoPrincipal = "none";
                    b64p = "none";
                }

                if((!marca.isEmpty() && !modelo.isEmpty() && plazas > 0 && puertas > 0 && !transmision.isEmpty() && !combustible.isEmpty() && !aireAcondicionadoString.isEmpty() && !bluetoothString.isEmpty() && !gpsString.isEmpty() && !descripcion.isEmpty()) || (accion.equals("aniadir") && !matricula.isEmpty())) {

                    if (accion.equals("modificar")){
                        actualizarDatosCoche();
                    }else{
                        if(imagenPrincipalUriCambio != null){
                            anadirCoche();
                        }else{
                            informacionDatosCocheTextView.setText("Debe añadir una foto del coche para poder continuar.");
                            informacionDatosCocheTextView.setVisibility(View.VISIBLE);
                        }
                    }
                }
                else{
                    informacionDatosCocheTextView.setText("Debe rellenar todos los campos para poder continuar.");
                    informacionDatosCocheTextView.setVisibility(View.VISIBLE);
                }
            }
        });

        imagenPrincipalImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                target = F_PRINCIPAL;
                verImagenenGrande();
            }
        });

        activityResultLauncherSacarFoto = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                new ActivityResultCallback<Boolean>() {
                    @Override
                    public void onActivityResult(Boolean result) {
                        // There are no request code
                        if (result) {
                            try {

                                Toast.makeText(ModificarCoche.this, "Se ha guardado la imagen en la galería", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                //no hace nada
                            }
                        }
                        Intent intent2 = new Intent(ModificarCoche.this, RecortarImagen.class);
                        intent2.putExtra("data", String.valueOf(imagen));
                        if(target == F_PRINCIPAL){
                            intent2.putExtra("formato", "11");
                        }else{
                            intent2.putExtra("formato", "169");
                        }
                        startActivityForResult(intent2,101);
                    }
                });


        activityResultLauncherElegirFoto = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri data) {
                        if (data != null && !data.equals(Uri.EMPTY)){
                            imagen = data;
                            //imagenPerfil.setImageURI(imagen);

                            Intent intent2 = new Intent(ModificarCoche.this, RecortarImagen.class);
                            intent2.putExtra("data", String.valueOf(imagen));
                            if(target == F_PRINCIPAL){
                                intent2.putExtra("formato", "11");
                            }else{
                                intent2.putExtra("formato", "169");
                            }
                            startActivityForResult(intent2,101);
                        }
                    }
                });

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("nombreFotoPrincipal", nombreFotoPrincipal);

        if(imagenPrincipalUriCambio != null && !imagenPrincipalUriCambio.equals(Uri.EMPTY)) {
            outState.putParcelable("imagenPrincipalUriCambio", imagenPrincipalUriCambio);
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        nombreFotoPrincipal = savedInstanceState.getString("nombreFotoPrincipal");

        imagenPrincipalUriCambio = savedInstanceState.getParcelable("imagenPrincipalUriCambio");
        if(imagenPrincipalUriCambio != null && !imagenPrincipalUriCambio.equals(Uri.EMPTY)) {
            imagenPrincipalImageView.setImageURI(savedInstanceState.getParcelable("imagenPrincipalUriCambio"));
        }
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
        //menu.findItem(R.id.PerfilToobarItem).setVisible(condition2);
        //menu.findItem(R.id.PreferenciasToobarItem).setVisible(condition3);
        menu.findItem(R.id.AdministradorToobarItem).setVisible(false);
        //menu.findItem(R.id.CerrarSesionToobarItem).setVisible(CerrarSesionToobarItem);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.PerfilToobarItem) {
            Intent intentPerfil = new Intent(ModificarCoche.this, PerfilUsuario.class);
            intentPerfil.putExtra("usuario", usuario);
            intentPerfil.putExtra("idComunidad", idComunidad);
            intentPerfil.putExtra("idUsuarioPerfil", usuario);
            intentPerfil.putExtra("Administrador", "No");
            startActivityForResult(intentPerfil,1);
            return true;
        } else if (itemId == R.id.TemaToobarItem) {
            boolean esOscuro = Preferencias.esTemaOscuro(this);
            Preferencias.setTemaOscuro(this, !esOscuro);
            Intent intentTema = getIntent();
            finish();
            startActivity(intentTema);
            return true;
        } else if (itemId == R.id.CerrarSesionToobarItem) {
            Intent intentCerrarSesion = new Intent(ModificarCoche.this, IniciarSesion.class);
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

    private void obtenerInfoCoche(){
        String idCocheEncoded = "";
        try {
            idCocheEncoded = URLEncoder.encode(idCoche, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            //e.printStackTrace();
        }

        StringRequest peticion = new StringRequest(Request.Method.GET,
                Constantes.URL_OBTENERINFOCOCHE+"?IdCoche="+idCocheEncoded,
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
                            aireAcondicionadoCheckBox.setChecked(aireAcondicionado);
                            bluetoothCheckBox.setChecked(bluetooth);
                            gpsCheckBox.setChecked(gps);
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

    private void guardarInfo(){
        try{
            if(imagenPrincipalUriCambio==null){
                nombreFotoPrincipal = respuestaFotos.getJSONObject(0).getString("FotoCoche");
                Glide.with(this.context).load(URL_BASE_FOTOS+nombreFotoPrincipal).into(imagenPrincipalImageView);
            }
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
            aireAcondicionado = respuestaInfo.getJSONObject(0).getString("AireAcondicionado").equals("1");
            bluetooth = respuestaInfo.getJSONObject(0).getString("Bluetooth").equals("1");
            gps = respuestaInfo.getJSONObject(0).getString("GPS").equals("1");
            descripcion = respuestaInfo.getJSONObject(0).getString("Descripcion");
        }catch (Exception e){
            //no hace nada
        }
    }

    private void anadirCoche(){
        StringRequest peticion = new StringRequest(Request.Method.POST,
                Constantes.URL_ANADIRCOCHE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String respuesta) {
                        try {
                            JSONObject objetoJSON = new JSONObject(respuesta);
                            informacionDatosCocheTextView.setText(objetoJSON.getString("mensaje"));
                            informacionDatosCocheTextView.setVisibility(View.VISIBLE);

                            if(objetoJSON.getString("error").equals("false")){
                                Intent intentResultado = new Intent();
                                setResult(3, intentResultado);
                                finish();
                            }

                            AdministradorPeticiones.getInstance(context).cancelAll("peticion");
                        } catch (JSONException e) {
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
                parametros.put("IdComunidad", idComunidad);
                parametros.put("Propietario", usuario);
                parametros.put("Matricula", toUpperCase(matricula));
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
                parametros.put("NombreFotoPrincipal", nombreFotoPrincipal);
                parametros.put("FotoPrincipal", b64p);
                return parametros;
            }
        };

        peticion.setTag("peticion");
        AdministradorPeticiones.getInstance(this).addToRequestQueue(peticion);
    }

    private void actualizarDatosCoche(){
        StringRequest peticion = new StringRequest(Request.Method.POST,
                Constantes.URL_ACTUALIZARINFOCOCHE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String respuesta) {
                        try {
                            JSONObject objetoJSON = new JSONObject(respuesta);

                            Intent intentResultado = new Intent();
                            setResult(3, intentResultado);
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
                parametros.put("NombreFotoPrincipal", nombreFotoPrincipal);
                parametros.put("FotoPrincipal", b64p);
                return parametros;
            }
        };

        peticion.setTag("peticion");
        AdministradorPeticiones.getInstance(this).addToRequestQueue(peticion);
    }

    private void verImagenenGrande() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialogo_imagen);


        ImageView imagenGrandeImageView = dialog.findViewById(R.id.imagenGrandeImageView);

        if(target == 0 && nombreFotoPrincipal!=null && imagenPrincipalUriCambio == null && accion.equals("modificar")){
            String urlCompleta= URL_BASE_FOTOS+nombreFotoPrincipal;
            Glide.with(this.context).load(urlCompleta).into(imagenGrandeImageView);
        } else if (imagenPrincipalUriCambio != null ) {
            imagenDialogoUri = imagenPrincipalUriCambio;
            imagenGrandeImageView.setImageURI(imagenDialogoUri);
        } else {
            imagenDialogoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.coche);
            imagenGrandeImageView.setImageURI(imagenDialogoUri);
        }


        Button changeImageButton = dialog.findViewById(R.id.cambiarImagenButton);
        changeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoSeleccion();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void mostrarDialogoSeleccion(){
        String[] opciones = {"Sacar una foto","Seleccionar de la galería"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona una opción para la foto de tu coche");
        builder.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0){
                    //se ha de mirar primero si la version es igual o superior a la marshmallow
                    //si es igual o superior
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        //si no hay permisos para acceder a la cámara, o escribir
                        if (ActivityCompat.checkSelfPermission(ModificarCoche.this,android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(ModificarCoche.this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            //dar permisos de camara y escritura
                            ActivityCompat.requestPermissions(ModificarCoche.this,new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PHOTO_CODE);
                        } else {
                            //si ya hay permisos se abre la camara
                            abrirCamara();
                        }
                    }
                    //si la version es menor a la marshmallow
                    else {
                        abrirCamara();
                    }
                }else if (i == 1){


                    //mirar si la version es mayor o igual a marshmallow
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        //si lo es mirar si se han dado los permisos de lectura
                        if (ActivityCompat.checkSelfPermission(ModificarCoche.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            //si no hay permisos de lectura darselos
                            ActivityCompat.requestPermissions(ModificarCoche.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, IMAGE_CODE);
                        } else {
                            //si ya hay permisos
                            elegirfoto();
                        }
                    } else {
                        //si la version es menor a marshmallow
                        elegirfoto();
                    }
                }
            }
        });
        builder.create();

        Preferencias.setTemaAlertDialogConOpciones(builder, context);
    }

    //metodo para cuando se usa la camara
    private void abrirCamara() {
        ContentValues cv = new ContentValues();
        //informacion de la imagen
        cv.put(MediaStore.Images.Media.TITLE, "Nueva Imagen");
        cv.put(MediaStore.Images.Media.DESCRIPTION, "Nueva Imagen sacada con la cámara");
        //uri de la imagen
        imagen = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
        //crear intent para la camara
        Intent camarai = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        camarai.putExtra(MediaStore.EXTRA_OUTPUT, imagen);

        activityResultLauncherSacarFoto.launch(imagen);
    }

    //metodo para cuando se elige foto de la galeria
    private void elegirfoto() {
        activityResultLauncherElegirFoto.launch("image/*");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //si se esta sacando una foto
        if(requestCode==PHOTO_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //se han dado los permisos necesarios
                abrirCamara();
            } else {
                //no se han dado los permisos necesarios
                Toast.makeText(this, "No se han aceptado los permisos", Toast.LENGTH_SHORT).show();
            }
        }
        //si se esta eligiendo una foto de la galeria
        if(requestCode==IMAGE_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //se han dado los permisos necesarios
                elegirfoto();
            } else {
                //no se han dado los permisos necesarios
                Toast.makeText(this, "No se han aceptado los permisos", Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1 && requestCode == 101) {
            data.getExtras().get("data");
            String resultadoRecorte = String.valueOf(data.getExtras().get("data"));
            Uri uriResultadoRecorte = null;

            if (resultadoRecorte != null) {
                uriResultadoRecorte = Uri.parse(resultadoRecorte);

                if (target == F_PRINCIPAL) {
                    imagenPrincipalUriCambio = uriResultadoRecorte;
                    imagenPrincipalImageView.setImageURI(imagenPrincipalUriCambio);
                } else {
                }
            }
        }
    }
}