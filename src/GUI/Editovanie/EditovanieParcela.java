package GUI.Editovanie;

import GUI.*;
import Aplikacia.Prezenter;
import Objekty.Parcela;

import javax.swing.*;

public class EditovanieParcela extends JPanel
{
    private JPanel panel;
    private JLabel text_parcelaID;
    private JTextField input_popis;
    private JTextField input_vlavoDoleX;
    private JTextField input_vpravoHoreX;
    private JTextField input_vpravoHoreY;
    private JTextField input_vlavoDoleY;
    private JButton button_oknoHlavne;
    private JButton button_potvrd;

    public EditovanieParcela(Prezenter prezenter, GUI gui, int parcelaID)
    {
        Parcela parcela = prezenter.vyhladajParcelu(parcelaID);
        if (parcela == null)
        {
            gui.zobrazHlavneOkno();
        }

        this.text_parcelaID.setText("Identifikačné číslo parcely: " + parcela.getParcelaID());
        this.input_popis.setText(parcela.getPopis());
        this.input_vlavoDoleX.setText("" + parcela.getVlavoDoleX());
        this.input_vlavoDoleY.setText("" + parcela.getVlavoDoleY());
        this.input_vpravoHoreX.setText("" + parcela.getVpravoHoreX());
        this.input_vpravoHoreY.setText("" + parcela.getVpravoHoreY());

        this.button_oknoHlavne.addActionListener(e -> gui.zobrazHlavneOkno());

        this.button_potvrd.addActionListener(e -> {
            try
            {
                String novyPopis = this.input_popis.getText();

                double noveVlavoDoleX = Double.parseDouble(this.input_vlavoDoleX.getText());
                double noveVlavoDoleY = Double.parseDouble(this.input_vlavoDoleY.getText());
                double noveVpravoHoreX = Double.parseDouble(this.input_vpravoHoreX.getText());
                double noveVpravoHoreY = Double.parseDouble(this.input_vpravoHoreY.getText());

                if (noveVlavoDoleX != parcela.getVlavoDoleX() || noveVlavoDoleY != parcela.getVlavoDoleY() ||
                    noveVpravoHoreX != parcela.getVpravoHoreX() || noveVpravoHoreY != parcela.getVpravoHoreY())
                {
                    // Menia sa aj suradnice
                    boolean aktualizovane = prezenter.aktualizujParcelu(parcelaID, novyPopis, noveVlavoDoleX, noveVlavoDoleY,
                                                                        noveVpravoHoreX, noveVpravoHoreY);

                    if (!aktualizovane)
                    {
                        JOptionPane.showMessageDialog(EditovanieParcela.this, "Editácia parcely bola neúspešná, pravdepobne sa prekrýva s príliš veľkým počtom nehnuteľností!");
                    }
                    else
                    {
                        gui.zobrazHlavneOkno();
                    }
                }
                else
                {
                    // Meni sa iba popis
                    boolean aktualizovane = prezenter.aktualizujParcelu(parcelaID, novyPopis);

                    if (!aktualizovane)
                    {
                        JOptionPane.showMessageDialog(EditovanieParcela.this, "Editácia parcely bola neúspešná!");
                    }
                    else
                    {
                        gui.zobrazHlavneOkno();
                    }
                }
            }
            catch (Exception exception)
            {
                JOptionPane.showMessageDialog(EditovanieParcela.this, "Neplatné vstupy!");
            }
        });
    }

    public JPanel getJPanel()
    {
        return this.panel;
    }
}
