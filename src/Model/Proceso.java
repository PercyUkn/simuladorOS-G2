package Model;

public class Proceso {
    //estados propios del proceso
    public static final int NUEVO = 0, LISTO = 1, EJECUTANDO = 2, BLOQUEADO = 3, FINALIZADO = 4;
    public static int numeroProcesos;
    //0-5: numero random de dispositivo, donde 0:Impresora, 1: Disco, ... 
    //Detallado en la funcion getDispositivoES de la clase ColaES 
    public int disp;
    static final int MAXIMO_BURSTTIME = 100;
    static final int MINIMO_BURSTTIME=40;
    private boolean error = false;
    private int PCError = 0;
    //momento en el que se crea el proceso
    private long tiempoCreacion;
    //momento en el que el proceso finaliza
    private long tiempoFinalizacion;
    //momento en el que el proceso finaliza
    private long tiempoPrimeraAtencion = -1;
    //Para el cálculo del tiempo de espera en la cola de listos
    private long tiempoInicioEspera;
    private long tiempoFinEspera;
    private long tiempoEsperaTotal=-1;
    //cantidad de memoria que se le asigna al inicio de la creacion
    private int memoriaInicio;
    
    private PCB pcb;

    //Constructores de la clase Proceso
    //cuando no se coloca el parametro "burst time"
    // Genera valores aleatorios
    public Proceso() {
        pcb = new PCB(numeroProcesos);
        generarError();
        numeroProcesos++;
        //hallamos el tiempo actual del sistema en milisengundos
        tiempoCreacion = System.currentTimeMillis();
        tiempoInicioEspera=tiempoCreacion;
    }
    //cuando se coloca el parametro burst time 
    public Proceso(int bt) {
        pcb = new PCB(numeroProcesos, bt);
        //generar el probable error de creacion del proceso
        generarError();
        numeroProcesos++;
        //hallamos el tiempo actual del sistema en milisengundos
        tiempoCreacion = System.currentTimeMillis();
        tiempoInicioEspera=tiempoCreacion;
    }
    private void generarError(){
        double rand = Math.random();
        //condicion: La cantidad de procesos errados será aleatoria y será el 0.5%
        if(rand<=0.005){
            error = true;
            //Qué el error suceda en algún momento aleatorio del BurstTime
            PCError = (int)(pcb.burstTime*Math.random()+1);
        }
    }
    public boolean ejecutarSiguiente(){
        if(pcb.PC<pcb.burstTime){
            // Si llego al momento del PCError
            if(error && pcb.PC == PCError){
                tiempoFinalizacion = System.currentTimeMillis();
                pcb.estado = FINALIZADO;
                return true;
            }
            //Si se está ejecutando que lo siga haciéndolo
            if(pcb.estado == EJECUTANDO){
                pcb.PC++;
                return true;
            }
        }else{
            //La primera vez que entra aquí es la última (PCB==burstTime)
            if(pcb.estado!=FINALIZADO){
                tiempoFinalizacion = System.currentTimeMillis();
                pcb.estado = FINALIZADO;
                return true;
            }
        }
        return false;
    }
    
    //Encapsulamiento de atributos de la clase Proceso
    public int getMemoria(){
        return pcb.memoria;
    }
    
    public long getTiempoEspera(){
        return (tiempoFinalizacion-tiempoCreacion);
    }
    
    public int getPID(){
        return pcb.PID;
    }
    
    public int getEstado(){
        return pcb.estado;
    }
    
    public int getProgreso(){
        if(pcb.PC<pcb.burstTime)
            return (int)(100*pcb.PC/pcb.burstTime);
        return 100;
    }
    
    public double getTamanio(){
        return (1.0*pcb.burstTime/MAXIMO_BURSTTIME);
    }
    
    public int getPC(){
        return pcb.PC;
    }
    
    public int getPCError(){
        return PCError;
    }
    
    public int getPrioridad(){
        return pcb.prioridad;
    }
    
    public int getBurstTime(){
        return pcb.burstTime;
    }
    
    public int getRestante(){
        return (pcb.burstTime - pcb.PC);
    }

    public void setMemoriaInicio(int memoriaInicio) {
        this.memoriaInicio = memoriaInicio;
    }
    
    public int getMemoriaInicio() {
        return memoriaInicio;
    }
    public boolean setEstado(int estado){
        if(estado<5){
            pcb.estado = estado;
            return true;
        }
        return false;
    }
    public boolean isError(){
        return error;
    }

    public long getTiempoPrimeraAtencion() {
        return tiempoPrimeraAtencion;
    }

    public void setTiempoPrimeraAtencion(long tiempoPrimeraAtencion) {
        this.tiempoPrimeraAtencion = tiempoPrimeraAtencion;
    }

    public long getTiempoInicioEspera() {
        return tiempoInicioEspera;
    }

    public void setTiempoInicioEspera(long tiempoInicioEspera) {
        this.tiempoInicioEspera = tiempoInicioEspera;
    }

    public long getTiempoFinEspera() {
        return tiempoFinEspera;
    }

    public void setTiempoFinEspera(long tiempoFinEspera) {
        this.tiempoFinEspera = tiempoFinEspera;
    }

    public long getTiempoEsperaTotal() {
        return tiempoEsperaTotal;
    }

    public void setTiempoEsperaTotal(long tiempoEsperaTotal) {
        this.tiempoEsperaTotal = tiempoEsperaTotal;
    }
    
    
    
    public String getEstadoName(int estado){
        switch (estado){
            case 0: return "Nuevo";
            case 1: return "Listo";
            case 2: return "Ejecutando";
            case 3: return "Bloqueado";
            case 4: return "Finalizado";
            default: return null;
        }
    }

    public long getTiempoCreacion() {
        return tiempoCreacion;
    }
    
    
}
