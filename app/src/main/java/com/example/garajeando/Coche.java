package com.example.garajeando;

public class Coche {

    String idCoche, propietario, nombrePropietario, apellidosPropietario, matricula, marca, modelo, transmision, combustible, descripcion;
    Integer plazas, puertas;
    Boolean aireAcondicionado, bluetooth, gps;

    public Coche(String idCoche, String propietario, String nombrePropietario, String apellidosPropietario, String matricula, String marca, String modelo, String transmision, String combustible, String descripcion, Integer plazas, Integer puertas, Boolean aireAcondicionado, Boolean bluetooth, Boolean gps) {
        this.idCoche=idCoche;
        this.propietario=propietario;
        this.nombrePropietario=nombrePropietario;
        this.apellidosPropietario=apellidosPropietario;
        this.matricula=matricula;
        this.marca=marca;
        this.modelo=modelo;
        this.transmision=transmision;
        this.combustible=combustible;
        this.descripcion=descripcion;
        this.plazas=plazas;
        this.puertas=puertas;
        this.aireAcondicionado=aireAcondicionado;
        this.bluetooth=bluetooth;
        this.gps=gps;
    }

    public void setIdCoche(String idCoche) {
        this.idCoche = idCoche;
    }

    public void setPropietario(String propietario) {
        this.propietario = propietario;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public void setTransmision(String transmision) {
        this.transmision = transmision;
    }

    public void setCombustible(String combustible) {
        this.combustible = combustible;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setPlazas(Integer plazas) {
        this.plazas = plazas;
    }

    public void setPuertas(Integer puertas) {
        this.puertas = puertas;
    }

    public void setAireAcondicionado(Boolean aireAcondicionado) {
        this.aireAcondicionado = aireAcondicionado;
    }

    public String getNombrePropietario() {
        return nombrePropietario;
    }

    public void setNombrePropietario(String nombrePropietario) {
        this.nombrePropietario = nombrePropietario;
    }

    public void setBluetooth(Boolean bluetooth) {
        this.bluetooth = bluetooth;
    }

    public void setGps(Boolean gps) {
        this.gps = gps;
    }

    public String getIdCoche() {
        return idCoche;
    }

    public String getPropietario() {
        return propietario;
    }

    public String getMatricula() {
        return matricula;
    }

    public String getMarca() {
        return marca;
    }

    public String getModelo() {
        return modelo;
    }

    public String getTransmision() {
        return transmision;
    }

    public String getCombustible() {
        return combustible;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Integer getPlazas() {
        return plazas;
    }

    public Integer getPuertas() {
        return puertas;
    }

    public Boolean getAireAcondicionado() {
        return aireAcondicionado;
    }

    public Boolean getBluetooth() {
        return bluetooth;
    }

    public Boolean getGps() {
        return gps;
    }

    public String getApellidosPropietario() {
        return apellidosPropietario;
    }

    public void setApellidosPropietario(String apellidosPropietario) {
        this.apellidosPropietario = apellidosPropietario;
    }
}
