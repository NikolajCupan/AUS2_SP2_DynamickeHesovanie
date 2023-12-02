package GUI;

import Aplikacia.Prezenter;
import GUI.Pridanie.PridanieNehnutelnost;
import Objekty.Nehnutelnost;
import Objekty.Parcela;

import javax.swing.*;

public class Vyhladanie extends JPanel
{
    private JPanel panel;
    private JComboBox combo_typ;
    private JTextField input_ID;
    private JButton button_oknoHlavne;
    private JButton button_potvrd;
    private JTextArea text_vystup;

    public Vyhladanie(Prezenter prezenter, GUI gui)
    {
        this.button_oknoHlavne.addActionListener(e -> gui.zobrazHlavneOkno());

        this.button_potvrd.addActionListener(e -> {
            try
            {
                String typString = String.valueOf(this.combo_typ.getSelectedItem());
                int ID = Integer.parseInt(input_ID.getText());

                Class typ;
                if (typString.equals("Parcela"))
                {
                    typ = Parcela.class;
                }
                else
                {
                    typ = Nehnutelnost.class;
                }

                String vysledok = prezenter.vyhladaj(ID, typ);
                this.text_vystup.setText(vysledok);
            }
            catch (Exception exception)
            {
                JOptionPane.showMessageDialog(Vyhladanie.this, "Neplatné vstupy!");
            }
        });
    }

    public JPanel getJPanel()
    {
        return this.panel;
    }
}
