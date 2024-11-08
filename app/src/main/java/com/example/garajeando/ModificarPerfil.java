package com.example.garajeando;

import android.Manifest;
import android.app.Activity;
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
import android.widget.EditText;
import android.widget.ImageView;
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

public class ModificarPerfil extends AppCompatActivity {

    private Activity activity = this;
    private Context context = this;

    String usuario, idComunidad, idUsuarioPerfil;

    String nombre, apellidos, correo, direccion, puntos, nombreFotoPrincipal, b64p;
    Boolean borrar = false;

    EditText nombreEditText, apellidosEditText, correoEditText, direccionEditText;
    TextView avisoModificarUsuarioTextView;
    ImageView fotoPerfilUsuarioModificarImageView;
    Button actualizarInformacionUsuarioButton;

    private static final String URL_BASE_FOTOS = "http://ec2-51-20-10-72.eu-north-1.compute.amazonaws.com/imagenes/fotosperfil/";
    Uri imagenPrincipalUriCambio, imagenDialogoUri, imagen, uriResultadoRecorte;

    private static final int IMAGE_CODE=112;
    private static final int PHOTO_CODE=111;

    ActivityResultLauncher<String> activityResultLauncherElegirFoto;
    ActivityResultLauncher<Uri> activityResultLauncherSacarFoto;

    private JSONArray respuestaUsuario;

    private final int F_PRINCIPAL = 0;
    private final int F_FRONTAL = 1;
    private final int F_REVERSO = 2;
    private int target;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Preferencias.aplicarTema(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_modificar_perfil);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        usuario = getIntent().getExtras().getString("usuario");

        nombreEditText = findViewById(R.id.nombreEditText);
        apellidosEditText = findViewById(R.id.apellidosEditText);
        correoEditText = findViewById(R.id.correoEditText);
        direccionEditText = findViewById(R.id.direccionEditText);
        avisoModificarUsuarioTextView = findViewById(R.id.avisoModificarUsuarioTextView);
        avisoModificarUsuarioTextView.setVisibility(View.GONE);
        fotoPerfilUsuarioModificarImageView = findViewById(R.id.fotoPerfilUsuarioModificarImageView);

        actualizarInformacionUsuarioButton = findViewById(R.id.actualizarInformacionUsuarioButton);

        obtenerInfoUsuario();

        actualizarInformacionUsuarioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                nombre = nombreEditText.getText().toString().trim();
                apellidos = apellidosEditText.getText().toString().trim();
                correo = correoEditText.getText().toString().trim();
                direccion = direccionEditText.getText().toString().trim();

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

                if (borrar) {
                    nombreFotoPrincipal = "borrar";
                    b64p = "none";
                }

