package View;

import Model.Proceso;
import java.awt.Color;
import java.awt.Dimension;

public final class PanelRAM extends javax.swing.JPanel {
    
    public PanelRAM() {
        initComponents();
    }
    
    public PanelRAM(Proceso p) {
        initComponents();
        jLabel1.setText("PID "+p.getPID()+" ("+p.getMemoria()
                +"MB)");
        setDirInicial(p.getMemoriaInicio());
        setDirFinal(p.getMemoriaInicio() + p.getMemoria());
        setColorFondo(Color.CYAN);
    }
    
    public static PanelRAM createHueco(int dirInicial, int dirFinal){
        PanelRAM e = new PanelRAM();
        e.setDirInicial(dirInicial);
        e.setDirFinal(dirFinal);
        e.jLabel1.setText("Hueco "+(dirFinal-dirInicial)+"MB");
        e.setColorFondo(Color.lightGray);
        return e;
    }
    
    public static PanelRAM createSO(int memoria){
        PanelRAM e = new PanelRAM();
        e.setDirInicial(0);
        e.setDirFinal(memoria);
        e.jLabel1.setText("MEMORIA DEL SO: "+memoria+"MB");
        e.setColorFondo(Color.PINK);
        e.setPreferredSize(new Dimension(100, 100));
        return e;
    }
    
    //El espacio de memoria del SO
    public PanelRAM(String texto, int dirInicial, int dirFinal) {
        initComponents();
        jLabel1.setText(texto);
        setBackground(Color.green);
        jPanel1.setBackground(Color.green);
        setDirInicial(dirInicial);
        setDirFinal(dirFinal);
    }
    
    private void setColorFondo(Color c){
        setBackground(c);
        jPanel1.setBackground(c);
    }
    
    public void setDirInicial(int dirInicial){
        jlDirInicial.setText(Integer.toString(dirInicial));
    }
    
    public void setDirFinal(int dirFinal){
        jlDirFinal.setText(Integer.toString(dirFinal));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jlDirFinal = new javax.swing.JLabel();
        jlDirInicial = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        setLayout(new java.awt.BorderLayout());

        jlDirFinal.setFont(new java.awt.Font("Segoe UI Symbol", 0, 11)); // NOI18N
        jlDirFinal.setText("finMemoria");
        add(jlDirFinal, java.awt.BorderLayout.SOUTH);

        jlDirInicial.setFont(new java.awt.Font("Segoe UI Symbol", 0, 11)); // NOI18N
        jlDirInicial.setText("incioMemoria");
        add(jlDirInicial, java.awt.BorderLayout.PAGE_START);

        jPanel1.setLayout(new java.awt.BorderLayout());

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("ID: ");
        jPanel1.add(jLabel1, java.awt.BorderLayout.CENTER);

        add(jPanel1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel jlDirFinal;
    private javax.swing.JLabel jlDirInicial;
    // End of variables declaration//GEN-END:variables
}
