package GUI.Vyhladanie;

import Aplikacia.Prezenter;
import GUI.GUI;
import Objekty.Polygon;
import QuadStrom.Objekty.DummyNehnutelnost;
import QuadStrom.Objekty.DummyParcela;

import javax.swing.*;
import java.util.ArrayList;

public class VyhladanieObdlznik extends JPanel
{
    private JPanel panel;
    private JComboBox combo_typ;
    private JButton button_oknoHlavne;
    private JButton button_potvrd;
    private JTextField input_vlavoDoleX;
    private JTextField input_vlavoDoleY;
    private JTextField input_vpravoHoreX;
    private JTextField input_vpravoHoreY;

    public VyhladanieObdlznik(Prezenter prezenter, GUI gui)
    {
        this.button_oknoHlavne.addActionListener(e -> gui.zobrazHlavneOkno());
        this.button_potvrd.addActionListener(e -> {
            try
            {
                double vlavoDoleX =  Double.parseDouble(VyhladanieObdlznik.this.input_vlavoDoleX.getText());
                double vlavoDoleY =  Double.parseDouble(VyhladanieObdlznik.this.input_vlavoDoleY.getText());
                double vpravoHoreX = Double.parseDouble(VyhladanieObdlznik.this.input_vpravoHoreX.getText());
                double vpravoHoreY = Double.parseDouble(VyhladanieObdlznik.this.input_vpravoHoreY.getText());

                String typString = String.valueOf(VyhladanieObdlznik.this.combo_typ.getSelectedItem());
                if (typString.equals("Nehnuteľnosti"))
                {
                    ArrayList<DummyNehnutelnost> dummyNehnutelnosti = prezenter.vyhladajDummyNehnutelnosti(vlavoDoleX, vlavoDoleY, vpravoHoreX, vpravoHoreY);
                    gui.zobrazZoznamDummyNehnutelnosti(dummyNehnutelnosti);
                }
                else if (typString.equals("Parcely"))
                {
                    ArrayList<DummyParcela> dummyParcely = prezenter.vyhladajDummyParcely(vlavoDoleX, vlavoDoleY, vpravoHoreX, vpravoHoreY);
                    gui.zobrazZoznamDummyParciel(dummyParcely);
                }
                else
                {
                    ArrayList<Polygon> polygony = prezenter.vyhladajPolygony(vlavoDoleX, vlavoDoleY, vpravoHoreX, vpravoHoreY);
                    gui.zobrazZoznamPolygonov(polygony);
                }
            }
            catch (Exception ex)
            {
                JOptionPane.showMessageDialog(VyhladanieObdlznik.this, "Neplatné vstupy!");
            }
        });
    }

    public JPanel getJPanel()
    {
        return this.panel;
    }
}
