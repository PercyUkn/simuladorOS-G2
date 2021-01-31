package Model;

import View.PanelRAM;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;

public class SO implements ISimulador{
    public static int maxMemoria = 2000;
    public static int maxCantProcesos = 20;//nivel de multiprogramacion (n procesos como maximo en memoria)

    //Clase Interna
    CPU cpu;
    //Clase Interna
    Planificador planif;
    RAM ram;
    
    long tiempoInicio;
    long duracion;

    public SO() {
        cpu = new CPU();
        planif = new Planificador();
        ram = new RAM(maxMemoria);
    }

    public SO(int politica, boolean apropiativa, int asignacionMemoria) {
        cpu = new CPU();
        planif = new Planificador(politica, apropiativa);
        ram = new RAM(maxMemoria);
        ram.setPolitica(asignacionMemoria);
    }
    
    @Override
    public void iniciar(){
        cpu.iniciar();
        planif.iniciar();
        tiempoInicio = System.currentTimeMillis();
        System.out.println("Inició el Sistema Operativo");
    }
    
    @Override
    public void parar(){
        planif.parar();
        cpu.parar();
        System.out.println("Paró el Sistema Operativo");
    }

    @Override
    public void setDelay(int delay) {
        planif.setDelay(delay);
        cpu.setDelay(delay);
    }

    @Override
    public int getDelay() {
        return cpu.getDelay();
    }
    
    public void setPolitica(int p){
        planif.setPolitica(p);
    }
    
    public int getPolitica(){
        return planif.getPolitica();
    }
    
    public int getTipoAsignacionMemoria(){
        return ram.getPolitica();
    }
    
    public void setApropiativa(boolean a){
        planif.apropiativa = a;
    }
    
    public boolean isApropiativa(){
        return planif.apropiativa;
    }
    
    public void setQuantum(int q){
        planif.quantum = q;
    }
    
    public void cambiarDelay(int delay){
        cpu.setDelay(delay);
        planif.setDelay(delay);
    }
    
    public void cambiarCapMemoria(int m){
        maxMemoria = m;
        ram.setCapTotal(m);
    }
    
    public boolean crearNuevoProceso(Proceso p){
        return planif.agregarNuevo(p);
    }
    
    public void insertarNProcesos(int n){
        for (int i = 0; i < n; i++) {
            crearNuevoProceso(new Proceso());
        }
    }
    
    public boolean crearProcesoPersonalizado(int bursTime){
        return planif.agregarNuevo(new Proceso(bursTime));
    }
    
    public void setAsignacionMemoria(int i){
        ram.setPolitica(i);
    }
    
    class Planificador implements ActionListener, ISimulador{
        static final int FCFS = 0, SJF = 1, ROUNDROBIN = 2, POR_PRIORIDADES = 3;
        int quantum = 10;
        //cada 51 ms ejecute el método ActionPerformed
        private final Timer t = new Timer(51, this);
        private final ColaProcesos cp = new ColaProcesos();
        private final ColaES ces = new ColaES();
        private final ColaListos cl = new ColaListos();
        
        private int politica = FCFS;
        private boolean apropiativa = true;
        private int quantumRestante = quantum;

        Despachador dspch = new Despachador();

        public Planificador() {
        }
        
