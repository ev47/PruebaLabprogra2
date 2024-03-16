/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package prueba;

/**
 *
 * @author edgva
 */
public class NodoPlay {
    private String ruta;
    private String nombre = "";
    private String artista = "";
    private String tipo =" ";
    private boolean reproduciendo; 
    private boolean pausada; 
    private boolean detenida;
    private NodoPlay siguiente;

    public NodoPlay(String ruta,String nombre,String artista,String tipo) {
        this.ruta = ruta;
        this.reproduciendo = false;
        this.pausada = false;
        this.detenida = true;
        this.nombre = nombre;
        this.artista= artista;
        this.tipo = tipo;
        this.siguiente = null;
    }

    public String getRuta() {
        return ruta;
    }

    public String getNombre() {
        return nombre;
    }

    public String getArtista() {
        return artista;
    }

    public String getTipo() {
        return tipo;
    }
      
    public NodoPlay getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(NodoPlay siguiente) {
        this.siguiente = siguiente;
    }

    public void reproducir() {
        reproduciendo = true;
        pausada = false;
        detenida = false;
    }
    public void pausar() {
        reproduciendo = false;
        pausada = true;
        detenida = false;
    }

    public void detener() {
        reproduciendo = false;
        pausada = false;
        detenida = true;
    }

    @Override
    public String toString() {
        return "La cancion {" + "es: " + nombre + ", artista=" + artista + ", tipo=" + tipo +'}';
    }
         
}
