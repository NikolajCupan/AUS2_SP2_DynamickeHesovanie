package GUI.Vyhladanie;

import Aplikacia.Prezenter;
import GUI.GUI;
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
    private JButton button_oknoEdituj;
    private JButton button_vymaz;

    private int zvoleneID;
    private Class zvolenyTyp;

    public Vyhladanie(Prezenter prezenter, GUI gui)
    {
        this.button_oknoHlavne.addActionListener(e -> gui.zobrazHlavneOkno());

        this.button_potvrd.addActionListener(e -> {
            try
            {
                String typString = String.valueOf(this.combo_typ.getSelectedItem());
                int ID = Integer.parseInt(input_ID.getText());
                Vyhladanie.this.zvoleneID = ID;

                Class typ;
                if (typString.equals("Parcela"))
                {
                    Vyhladanie.this.zvolenyTyp = Parcela.class;
                    typ = Parcela.class;
                }
                else
                {
                    Vyhladanie.this.zvolenyTyp = Nehnutelnost.class;
                    typ = Nehnutelnost.class;
                }

                String vysledok = prezenter.vyhladajToString(ID, typ);
                this.text_vystup.setText(vysledok);

                if (vysledok.equals("Parcela s daným identifikačným číslom neexistuje!") ||
                    vysledok.equals("Nehnuteľnosť s daným identifikačným číslom neexistuje!"))
                {
                    this.button_oknoEdituj.setEnabled(false);
                    this.button_vymaz.setEnabled(false);
                }
                else
                {
                    this.button_oknoEdituj.setEnabled(true);
                    this.button_vymaz.setEnabled(true);
                }
            }
            catch (Exception exception)
            {
                JOptionPane.showMessageDialog(Vyhladanie.this, "Neplatné vstupy!");
            }
        });

        this.button_oknoEdituj.addActionListener(e ->
        {
            if (this.zvolenyTyp.equals(Parcela.class))
            {
                gui.zobrazOknoEditovanieParcela(this.zvoleneID);
            }
            else if (this.zvolenyTyp.equals(Nehnutelnost.class))
            {
                gui.zobrazOknoEditovanieNehnutelnost(this.zvoleneID);
            }
        });

        this.button_vymaz.addActionListener(e ->
        {
            boolean uspesneVymazane = false;

            if (this.zvolenyTyp.equals(Parcela.class))
            {
                uspesneVymazane = prezenter.skusVymazatParcelu(this.zvoleneID);
            }
            else if (this.zvolenyTyp.equals(Nehnutelnost.class))
            {
                uspesneVymazane = prezenter.skusVymazatNehnutelnost(this.zvoleneID);
            }

            if (uspesneVymazane)
            {
                gui.zobrazHlavneOkno();
            }
            else
            {
                JOptionPane.showMessageDialog(Vyhladanie.this, "Pri vymazávaní záznamu nastala chyba!");
            }
        });
    }

    public JPanel getJPanel()
    {
        return this.panel;
    }
}