        public Planificador(int politica, boolean apropiativa){
            this.politica = politica;
            this.apropiativa = apropiativa;
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
        public void setDelay(int d){
            //Cambia la frecuencia en la que se ejecuta el método ActionPerformed
            t.setDelay(d);
        }

        @Override
        public int getDelay() {
            return t.getDelay();
        }
        
        public void setPolitica(int p){
            if(p>-1 && p<4)
                politica = p;
        }
        
        public float getTiempoEsperaProm(){
            int n = 0;
            float tProm = 0;
            long tTotal = 0;
            for (Proceso p : cp) {
                if(p.getEstado()==Proceso.FINALIZADO){
                    n++;
                    //Falta corregir, para que se vea reflejado el tiempo de espera real
                    tTotal += p.getTiempoEspera();
                }
            }
            if(n>0){
                tProm = (tTotal)/n;
            }
            return tProm;
        }
        
        public long getTiempoFinal(){
            if(cp.size()>0 && cp.getCantProcesosActivos()==0 && duracion<1){
                duracion = System.currentTimeMillis() - tiempoInicio;
            }
            return duracion;
        }
        
        public int getPolitica(){
            return politica;
        }
        
        public ColaProcesos getColaProcesos(){
            return cp;
        }

        public ColaES getColaES(){
            return ces;
        }

        public ColaListos getColaListos(){
            return cl;
        }

        //Se ejecuta cada 51 milisegundos
        @Override
        public void actionPerformed(ActionEvent ae) {
            ordenarListos();
            planificar();
            dspch.cambiarContexto();
            agregarSigListo();
        }

        //Largo plazo
        public boolean agregarNuevo(Proceso p){
            if(cp.getCantProcesosActivos()<maxCantProcesos){
                cp.addLast(p);
                return true;
            }
            return false;
        }

        //Mediano plazo
        public boolean agregarListo(Proceso p){
            //Carga el proceso en memoria
            // Solo si se existe memoria para el proceso
            if(ram.agregarProceso(p)){
                cl.addLast(p);
                return true;
            }
            return false;
        }

        //Escoge el siguiente a cargar en memoria en base
        // a la política de corto plazo de la
        // cola de procesos que están en el estado
        // nuevo
        public boolean agregarSigListo(){
            ArrayList<Proceso> nuevos = new ArrayList<>();
            for (Proceso proc : cp) {
                if(proc.getEstado()==Proceso.NUEVO){
                    nuevos.add(proc);
                }
            }
            //Escoge el siguiente a cargar en memoria en base
            // a la política de corto plazo
            if(politica!=ROUNDROBIN)
                nuevos.sort(Comparadores.getComparador(politica));
            if(nuevos.size()>0)
                return agregarListo(nuevos.get(0));
            return false;
        }

        //Corto plazo
        Proceso procesoSiguiente = null;

        private void ordenarListos(){
            if(politica!=ROUNDROBIN)
                cl.sort(Comparadores.getComparador(politica));
        }
        
        private void planificarNoApropiativa(){
            Proceso act = cpu.getActual();
            procesoSiguiente = null;
            switch (politica){
                case FCFS:
                    if(act==null && cl.size()>0){
                        Proceso pTemp = cl.getFirst();
                        if(pTemp.getEstado()!=Proceso.FINALIZADO)
                            procesoSiguiente = pTemp;
                        break;
                    }
                    break;
                case SJF:
                    if(act!=null && act.getEstado()!=Proceso.FINALIZADO)
                        break;
                    int grand = 1000;
                    Proceso pt = null;
                    //Obtiene el menor tiempo de ejecución restante del proceso
                    //De la cola de procesos (incluidos los procesos en el estado nuevo?)
                    // ¿En la cola de procesos también están los procesos bloqueados?
                    for (Proceso proc : cp) {
                        if(proc.getRestante()<grand && proc.getEstado()!=Proceso.FINALIZADO){
                            grand = proc.getRestante();
                            pt = proc;
                        }
                    }
                    if(pt!=null)
                        procesoSiguiente = pt;
                    break;
                case ROUNDROBIN:
                    if(quantumRestante<1){
                        if(cl.size()>0) {
                            procesoSiguiente = cl.getFirst();
                        }
                        quantumRestante = quantum;
                    }else{
                        if(act!=null && act.getEstado()!=Proceso.FINALIZADO){
                            if(act.getEstado()!=Proceso.BLOQUEADO)
                                quantumRestante--;
                            break;
                        }
                        // Si el proceso está bloqueado obtiene otro
                        if(cl.size()>0) {
                            procesoSiguiente = cl.getFirst();
                        }
                    }
                    break;
                case POR_PRIORIDADES:
                    if(act!=null && act.getEstado()!=Proceso.FINALIZADO)
                        break;
                    int priori = 1000;
                    Proceso pt2 = null;
                    for (Proceso proc : cp) {
                        if(proc.getPrioridad()<priori && proc.getEstado()!=Proceso.FINALIZADO){
                            priori = proc.getPrioridad();
                            pt2 = proc;
                        }
                    }
                    if(pt2!=null)
                        procesoSiguiente = pt2;
                    break;
            }
        }
        
        private void planificarApropiativa(){
            procesoSiguiente = null;
            Proceso act = cpu.getActual();
            //Si el proceso esta bloqueado
            if(act==null && cl.size()>0){
                procesoSiguiente = cl.getFirst();
            }
            if(politica==ROUNDROBIN){
                quantumRestante--;
                if(quantumRestante<0 && cl.size()>0){
                    procesoSiguiente = cl.getFirst();
                }
            }
        }
        
        private void planificar(){
            if(apropiativa)
                planificarApropiativa();
            else
                planificarNoApropiativa();
        }

        class Despachador {
            void cambiarContexto(){
                // O sea se ejecuta solo si el proceso
                // siguiente fue escogido por el scheduler
                // y el scheduler solo se ejecuta si el proceso
                // que se está ejecutando es nulo
                if(procesoSiguiente!=null){
                    Proceso p = cpu.getActual();
                    cpu.setActual(procesoSiguiente);
                    cl.remove(procesoSiguiente);
                    if(p != null){
                        switch (p.getEstado()){
                            case Proceso.EJECUTANDO:
                                cl.addLast(p);
                                break;
                            case Proceso.BLOQUEADO:
                                //Si da un bloqueado hace esto
                                ces.addLast(p);
                                break;
                            case Proceso.FINALIZADO:
                                cl.remove(p);
                                break;
                            default:
                                System.out.println("------");
                        }
                    }
                    quantumRestante = quantum;
                }
            }
        }

        public void gestionarInterrupcion(int tipo){
            switch(tipo){
                case Interrupciones.REQ_ES:
                    Proceso pa = cpu.getActual();
                    if(pa!=null && !ces.contains(pa)){
                        ces.addLast(pa);
                        if(apropiativa)
                            cpu.setActual(null);
                    }
                    actionPerformed(new ActionEvent(this, tipo, "reqIO"));
                    break;
                case Interrupciones.FIN_REQ_ES:
                    Proceso pt = null;
                    if(apropiativa){
                        if(ces.size()>0)
                            pt = ces.getFirst();
                        if(pt!=null){
                            ces.remove(pt);
                            //Pero no sería aquí también pt.setEstado(Proceso.LISTO)
                            cl.addLast(pt);
                        }
                    }else{
                        if(ces.size()>0)
                            pt = ces.getFirst();
                        if(pt!=null){
                            ces.remove(pt);
                            //Que vuelva a dónde se ejecutó
                            pt.setEstado(Proceso.LISTO);
                            cpu.setActual(pt);
                        }
                    }
                    actionPerformed(new ActionEvent(this, tipo, "reqIO"));
                    break;
            }
        }
    }
    
   
    class CPU implements ActionListener, ISimulador{
        Proceso pActual;
        //Cada 51ms, se ejecuta el método actionPerformed
        Timer t = new Timer(51, this);
        Interrupciones itr = new Interrupciones();
        
