package GUI.Pridanie;

import Aplikacia.Prezenter;
import GUI.*;
import Objekty.Nehnutelnost;
import Objekty.Parcela;

import javax.swing.*;

public class Generovanie extends JPanel
{
    private JPanel panel;
    private JButton button_oknoHlavne;
    private JButton button_potvrd;
    private JComboBox combo_typ;
    private JTextField input_pocet;
    private JTextField input_faktorZmensenia;
    private JTextField input_supisneCislo;

    public Generovanie(Prezenter prezenter, GUI gui)
    {
        this.button_oknoHlavne.addActionListener(e -> gui.zobrazHlavneOkno());

        this.button_potvrd.addActionListener(e -> {
            try
            {
                String typString = String.valueOf(this.combo_typ.getSelectedItem());
                int pocetGenerovanych = Integer.parseInt(Generovanie.this.input_pocet.getText());
                int supisneCislo = Integer.parseInt(Generovanie.this.input_supisneCislo.getText());
                double faktorZmensenia = Double.parseDouble(Generovanie.this.input_faktorZmensenia.getText());

                if (faktorZmensenia < 1.0)
                {
                    JOptionPane.showMessageDialog(Generovanie.this, "Neplatná hodnota faktoru zmenšenia!");
                    return;
                }

                int realneVygenerovanych = -1;
                if (typString.equals("Parcely"))
                {
                    realneVygenerovanych = prezenter.generujParcely(pocetGenerovanych, faktorZmensenia);
                }
                else if (typString.equals("Nehnuteľnosti"))
                {
                    realneVygenerovanych = prezenter.generujNehnutelnosti(supisneCislo, pocetGenerovanych, faktorZmensenia);
                }

                JOptionPane.showMessageDialog(Generovanie.this, "Vygenerovaných bolo " + realneVygenerovanych +
                                                             "/" + pocetGenerovanych + " záznamov");
                gui.zobrazHlavneOkno();
            }
            catch (Exception ex)
            {
                JOptionPane.showMessageDialog(Generovanie.this, "Neplatné vstupy!");
            }
        });
    }

    public JPanel getJPanel()
    {
        return this.panel;
    }
}
