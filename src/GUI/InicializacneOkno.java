package GUI;

import Aplikacia.Prezenter;

import javax.swing.*;

public class InicializacneOkno extends JPanel
{
    private JPanel panel;
    private JTextField input_blokovaciFaktorHlavnySuborParciel;
    private JTextField input_blokovaciFaktorPreplnujuciSuborParciel;
    private JTextField input_blokovaciFaktorHlavnySuborNehnutelnosti;
    private JTextField input_blokovaciFaktorPreplnujuciSuborNehnutelnosti;
    private JTextField input_minX;
    private JTextField input_minY;
    private JTextField input_maxX;
    private JTextField input_maxY;
    private JTextField input_maxUrovenParcely;
    private JTextField input_maxUrovenNehnutelnosti;
    private JLabel input_max;
    private JButton button_potvrd;


    public InicializacneOkno(Prezenter prezenter, GUI gui)
    {
        this.button_potvrd.addActionListener(e -> {
            try
            {
                int blokovaciFaktorHlavnySuborParciel = Integer.parseInt(this.input_blokovaciFaktorHlavnySuborParciel.getText());
                int blokovaciFaktorPreplnujuciSuborParciel = Integer.parseInt(this.input_blokovaciFaktorPreplnujuciSuborParciel.getText());

                int blokovaciFaktorHlavnySuborNehnutelnosti = Integer.parseInt(this.input_blokovaciFaktorHlavnySuborNehnutelnosti.getText());
                int blokovaciFaktorPreplnujuciSuborNehnutelnosti = Integer.parseInt(this.input_blokovaciFaktorPreplnujuciSuborNehnutelnosti.getText());

                double vlavoDoleX = Double.parseDouble(this.input_minX.getText());
                double vlavoDoleY = Double.parseDouble(this.input_minY.getText());
                double vpravoHoreX = Double.parseDouble(this.input_maxX.getText());
                double vpravoHoreY = Double.parseDouble(this.input_maxY.getText());

                int maxUrovenParcely = Integer.parseInt(this.input_maxUrovenParcely.getText());
                int maxUrovenNehnutelnosti = Integer.parseInt(this.input_maxUrovenNehnutelnosti.getText());

                prezenter.inicializujNove(blokovaciFaktorHlavnySuborParciel, blokovaciFaktorPreplnujuciSuborParciel,
                                          blokovaciFaktorHlavnySuborNehnutelnosti, blokovaciFaktorPreplnujuciSuborNehnutelnosti,
                                          vlavoDoleX, vlavoDoleY, vpravoHoreX, vpravoHoreY, maxUrovenParcely, maxUrovenNehnutelnosti);

                gui.zobrazHlavneOkno();
            }
            catch (Exception ex)
            {
                JOptionPane.showMessageDialog(InicializacneOkno.this, "Neplatné vstupy!");
            }
        });
    }

    public JPanel getJPanel()
    {
        return this.panel;
    }
}