        //Datos
        long tiempoUso;
        long tiempoOcioso;
        long tCreacion;
        long tFinal;

        public CPU() {
            itr = new Interrupciones();
            tCreacion = System.currentTimeMillis();
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
        public void setDelay(int d){
            t.setDelay(d);
        }

        @Override
        public int getDelay() {
            return t.getDelay();
        }
        
        public void comunicarInterrupciones(){
            int inter1 = itr.generarIntES();
            int inter2 = itr.generarFinIntES();
            
            if(inter1 > 0)
                planif.gestionarInterrupcion(Interrupciones.REQ_ES);
            if(inter2 > 0)
                planif.gestionarInterrupcion(Interrupciones.FIN_REQ_ES);
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            if(pActual!=null){
                if(pActual.getEstado()!=Proceso.FINALIZADO && pActual.ejecutarSiguiente()){
                    tiempoUso++;
                }else{
                    tiempoOcioso++;
                }
                if(pActual.getEstado()==Proceso.FINALIZADO){
                    ram.quitarProceso(pActual);
                    pActual = null;
                }
                    
            }else{
                tiempoOcioso++;
            }
            comunicarInterrupciones();
        }

        public void setActual(Proceso p){
            pActual = p;
            if(p!=null && p.getEstado()!=Proceso.BLOQUEADO 
                    && p.getEstado()!=Proceso.FINALIZADO)
                p.setEstado(Proceso.EJECUTANDO);
        }

        public Proceso getActual(){
            return pActual;
        }

    }
    
