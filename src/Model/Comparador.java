
package Model;

import java.util.Comparator;

public class Comparador implements Comparator<Hueco>{
    @Override
    public int compare(Hueco t, Hueco t1) {
        if(t.getCapacidad()==t1.getCapacidad())
            return 0;
        if(t.getCapacidad()>t1.getCapacidad())
            return 1;
        return -1;
    }
}
