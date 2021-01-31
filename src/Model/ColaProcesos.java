package Model;

public class ColaProcesos extends Cola{
    @Override
    public void addLast(Proceso p) {
        super.addLast(p);
        p.setEstado(Proceso.NUEVO);
    }
    
    public int getCantProcesosActivos(){
        int n = 0;
        if(size()>0){
            for (Proceso p : this) {
                if(p.getEstado()!=Proceso.FINALIZADO)
                    n++;
            }
        }
        return n;
    }
}