    //Graficar Barras de Progreso de la Cola de Proceso
        
    public void graficarColaProcesos(JPanel jp, JTable tblEjec, JTable tblListos,
            JTable tblBloqueados, JTable tblFinal,JTable tblHistEjec, JTable tblHistBloqueados, JTable tlbListaProcesos, JTable tlbDisco){
        int MAX_ALTO = 120;
        jp.removeAll();
        for (int i = 0; i < planif.getColaProcesos().size(); i++) {
            //PanelSO - Sistema Operativo
            JProgressBar ProgresoProceso = new javax.swing.JProgressBar();
            ProgresoProceso.setOrientation(SwingConstants.VERTICAL);
            ProgresoProceso.setStringPainted(true);
            Proceso p = planif.getColaProcesos().get(planif.getColaProcesos().size()-1-i);
            ProgresoProceso.setValue(p.getProgreso());
            ProgresoProceso.setPreferredSize(new Dimension(14,(int)(p.getTamanio()*MAX_ALTO)));
            String est="";
            switch(p.getEstado()){
                case 0: est="Nuevo";
                        break;
                case 1: est="Listo";
                        break;
                case 2: est="Ejecutando";
                        break;
                case 3: est="Bloqueado";
                        break;
                case 4: est="Finalizado";
                        break;                 
            }
            ProgresoProceso.setToolTipText("PID: "+p.getPID()+" PC: "+p.getPC()+" Burst Time: "
                +p.getBurstTime()
                +" Memoria: "+p.getMemoria()
                +" Prioridad: "+p.getPrioridad()
                +" Estado: "+est);
            ProgresoProceso.setOpaque(true);
            //ProgresoProceso.setForeground(Color.red);
            switch(p.getEstado()){
                case Proceso.EJECUTANDO:
                    ProgresoProceso.setBackground(Color.GREEN);
                    break;
                case Proceso.LISTO:
                    ProgresoProceso.setBackground(Color.YELLOW);
                    break;
                case Proceso.BLOQUEADO:
                    ProgresoProceso.setBackground(Color.PINK);                    
                    break;
                case Proceso.FINALIZADO:                    
                    ProgresoProceso.setBackground(Color.BLUE);                    
                    if(p.isError())
                        ProgresoProceso.setBackground(Color.RED);
                    break;
            }
            jp.add(ProgresoProceso);
            jp.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }
        jp.repaint();
        actualizarTablaEjecutando(tblHistEjec,2);
        actualizarTablaEjecutando(tblEjec,1);
        actualizarTablaBloqueados(tblHistBloqueados,2);
        actualizarTablaBloqueados(tblBloqueados,1);
        actualizarTablaBloqueados(tlbDisco,3);
        actualizarTablaListos(tblListos);
        actualizarTablaFinalizados(tblFinal);
        actualizarListaProcesos(tlbListaProcesos);
    }
    public void actualizarTablaEjecutando(JTable tablaEjecutando,int modo){
        DefaultTableModel tabla=(DefaultTableModel) tablaEjecutando.getModel();
        if(modo==1){
            tabla.setRowCount(0);
            if(cpu.getActual()!=null){
                Object [] fila={cpu.getActual().getPID(),cpu.getActual().getPC(),
                ((Integer) cpu.getActual().getMemoria()).toString()+" MB",
                cpu.getActual().getPrioridad(),((Integer)cpu.getActual().getProgreso()).toString()+"%"};
                tabla.addRow(fila);
            }
        }
        //Histórico
        else if(modo==2){
            int numeroFilas = tabla.getRowCount();
            if(cpu.getActual()!=null){
                Object [] fila= {cpu.getActual().getPID(),cpu.getActual().getPC(),
                ((Integer) cpu.getActual().getMemoria()).toString()+" MB",
                cpu.getActual().getPrioridad(),((Integer)cpu.getActual().getProgreso()).toString()+"%"};
                if(numeroFilas==0){
                    tabla.addRow(fila);
                }
                else{
                    //Si la fila recientemente agregada es la fila que se va a agregar
                    // Impide que se duplique, y actualiza la información.
                    if(tabla.getValueAt(numeroFilas-1, 0)== fila[0]){
                        tabla.removeRow(numeroFilas-1);
                        tabla.addRow(fila);
                    }
                    else{
                        tabla.addRow(fila);
                    }
                }
            }
            
        }
    }  
        
