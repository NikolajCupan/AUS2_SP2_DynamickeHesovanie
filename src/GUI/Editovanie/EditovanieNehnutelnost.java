package GUI.Editovanie;

import GUI.*;
import Aplikacia.Prezenter;
import Objekty.Nehnutelnost;

import javax.swing.*;

public class EditovanieNehnutelnost extends JPanel
{
    private JPanel panel;
    private JLabel text_nehnutelnostID;
    private JTextField input_vlavoDoleX;
    private JTextField input_vlavoDoleY;
    private JTextField input_vpravoHoreX;
    private JTextField input_vpravoHoreY;
    private JButton button_oknoHlavne;
    private JButton button_potvrd;
    private JTextField input_supisneCislo;
    private JTextField input_popis;

    public EditovanieNehnutelnost(Prezenter prezenter, GUI gui, int nehnutelnostID)
    {
        Nehnutelnost nehnutelnost = prezenter.vyhladajNehnutelnost(nehnutelnostID);
        if (nehnutelnost == null)
        {
            gui.zobrazHlavneOkno();
        }

        this.text_nehnutelnostID.setText("Identifikačné číslo nehnuteľnosti: " + nehnutelnost.getNehnutelnostID());
        this.input_popis.setText(nehnutelnost.getPopis());
        this.input_supisneCislo.setText("" + nehnutelnost.getSupisneCislo());
        this.input_vlavoDoleX.setText("" + nehnutelnost.getVlavoDoleX());
        this.input_vlavoDoleY.setText("" + nehnutelnost.getVlavoDoleY());
        this.input_vpravoHoreX.setText("" + nehnutelnost.getVpravoHoreX());
        this.input_vpravoHoreY.setText("" + nehnutelnost.getVpravoHoreY());

        this.button_oknoHlavne.addActionListener(e -> gui.zobrazHlavneOkno());

        this.button_potvrd.addActionListener(e -> {
            try
            {
                String novyPopis = this.input_popis.getText();
                int noveSupisneCislo = Integer.parseInt(this.input_supisneCislo.getText());

                double noveVlavoDoleX = Double.parseDouble(this.input_vlavoDoleX.getText());
                double noveVlavoDoleY = Double.parseDouble(this.input_vlavoDoleY.getText());
                double noveVpravoHoreX = Double.parseDouble(this.input_vpravoHoreX.getText());
                double noveVpravoHoreY = Double.parseDouble(this.input_vpravoHoreY.getText());

                if (noveVlavoDoleX != nehnutelnost.getVlavoDoleX() || noveVlavoDoleY != nehnutelnost.getVlavoDoleY() ||
                   noveVpravoHoreX != nehnutelnost.getVpravoHoreX() || noveVpravoHoreY != nehnutelnost.getVpravoHoreY())
                {
                    // Menia sa aj suradnice
                    boolean aktualizovane = prezenter.aktualizujNehnutelnost(nehnutelnostID, noveSupisneCislo, novyPopis,
                                                                             noveVlavoDoleX, noveVlavoDoleY, noveVpravoHoreX, noveVpravoHoreY);

                    if (!aktualizovane)
                    {
                        JOptionPane.showMessageDialog(EditovanieNehnutelnost.this, "Editácia nehnuteľnosti bola neúspešná, pravdepobne sa prekrýva s príliš veľkým počtom parciel!");
                    }
                    else
                    {
                        gui.zobrazHlavneOkno();
                    }
                }
                else
                {
                    // Meni sa iba supisne cislo a/alebo popis
                    boolean aktualizovane = prezenter.aktualizujNehnutelnost(nehnutelnostID, noveSupisneCislo, novyPopis);

                    if (!aktualizovane)
                    {
                        JOptionPane.showMessageDialog(EditovanieNehnutelnost.this, "Editácia nehnuteľnosti bola neúspešná!");
                    }
                    else
                    {
                        gui.zobrazHlavneOkno();
                    }
                }
            }
            catch (Exception exception)
            {
                JOptionPane.showMessageDialog(EditovanieNehnutelnost.this, "Neplatné vstupy!");
            }
        });
    }

    public JPanel getJPanel()
    {
        return this.panel;
    }
}
