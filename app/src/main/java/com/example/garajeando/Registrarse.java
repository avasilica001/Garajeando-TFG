package com.example.garajeando;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.cast.framework.media.ImagePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import android.Manifest;
import com.canhub.cropper.CropImage;
import com.canhub.cropper.CropImageActivity;

public class Registrarse extends AppCompatActivity {

    private EditText correoElectronicoEditText, contrasenaEditText, repetirContrasenaEditText, nombreEditText, apellidosEditText;
    private Button registrarseButton, anadirFotoButton;
    private TextView avisoTextView;
    private ImageView imagenPerfil;
    Toolbar barraSuperiorRegistrarseToolbar;

    private String correoElectronico, contrasena, repetirContrasena, nombre, apellidos, aviso, contrasenaEncriptada, pathImagen;
    Uri uriImagen;

    public static Boolean contrasenaVisible;

    private Uri imagen;
    private Bitmap bimagen;
    private String b64;
    private String nombreimagen;

    private static final int IMAGE_CODE=112;
    private static final int PHOTO_CODE=111;

    ActivityResultLauncher<String> activityResultLauncherElegirFoto;




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

        registrarseButton = (Button) findViewById(R.id.registrarseButtonR);
        registrarseButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                crearUsuario();
            }
        });

        imagenPerfil = (ImageView) findViewById(R.id.fotoPerfilR);

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");

        activityResultLauncherElegirFoto = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri data) {
                        imagen = data;
                        imagenPerfil.setImageURI(imagen);
                    }
                });


        anadirFotoButton = (Button) findViewById(R.id.anadirFotoButtonR);
        anadirFotoButton.bringToFront();
        anadirFotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoSeleccion();

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
        contrasenaEditText = (EditText) findViewById(R.id.contrasenaEditTextR);

        if(imagen != null && !imagen.equals(Uri.EMPTY)) {
            outState.putParcelable("imagen", imagen);
        }
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
        contrasenaEditText = (EditText) findViewById(R.id.contrasenaEditTextR);

        imagen=savedInstanceState.getParcelable("imagen");
        imagenPerfil.setImageURI(savedInstanceState.getParcelable("imagen"));
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
                        if (ActivityCompat.checkSelfPermission(Registrarse.this,android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(Registrarse.this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            //dar permisos de camara y escritura
                            ActivityCompat.requestPermissions(Registrarse.this,new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PHOTO_CODE);
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
                        if (ActivityCompat.checkSelfPermission(Registrarse.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            //si no hay permisos de lectura darselos
                            ActivityCompat.requestPermissions(Registrarse.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, IMAGE_CODE);
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

        //startActivityForResult(camarai, 1111);

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Intent data = result.getData();

                            //ver vista previa de la imagen
                            imagenPerfil.setImageURI(imagen);
                            //guardar la imagen en bitmap para luego subirla a la bd

                            //encontrar directorio de la galeria
                            File directorio = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

                            //crear nombre de la foto sacada
                            String tiempo = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                            nombreimagen = "IMG_" + tiempo + ".png";
                            File imagenfinal = new File(directorio, nombreimagen);

                            try {
                                //guardar la foto en un file y enviarla a la galeria
                                FileOutputStream fos = new FileOutputStream(imagenfinal);
                                bimagen.compress(Bitmap.CompressFormat.PNG, 100, fos);
                                fos.flush();
                                fos.close();

                                Toast.makeText(Registrarse.this, "Se ha guardado la imagen en la galería", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                //no hace nada
                            }
                        }
                    }
                });

        activityResultLauncher.launch(camarai);
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
            finish();
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