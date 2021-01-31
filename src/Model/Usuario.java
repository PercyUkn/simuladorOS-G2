
package Model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

public class Usuario implements ActionListener, ISimulador{
    Timer t = new Timer(51, this);
    int maxDelay = 2000;
    SO refSO;
    
    public Usuario(SO so) {
        refSO = so;
    }
    
    @Override
    public void iniciar(){
        t.start();
    }
    
    @Override
    public void parar(){
        t.stop();
    }

    @Override
    public void setDelay(int delay) {
        maxDelay = 100+100*delay;
    }

    @Override
    public int getDelay() {
        return (int)maxDelay;
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        //Se genera un proceso aleatorio cada "delay" milisegundos
        if(!refSO.crearNuevoProceso(new Proceso()))
            // Se disminuye porque cuándo
            // se llama al método crear proceso
            // aumenta Proceso.numeroProcesos
            Proceso.numeroProcesos--;
        
        genTiempoAleatorio();
    }
    
    private void genTiempoAleatorio(){
        int nDelay = (int)(1000+Math.random()*maxDelay);
        t.setDelay(nDelay);
    }
}
