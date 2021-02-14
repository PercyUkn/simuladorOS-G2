
package Model;

import View.PanelInterrupcionTeclado;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class ColaES extends Cola{

    private List<Interrupcion> atendidos = new ArrayList<>();
    
    private static ColaES ces;
    
    private ColaES(){
    }
    
    public static ColaES getIntance(){
        if (ces == null)
        { ces = new ColaES();}
        return ces;
    }
    
    @Override
    //Cuándo usa este método es porque ya agrego a la cola de espera
    public void addLast(Proceso p) {
        super.addLast(p);
        p.setEstado(Proceso.BLOQUEADO);
        // Generar Dispositivo de ES que genera la interrupcion
        p.disp = (int)(Math.random()*5);
        if (p.disp==2){ // SI SE TRATA DEL TECLADO
            PanelInterrupcionTeclado tecla = new PanelInterrupcionTeclado(p);
            tecla.setVisible(true);
            tecla.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    super.keyPressed(e); //To change body of generated methods, choose Tools | Templates.
                    tecla.dispose();
                    System.out.println("Se presiono una tecla");
                    ColaES ces = ColaES.getIntance();
                   
                    
                    //SO y Planificador
                    SO so = SO.getInstance();
                    SO.Planificador planif = so.getPlanif();
                    SO.CPU cpu = so.getCpu();
                    ColaListos cl = planif.getCl();
                    long tiempoInicio = so.getTiempoInicio();
                    
                    if (planif.isApropiativa()){
                         if(ces.size()>0)
                            {
                                ces.remove(p);
                                ces.addAtendidos(p,tiempoInicio);
                                cl.addLast(p);
                            }
                    }
                    else {
                        if(ces.size()>0)
                        {
                             ces.remove(p);
                             ces.addAtendidos(p,tiempoInicio);
                             p.setEstado(Proceso.LISTO);
                             cpu.setActual(p);
                        }
                    }
                    //Vuelve a planificar todo
                    planif.actionPerformed(new ActionEvent(this, modCount, "reqIO"));
                }
                
            });
        }
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
    
     //Para gestionar interrupciones de FIN que no sean de teclado
        public Proceso getNextProcesoGestionable(){
            Proceso p = null;
            if (ces!=null){
                if (ces.size()>0){
                    for (int i=0; i<ces.size();i++){
                        //Da el siguiente que no sea Interrupción de teclado
                        if (ces.get(i).getDisp()!=2){
                            p=ces.get(i);
                            break;
                        }
                    }
                }
            
            }
           return p;
        }
}