                if(!nombre.isEmpty() && !apellidos.isEmpty() && !correo.isEmpty() && !direccion.isEmpty() && !nombreFotoPrincipal.isEmpty()) {
                    actualizarInfoUsuario();
                }
                else{
                    avisoModificarUsuarioTextView.setText("Debe rellenar todos los campos para poder continuar.");
                    avisoModificarUsuarioTextView.setVisibility(View.VISIBLE);
                }
            }
        });

        fotoPerfilUsuarioModificarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                                Toast.makeText(ModificarPerfil.this, "Se ha guardado la imagen en la galería", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                //no hace nada
                            }
                        }
                        Intent intent2 = new Intent(ModificarPerfil.this, RecortarImagen.class);
                        intent2.putExtra("data", String.valueOf(imagen));
                        if(target == F_PRINCIPAL){
                            intent2.putExtra("formato", "11");
                        }else{
                            intent2.putExtra("formato", "169") ;
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

                            Intent intent2 = new Intent(ModificarPerfil.this, RecortarImagen.class);
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

    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean("borrar", borrar);
        outState.putString("nombreFotoPrincipal", nombreFotoPrincipal);
        outState.putParcelable("imagenPrincipalUriCambio", imagenPrincipalUriCambio);

        if(uriResultadoRecorte != null && !uriResultadoRecorte.equals(Uri.EMPTY) && !borrar) {
            outState.putParcelable("uriResultadoRecorte", uriResultadoRecorte);
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        borrar = savedInstanceState.getBoolean("borrar");
        nombreFotoPrincipal = savedInstanceState.getString("nombreFotoPrincipal");
        imagenPrincipalUriCambio = savedInstanceState.getParcelable("imagenPrincipalUriCambio");

        uriResultadoRecorte = savedInstanceState.getParcelable("uriResultadoRecorte");
        if(uriResultadoRecorte != null && !uriResultadoRecorte.equals(Uri.EMPTY) && !borrar) {
            fotoPerfilUsuarioModificarImageView.setImageURI(savedInstanceState.getParcelable("uriResultadoRecorte"));
        }
    }

    private void obtenerInfoUsuario(){
        StringRequest peticion = new StringRequest(Request.Method.POST,
                Constantes.URL_OBTENERUSUARIO,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String respuesta) {
                        try {
                            JSONObject objetoJSON = new JSONObject(respuesta);

                            respuestaUsuario = objetoJSON.getJSONArray("Usuario");

                            guardarInfoUsuario();

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
                parametros.put("IdUsuario", usuario);

                return parametros;
            }
        };

        peticion.setTag("peticion");
        AdministradorPeticiones.getInstance(this).addToRequestQueue(peticion);
    }

    private void guardarInfoUsuario(){
        try{
            nombreFotoPrincipal = respuestaUsuario.getJSONObject(0).getString("FotoPerfil");
            if(!nombreFotoPrincipal.equals("") || nombreFotoPrincipal.equals("borrar")){
                Glide.with(this.context).load(URL_BASE_FOTOS+nombreFotoPrincipal).into(fotoPerfilUsuarioModificarImageView);
            }

            nombre = respuestaUsuario.getJSONObject(0).getString("Nombre");
            apellidos = respuestaUsuario.getJSONObject(0).getString("Apellidos");
            correo = respuestaUsuario.getJSONObject(0).getString("CorreoElectronico");
            direccion = respuestaUsuario.getJSONObject(0).getString("Direccion");
            puntos = respuestaUsuario.getJSONObject(0).getString("PuntosTotales");

            nombreEditText.setText(nombre);
            apellidosEditText.setText(apellidos);
            correoEditText.setText(correo);
            direccionEditText.setText(direccion);

            setSupportActionBar(findViewById(R.id.modificarUsuarioToolbar));
            getSupportActionBar().setTitle(nombre + " " + apellidos);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

        }catch (Exception e){
            //no hace nada
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
        menu.findItem(R.id.PerfilToobarItem).setVisible(false);
        //menu.findItem(R.id.PreferenciasToobarItem).setVisible(condition3);
        menu.findItem(R.id.AdministradorToobarItem).setVisible(false);
        //menu.findItem(R.id.CerrarSesionToobarItem).setVisible(CerrarSesionToobarItem);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.TemaToobarItem) {
            boolean esOscuro = Preferencias.esTemaOscuro(this);
            Preferencias.setTemaOscuro(this, !esOscuro);
            Intent intentTema = getIntent();
            finish();
            startActivity(intentTema);
            return true;
        } else if (itemId == R.id.CerrarSesionToobarItem) {
            Intent intentCerrarSesion = new Intent(ModificarPerfil.this, IniciarSesion.class);
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

    private void actualizarInfoUsuario(){
        StringRequest peticion = new StringRequest(Request.Method.POST,
                Constantes.URL_ACTUALIZARINFOUSUARIO,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String respuesta) {
                        try {
                            JSONObject objetoJSON = new JSONObject(respuesta);

                            //respuestaUsuario = objetoJSON.getString("mensaje");

                            if (objetoJSON.getString("error").equals("true")) {
                                avisoModificarUsuarioTextView.setVisibility(View.VISIBLE);
                                avisoModificarUsuarioTextView.setText("No se ha podido actualizar la información.");
                            }else{
                                Intent intentResultado = new Intent();
                                setResult(3, intentResultado);
                                finish();
                            }
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
                parametros.put("IdUsuario", usuario);
                parametros.put("Nombre", nombre);
                parametros.put("Apellidos", apellidos);
                parametros.put("CorreoElectronico", correo);
                parametros.put("Direccion", direccion);
                parametros.put("NombreFoto", nombreFotoPrincipal);
                parametros.put("FotoPerfil", b64p);

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

        if(!nombreFotoPrincipal.equals("") && imagenPrincipalUriCambio == null && !borrar){
            String urlCompleta= URL_BASE_FOTOS+nombreFotoPrincipal;
            Glide.with(this.context).load(urlCompleta).into(imagenGrandeImageView);
        } else if (imagenPrincipalUriCambio != null && !borrar) {
            imagenDialogoUri = imagenPrincipalUriCambio;
            imagenGrandeImageView.setImageURI(imagenDialogoUri);
        } else {
            imagenDialogoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.circulo_usuario);
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
        String[] opciones = {"Sacar una foto","Seleccionar de la galería","Borrar la foto de perfil"};
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
                        if (ActivityCompat.checkSelfPermission(ModificarPerfil.this,android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(ModificarPerfil.this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            //dar permisos de camara y escritura
                            ActivityCompat.requestPermissions(ModificarPerfil.this,new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PHOTO_CODE);
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
                        if (ActivityCompat.checkSelfPermission(ModificarPerfil.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            //si no hay permisos de lectura darselos
                            ActivityCompat.requestPermissions(ModificarPerfil.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, IMAGE_CODE);
                        } else {
                            //si ya hay permisos
                            elegirfoto();
                        }
                    } else {
                        //si la version es menor a marshmallow
                        elegirfoto();
                    }
                }else if (i == 2){
                    borrar = true;
                    fotoPerfilUsuarioModificarImageView.setImageResource(R.drawable.circulo_usuario);
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
            uriResultadoRecorte = null;

            if (resultadoRecorte != null) {
                uriResultadoRecorte = Uri.parse(resultadoRecorte);

                imagenPrincipalUriCambio = uriResultadoRecorte;
                fotoPerfilUsuarioModificarImageView.setImageURI(imagenPrincipalUriCambio);
            }
        }

    }
}