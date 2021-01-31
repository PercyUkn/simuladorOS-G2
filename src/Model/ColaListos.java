
package Model;

public class ColaListos extends Cola{
    @Override
    public void addLast(Proceso p) {
        super.addLast(p);
        p.setEstado(Proceso.LISTO);
    }

    public int getMemoriaUsada(){
        int totalMemoriaListos = 0;
        for (int i = 0; i < size(); i++) {
            totalMemoriaListos+=get(i).getMemoria();
        }
        return totalMemoriaListos;
    }
}
