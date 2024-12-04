package com.example.garajeando;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.Activity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.bumptech.glide.Glide;

public class ListaUsuariosAdapter extends RecyclerView.Adapter<ListaUsuariosAdapter.UsuarioHolder> {

    private final Activity context;

    private final Activity activity;

    //arraylist para cada columna en la bd
    private ArrayList<Usuario> usuarios=new ArrayList<Usuario>();

    CardView l_usuarios;

    String usuario, idComunidad;
    Boolean noComunidadTodavia;

    public ListaUsuariosAdapter(Activity activity, Activity context, ArrayList<Usuario> usuarios, String usuario, String idComunidad, Boolean noComunidadTodavia) {
        this.context = context;
        this.activity = activity;
        this.usuarios = usuarios;
        this.usuario=usuario;
        this.idComunidad=idComunidad;
        this.noComunidadTodavia = noComunidadTodavia;
    }

    @NonNull
    @Override
    public UsuarioHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View l = LayoutInflater.from(context).inflate(R.layout.layout_usuarios, parent, false);

        return new UsuarioHolder(l);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioHolder holder, @SuppressLint("RecyclerView") int p) {
        if(usuarios.get(p).getFotoPerfil().equals("")){holder.fotoPerfilUsuarioAceptarImageView.setImageResource(R.drawable.circulo_usuario);}else{Glide.with(this.context).load("http://ec2-51-20-10-72.eu-north-1.compute.amazonaws.com/imagenes/fotosperfil/"+ usuarios.get(p).getFotoPerfil()).into(holder.fotoPerfilUsuarioAceptarImageView);}
        holder.nombreApellidosUsuarioAceptarTextView.setText(usuarios.get(p).getNombre() + " " + usuarios.get(p).getApellidos());
        holder.correoUsuarioAceptarTextView.setText(usuarios.get(p).getCorreoElectronico());
        holder.direccionUsuarioAceptarTextView.setText(usuarios.get(p).getDireccion());

        holder.idUsuarioCard = usuarios.get(p).getIdUsuario();
        holder.nombre = usuarios.get(p).getNombre();
        holder.apellidos = usuarios.get(p).getApellidos();
        holder.correo = usuarios.get(p).getCorreoElectronico();
        holder.direccion = usuarios.get(p).getDireccion();
        holder.fotoPerfil = usuarios.get(p).getFotoPerfil();


        holder.card_usuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PerfilUsuario.class);

                intent.putExtra("usuario", usuario);
                intent.putExtra("idUsuarioPerfil", usuarios.get(p).getIdUsuario());
                intent.putExtra("Administrador", "Administrador");
                intent.putExtra("IdComunidad", idComunidad);

                activity.startActivityForResult(intent, 2);
            }

        });
    }

    @Override
    public int getItemCount() {
        // +1 por la card de añadir coche
        return usuarios.size();
    }

    class UsuarioHolder extends RecyclerView.ViewHolder{

        TextView nombreApellidosUsuarioAceptarTextView, correoUsuarioAceptarTextView, direccionUsuarioAceptarTextView;
        ImageView fotoPerfilUsuarioAceptarImageView;
        CardView card_usuario;

        String idUsuarioCard, nombre, apellidos, fotoPerfil, correo, direccion;

        public UsuarioHolder(@NonNull View itemView) {
            super(itemView);
            nombreApellidosUsuarioAceptarTextView = itemView.findViewById(R.id.nombreApellidosUsuarioAceptarTextView);
            fotoPerfilUsuarioAceptarImageView = itemView.findViewById(R.id.fotoPerfilUsuarioAceptarImageView);
            correoUsuarioAceptarTextView = itemView.findViewById(R.id.correoUsuarioAceptarTextView);
            direccionUsuarioAceptarTextView = itemView.findViewById(R.id.direccionUsuarioAceptarTextView);
            card_usuario = itemView.findViewById(R.id.card_usuario);
        }
    }
}