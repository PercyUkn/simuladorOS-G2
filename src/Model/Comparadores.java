
package Model;

import java.util.Comparator;

public class Comparadores {
    private static final ComparadorFCFS CFCFS = new ComparadorFCFS();
    private static final ComparadorSJF CSJF = new ComparadorSJF();
    private static final ComparadorPrioridades CPRIORI = new ComparadorPrioridades();
    
    public static Comparator<Proceso> getComparador(int politica){
        switch(politica){
            case SO.Planificador.FCFS:
                return CFCFS;
            case SO.Planificador.SJF:
                return CSJF;
            case SO.Planificador.ROUNDROBIN:
                return CFCFS;
            case SO.Planificador.POR_PRIORIDADES:
                return CPRIORI;
        }
        return null;
    }
    
    static class ComparadorFCFS implements Comparator<Proceso>{
        @Override
        public int compare(Proceso t, Proceso t1) {
            if(t.getPID()==t1.getPID())
                return 0;
            if(t.getPID()>t1.getPID())
                return 1;
            return -1;
        }
    }
    
    static class ComparadorSJF implements Comparator<Proceso>{
        @Override
        public int compare(Proceso t, Proceso t1) {
            if(t.getRestante()==t1.getRestante())
                return 0;
            if(t.getRestante()>t1.getRestante())
                return 1;
            return -1;
        }
    }
    
    static class ComparadorPrioridades implements Comparator<Proceso>{
        @Override
        public int compare(Proceso t, Proceso t1) {
            if(t.getPrioridad()==t1.getPrioridad())
                return 0;
            if(t.getPrioridad()>t1.getPrioridad())
                return 1;
            return -1;
        }
    }  
}

