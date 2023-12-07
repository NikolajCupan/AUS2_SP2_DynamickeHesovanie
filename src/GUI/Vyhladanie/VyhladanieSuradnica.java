package GUI.Vyhladanie;

import Aplikacia.Prezenter;
import GUI.GUI;
import Objekty.Polygon;
import QuadStrom.Objekty.DummyNehnutelnost;
import QuadStrom.Objekty.DummyParcela;

import javax.swing.*;
import java.util.ArrayList;

public class VyhladanieSuradnica extends JPanel
{
    private JPanel panel;
    private JComboBox combo_typ;
    private JTextField input_x;
    private JTextField input_y;
    private JButton button_oknoHlavne;
    private JButton button_vyhladaj;

    public VyhladanieSuradnica(Prezenter prezenter, GUI gui)
    {
        this.button_oknoHlavne.addActionListener(e -> gui.zobrazHlavneOkno());

        this.button_vyhladaj.addActionListener(e -> {
            try
            {
                double x = Double.parseDouble(VyhladanieSuradnica.this.input_x.getText());
                double y = Double.parseDouble(VyhladanieSuradnica.this.input_y.getText());

                String typString = String.valueOf(VyhladanieSuradnica.this.combo_typ.getSelectedItem());
                if (typString.equals("Nehnuteľnosti"))
                {
                    ArrayList<DummyNehnutelnost> dummyNehnutelnosti = prezenter.vyhladajDummyNehnutelnosti(x, y);
                    gui.zobrazZoznamDummyNehnutelnosti(dummyNehnutelnosti);
                }
                else if (typString.equals("Parcely"))
                {
                    ArrayList<DummyParcela> dummyParcely = prezenter.vyhladajDummyParcely(x, y);
                    gui.zobrazZoznamDummyParciel(dummyParcely);
                }
                else
                {
                    ArrayList<Polygon> polygony = prezenter.vyhladajPolygony(x, y);
                    gui.zobrazZoznamPolygonov(polygony);
                }
            }
            catch (Exception ex)
            {
                JOptionPane.showMessageDialog(VyhladanieSuradnica.this, "Neplatné vstupy!");
            }
        });
    }

    public JPanel getJPanel()
    {
        return this.panel;
    }
}
