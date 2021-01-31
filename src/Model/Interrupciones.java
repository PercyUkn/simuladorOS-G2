
package Model;

public class Interrupciones {
    static final int NINGUNO = 0, REQ_ES = 1, FIN_REQ_ES = 2;
    int reqES = 0;
    static final double PROB_ES = 0.02, PROB_FIN_ES = 0.1;

    public Interrupciones() {
    }
    
    public int generarIntES(){
        Double random = Math.random();
        if(random<PROB_ES){
            reqES++;
            return REQ_ES; 
        }
        return NINGUNO;
    }
    
    public int generarFinIntES(){
        Double random = Math.random();
        if(reqES > 0 && random<PROB_FIN_ES){
            reqES--;
            return FIN_REQ_ES;
        }
        return NINGUNO;
    }
}