    public void actualizarTablaListos(JTable tablaListos){
        DefaultTableModel tabla=(DefaultTableModel) tablaListos.getModel();
        // Elimina las entradas anteriores de la tabla
        // (Se hace un refresh)
        tabla.setRowCount(0);
        if(planif.getColaListos().size()>0){
            for (Proceso procListo : planif.getColaListos()) {
                Object [] fila= {procListo.getPID(),procListo.getPC(),
                    ((Integer) procListo.getMemoria()).toString()+" MB", 
                    procListo.getPrioridad(),((Integer)procListo.getProgreso()).toString()+"%"};
                // Agrega otra vez todos los procesos con el estado listo
                tabla.addRow(fila);
            }
        }
    }
    
    public void actualizarTablaBloqueados(JTable tablaBloqueados, int modo){
        DefaultTableModel tabla=(DefaultTableModel) tablaBloqueados.getModel();
        if(modo==1){
            // modo 1: Tabla de Bloqueados
            tabla.setRowCount(0);
            if(planif.getColaES().size()>0){
                for (Proceso procBloqueado: planif.getColaES()){
                    Object [] fila= {procBloqueado.getPID(),procBloqueado.getPC(),
                        ((Integer) procBloqueado.getMemoria()).toString()+" MB", 
                        procBloqueado.getPrioridad(),((Integer) procBloqueado.getProgreso()).toString()+"%",
                        planif.getColaES().getDispositivo(procBloqueado)};
                        tabla.addRow(fila);
                }
            }
        }
        //Histórico
        else if(modo==2){
            int numeroFilas = tabla.getRowCount();
            if(planif.getColaES().size()>0){
                for (Proceso procBloqueado: planif.getColaES()){
                    Object [] fila= {procBloqueado.getPID(),procBloqueado.getPC(),
                        ((Integer) procBloqueado.getMemoria()).toString()+" MB", 
                        procBloqueado.getPrioridad(),((Integer) procBloqueado.getProgreso()).toString()+"%",
                        planif.getColaES().getDispositivo(procBloqueado)};
                    if(numeroFilas==0){
                        tabla.addRow(fila);
                    }
                    else{
                        if(tabla.getValueAt(numeroFilas-1, 0)== fila[0]){
                            tabla.removeRow(numeroFilas-1);
                            tabla.addRow(fila);
                        }
                        else{
                            tabla.addRow(fila);
                        }
                    }   
                }
            }
        }
        // Para la nueva pestaña (Según Dispositivos)
        else if (modo==3){
            //Tabla Disco
            if (planif.getColaES().size()>0){
                //Tabla de bloqueados --> Mandar como parámetro la tabla correspondiente
                DefaultTableModel model = (DefaultTableModel) tablaBloqueados.getModel();
                model.setRowCount(0);
                List<Object []> procesosDisco = new ArrayList<>();
                int contador=0;
                for (Proceso procesoBloqueado : planif.getColaES()){
                    contador++;
                    if (planif.getColaES().getDispositivo(procesoBloqueado).equals("Disco")) {
                        Object [] fila = {contador,procesoBloqueado.getPID(), ((Integer) procesoBloqueado.getMemoria()).toString()+" MB"};
                        procesosDisco.add(fila);
                    }                    
                }
                for (Object [] fila: procesosDisco){
                    model.addRow(fila);
                }
            }          
        }
                
    }
    
