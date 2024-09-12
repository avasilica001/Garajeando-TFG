package com.example.garajeando;

public class Comunidad {

    private String idComunidad, nombre, codInvitacion, rol;

    public Comunidad (String idComunidad, String nombre, String codInvitacion, String Rol){
        this.idComunidad=idComunidad;
        this.nombre=nombre;
        this.codInvitacion=codInvitacion;
        this.rol=rol;
    }

    public String getIdComunidad() {
        return idComunidad;
    }

    public void setIdComunidad(String idComunidad) {
        this.idComunidad = idComunidad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        nombre = nombre;
    }

    public String getCodInvitacion() {
        return codInvitacion;
    }

    public void setCodInvitacion(String codInvitacion) {
        this.codInvitacion = codInvitacion;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}
