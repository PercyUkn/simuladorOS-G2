
package Model;
import java.util.ArrayList;
import java.util.LinkedList;

public class RAM extends LinkedList<Object>{
    public static final int PRIMER_HUECO = 0, MEJOR_HUECO = 1, PEOR_HUECO = 2;
    private int capTotal;
    private int politica = PRIMER_HUECO;

    public RAM() {
        
    }
    
    public RAM(int capacidadTotal) {
        capTotal = capacidadTotal;
        agregarSO(800);
    }

    public boolean agregarProceso(Proceso p){
        switch(politica){
            case PRIMER_HUECO:
                for (Object o : this) {
                    if(o instanceof Hueco){
                        Hueco h = (Hueco)o;
                        if(h.getCapacidad()>p.getMemoria()){
                            p.setMemoriaInicio(h.getDirInicio());
                            h.setDirInicio(h.getDirInicio()+p.getMemoria()+1);
                            h.setCapacidad(h.getCapacidad()-p.getMemoria()-1);
                            add(indexOf(o), p);
                            return true;
                        }
                    }
                }
                break;
            case MEJOR_HUECO:
                ArrayList<Hueco> huecos = new ArrayList<>();
                for (Object o : this) {
                    if(o instanceof Hueco){
                        huecos.add((Hueco)o);
                    }
                }
                huecos.sort(new Comparador());
                for (Hueco h : huecos) {
                    if(h.getCapacidad()>p.getMemoria()){
                        p.setMemoriaInicio(h.getDirInicio());
                        h.setDirInicio(h.getDirInicio()+p.getMemoria()+1);
                        h.setCapacidad(h.getCapacidad()-p.getMemoria()-1);
                        add(indexOf(h), p);
                        return true;
                    }
                }
                break;
            case PEOR_HUECO:
                ArrayList<Hueco> huecos1 = new ArrayList<>();
                for (Object o : this) {
                    if(o instanceof Hueco){
                        huecos1.add((Hueco)o);
                    }
                }
                huecos1.sort(new Comparador().reversed());
                for (Hueco h : huecos1) {
                    if(h.getCapacidad()>p.getMemoria()){
                        p.setMemoriaInicio(h.getDirInicio());
                        h.setDirInicio(h.getDirInicio()+p.getMemoria()+1);
                        h.setCapacidad(h.getCapacidad()-p.getMemoria()-1);
                        add(indexOf(h), p);
                        return true;
                    }
                }
                break;
        }
        return false;
    }

    //Esto hace el merge de los huecos
    public boolean quitarProceso(Proceso p){
        int i = indexOf(p);
        if(i>-1){
            Hueco h1=null, h2=null;
            //Si existe el índice (i-1), o sea si es que tiene un nodo anterior
            if(i-1>-1 && (get(i-1) instanceof Hueco)){
                h1 = (Hueco)get(i-1);
            }
            if(i+1<size() && (get(i+1) instanceof Hueco)){
                h2 = (Hueco)get(i+1);
            }
            //Si hay un hueco a la izquierda y a la derecha
            if(h1!=null && h2!=null){
                add(i, new Hueco(p.getMemoria()+h1.getCapacidad()+2
                        +h2.getCapacidad(), h1.getDirInicio()));
                remove(h1);
                remove(h2);
                remove(p);
                return true;
            }

            //Si hay un hueco a la izquierda
            if(h1!=null){
                add(i, new Hueco(p.getMemoria()+h1.getCapacidad()+1, 
                        h1.getDirInicio()));
                remove(h1);
                remove(p);
                return true;
            }

            //Si hay un hueco a la derecha
            if(h2!=null){
                add(i, new Hueco(p.getMemoria()+h2.getCapacidad()+1, 
                        p.getMemoriaInicio()));
                remove(h2);
                remove(p);
                return true;
            }

            //No hay ningún hueco alrededor
            add(i, new Hueco(p.getMemoria(), p.getMemoriaInicio()));
            remove(p);
            return true;
        }
        return false;
    }
    
    //Métodos privados
    private void agregarSO(int memoria){
        add(new MemoriaSO(memoria));
        add(new Hueco(capTotal - memoria+1, memoria));
    }
    
    public void setCapTotal(int capTotal) {
        this.capTotal = capTotal;
        Object o = getLast();
        //Extiende el último hueco
        if(o instanceof Hueco){
            int dirI = ((Hueco)o).getDirInicio();
            remove(o);
            addLast(new Hueco(capTotal-dirI, dirI));
        }
    }

    public int getCapTotal() {
        return capTotal;
    }

    public void setPolitica(int politica) {
        this.politica = politica;
    }

    public int getPolitica() {
        return politica;
    }
    
}