    public void actualizarTablaFinalizados(JTable tablaFinalizados){
        DefaultTableModel tabla=(DefaultTableModel) tablaFinalizados.getModel();
        //Borra las entradas de la tabla
        tabla.setRowCount(0);
        for (Proceso procFinal: planif.getColaProcesos()){
            //Agrega los errores
            if(procFinal.getEstado()==Proceso.FINALIZADO && procFinal.isError()){
                Object [] fila= {procFinal.getPID(),procFinal.getPCError(),
                    ((Integer) procFinal.getMemoria()).toString()+" MB", 
                    };
                    tabla.addRow(fila);
            }
            //Agrega los procesos normales
            if(procFinal.getEstado()==Proceso.FINALIZADO && procFinal.isError()==false){
                Object [] fila= {procFinal.getPID(),"",
                    ((Integer) procFinal.getMemoria()).toString()+" MB", 
                    };
                    tabla.addRow(fila);
            }
        }
    }
    
    public void actualizarListaProcesos(JTable tlbListaProcesos){
        DefaultTableModel tabla=(DefaultTableModel) tlbListaProcesos.getModel();
        //Borra las entradas de la tabla
        tabla.setRowCount(0);
        for (Proceso procFinal: planif.getColaProcesos()){
            //Agrega los errores
            if(procFinal.getEstado()!=Proceso.FINALIZADO){
                Object [] fila= {procFinal.getPID(),
                    ((Integer) procFinal.getMemoria()).toString()+" MB", procFinal.getPrioridad(), procFinal.getEstadoName(procFinal.getEstado())
                    };
                    tabla.addRow(fila);
            }
        }
    }
    
    public void generarEstadisticas(JLabel tUso, JLabel tOcio,
            JLabel tEsperaProm, JLabel tDuracion){
        tUso.setText(Long.toString(cpu.tiempoUso));
        tOcio.setText(Long.toString(cpu.tiempoOcioso));
        tEsperaProm.setText(" "+planif.getTiempoEsperaProm());
        tDuracion.setText(Long.toString(planif.getTiempoFinal()));
    }
        
    public void graficarEspacioMemoria(JPanel jp){
        jp.removeAll();
        for (Object o : ram) {
            if(o instanceof MemoriaSO){
                jp.add(PanelRAM.createSO(((MemoriaSO) o).getCapacidad()));
            }else if(o instanceof Hueco){
                Hueco h = (Hueco)o;
                jp.add(PanelRAM.createHueco(h.getDirInicio(), 
                        h.getDirFin()));
            }else if(o instanceof Proceso){
                Proceso p = (Proceso)o;
                jp.add(new PanelRAM(p));
            }
        }
    }
    
}


