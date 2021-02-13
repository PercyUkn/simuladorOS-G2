/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

/**
 *
 * @author Usuario
 */
// Usado solamente cuándo se atiende un req. de I/O
public class Interrupcion {
    private final float tiempoSimulacion;
    private final int PID;
    private final int codigoInterrupcion;
    private String descripcion;
    
    public Interrupcion(Proceso p, long tiempoInicio){
        ColaES ces = new ColaES();
        this.tiempoSimulacion=System.currentTimeMillis()-tiempoInicio;
        this.PID=p.getPID();
        this.codigoInterrupcion=p.getDisp();
        this.descripcion="Interrupción por ";
        if (!ces.getDispositivo(p).equals("")){
            this.descripcion = descripcion.concat(ces.getDispositivo(p));
        }
        //else {descripcion.concat("Timer");}       
    }    

    public float getTiempoSimulacion() {
        return tiempoSimulacion;
    }

    public int getPID() {
        return PID;
    }

    public int getCodigoInterrupcion() {
        return codigoInterrupcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
    
    
    
    
}
