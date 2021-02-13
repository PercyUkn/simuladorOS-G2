
package Model;

import java.util.ArrayList;
import java.util.List;

public class ColaES extends Cola{

    private List<Interrupcion> atendidos = new ArrayList<>();
    
    @Override
    public void addLast(Proceso p) {
        super.addLast(p);
        p.setEstado(Proceso.BLOQUEADO);
        // Generar Dispositivo de ES que genera la interrupcion
        p.disp = (int)(Math.random()*5);
    }
    
    public int getMemoriaUsada(){
        int totalMemoriaES = 0;
        //Leer cada memoria ocupada por cada proceso
        for (Proceso p : this) {
            totalMemoriaES += p.getMemoria();
        }
        return totalMemoriaES;
    }
    
    public String getDispositivo(Proceso p){
        String dispositivoES="";
        switch(p.disp){
                case 0:
                    dispositivoES="Impresora";
                    break;
                case 1:
                    dispositivoES="Disco";
                    break;
                case 2:
                    dispositivoES="Teclado";
                    break;
                case 3:
                    dispositivoES="Mouse";
                    break;
                case 4:
                    dispositivoES="USB";
                    break;
        }
        return dispositivoES;
    }

    public void addAtendidos(Proceso p, long tiempoInicio){
        this.atendidos.add(new Interrupcion(p, tiempoInicio));
    }

    public List<Interrupcion> getAtendidos() {
        return atendidos;
    }
    
}
