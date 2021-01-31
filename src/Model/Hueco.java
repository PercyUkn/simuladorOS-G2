
package Model;

public class Hueco {
    private int capacidad;
    private int dirInicio;

    public Hueco() {
    }

    public Hueco(int capacidad, int direccionIncio) {
        this.capacidad = capacidad;
        dirInicio = direccionIncio;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }
    
    public void setDirInicio(int dirInicio) {
        this.dirInicio = dirInicio;
    }

    public int getDirInicio() {
        return dirInicio;
    }
    
    public int getDirFin(){
        return dirInicio + capacidad;
    }
}
