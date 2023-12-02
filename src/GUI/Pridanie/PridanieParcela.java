package GUI.Pridanie;

import Aplikacia.Prezenter;
import GUI.*;

import javax.swing.*;

public class PridanieParcela extends JPanel
{
    private JButton button_oknoHlavne;
    private JButton button_potvrd;
    private JPanel panel;
    private JTextField input_popis;
    private JTextField input_vlavoDoleX;
    private JTextField input_vlavoDoleY;
    private JTextField input_vpravoHoreX;
    private JTextField input_vpravoHoreY;

    public PridanieParcela(Prezenter prezenter, GUI gui)
    {
        this.button_oknoHlavne.addActionListener(e -> gui.zobrazHlavneOkno());

        this.button_potvrd.addActionListener(e ->
        {
            try
            {
                String popis = this.input_popis.getText();

                int vlavoDoleX = Integer.parseInt(this.input_vlavoDoleX.getText());
                int vlavoDoleY = Integer.parseInt(this.input_vlavoDoleY.getText());
                int vpravoHoreX = Integer.parseInt(this.input_vpravoHoreX.getText());
                int vpravoHoreY = Integer.parseInt(this.input_vpravoHoreY.getText());

                if (prezenter.skusVlozitParcelu(popis, vlavoDoleX, vlavoDoleY, vpravoHoreX, vpravoHoreY))
                {
                    gui.zobrazHlavneOkno();
                }
                else
                {
                    JOptionPane.showMessageDialog(PridanieParcela.this, "Parcelu s danými súradnicami nie je možné vložiť!");
                }
            }
            catch (Exception ex)
            {
                JOptionPane.showMessageDialog(PridanieParcela.this, "Neplatné vstupy!");
            }
        });
    }

    public JPanel getJPanel()
    {
        return this.panel;
    }
}
