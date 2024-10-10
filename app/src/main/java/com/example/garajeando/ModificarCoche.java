package com.example.garajeando;

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
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
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

    String idComunidad, usuario, idCoche, accion, matricula, marca, modelo, descripcion, transmision, combustible;
    Integer plazas, puertas, numFotos;
    Boolean aireAcondicionado, bluetooth, gps;

    JSONArray respuestaFotos, respuestaInfo;
    private String[] nombreFotosCoche = new String[9];
    private static String URL_BASE_FOTOS = "http://ec2-51-20-10-72.eu-north-1.compute.amazonaws.com/imagenes/fotoscoches/";
    FotosCocheAdapter fotosCocheAdapter;
    String nombreFotoPrincipal;

    Uri imagenPrincipalUriCambio, imagenDialogoUri, imagen;

    private static final int IMAGE_CODE=112;
    private static final int PHOTO_CODE=111;

    ActivityResultLauncher<String> activityResultLauncherElegirFoto;
    ActivityResultLauncher<Uri> activityResultLauncherSacarFoto;

    private int F_PRINCIPAL = 0, F_FRONTAL = 1, F_REVERSO = 2, target;



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

            fotosCocheAdapter = new FotosCocheAdapter(context, nombreFotosCoche, 9);
            fotosGridView.setAdapter(fotosCocheAdapter);

            //float factor = context.getResources().getDisplayMetrics().density;
            //fotosGridView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            //imagenPrincipalImageView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
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
                        if(imagenPrincipalUriCambio != null){
                            try {
                                Bitmap bprincipal = MediaStore.Images.Media.getBitmap(getContentResolver(),imagenPrincipalUriCambio);
                                ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
                                bprincipal.compress(Bitmap.CompressFormat.PNG, 100, baos1);
                                byte[] b1= baos1.toByteArray();
                                String b64p = Base64.encodeToString(b1, Base64.DEFAULT);
                                String tiempo = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                                String nombreFotoPrincipal = "IMG_" + tiempo + "_P.png";
                                parametros.put("NombreFotoPrincipal", nombreFotoPrincipal);
                                parametros.put("FotoPrincipal", b64p);
                            } catch (IOException e) {}
                        }else{
                            parametros.put("NombreFotoPrincipal", "none");
                            parametros.put("FotoPrincipal", "none");
                        }
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

    private void verImagenenGrande() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialogo_imagen);


        ImageView imagenGrandeImageView = dialog.findViewById(R.id.imagenGrandeImageView);

        if(target == 0 && nombreFotoPrincipal!=null && imagenPrincipalUriCambio == null){
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

        dialog.show(); // Show the dialog
    }

    private void mostrarDialogoSeleccion(){
        String opciones[] = {"Sacar una foto","Seleccionar de la galería"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona una opción para tu foto de perfil");
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
        builder.create().show();
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