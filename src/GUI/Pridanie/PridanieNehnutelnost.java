package GUI.Pridanie;

import Aplikacia.Prezenter;
import GUI.*;

import javax.swing.*;

public class PridanieNehnutelnost extends JPanel
{
    private JPanel panel;
    private JButton button_oknoHlavne;
    private JButton button_potvrd;
    private JTextField input_popis;
    private JTextField input_supisneCislo;
    private JTextField input_vlavoDoleX;
    private JTextField input_vlavoDoleY;
    private JTextField input_vpravoHoreX;
    private JTextField input_vpravoHoreY;

    public PridanieNehnutelnost(Prezenter prezenter, GUI gui)
    {
        this.button_oknoHlavne.addActionListener(e -> gui.zobrazHlavneOkno());

        this.button_potvrd.addActionListener(e -> {
            try
            {
                String popis = this.input_popis.getText();
                int supisneCislo = Integer.parseInt(this.input_supisneCislo.getText());

                int vlavoDoleX = Integer.parseInt(this.input_vlavoDoleX.getText());
                int vlavoDoleY = Integer.parseInt(this.input_vlavoDoleY.getText());
                int vpravoHoreX = Integer.parseInt(this.input_vpravoHoreX.getText());
                int vpravoHoreY = Integer.parseInt(this.input_vpravoHoreY.getText());

                if (prezenter.skusVlozitNehnutelnost(supisneCislo, popis, vlavoDoleX, vlavoDoleY, vpravoHoreX, vpravoHoreY))
                {
                    gui.zobrazHlavneOkno();
                }
                else
                {
                    JOptionPane.showMessageDialog(PridanieNehnutelnost.this, "Nehnuteľnosť s danými súradnicami nie je možné vložiť!");
                }
            }
            catch (Exception ex)
            {
                JOptionPane.showMessageDialog(PridanieNehnutelnost.this, "Neplatné vstupy!");
            }
        });
    }

    public JPanel getJPanel()
    {
        return this.panel;
    }
}
