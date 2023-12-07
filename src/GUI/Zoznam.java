package GUI;

import Aplikacia.Prezenter;
import GUI.Vyhladanie.Vyhladanie;
import Objekty.Nehnutelnost;
import Objekty.Parcela;
import QuadStrom.Objekty.DummyNehnutelnost;
import QuadStrom.Objekty.DummyParcela;
import Rozhrania.IPolygon;

import javax.swing.*;
import java.util.ArrayList;

public class Zoznam<T extends IPolygon> extends JPanel
{
    private JPanel panel;
    private JButton button_detail;
    private JButton button_oknoHlavne;
    private JList<T> list;
    private final DefaultListModel<T> model;
    private JTextArea detail;

    public Zoznam(Prezenter prezenter, GUI gui, ArrayList<T> zoznam)
    {
        this.model = new DefaultListModel<>();
        this.list.setModel(this.model);

        for (T element : zoznam)
        {
            this.model.addElement(element);
        }

        this.button_oknoHlavne.addActionListener(e -> gui.zobrazHlavneOkno());

        this.button_detail.addActionListener(e -> {
            T element = Zoznam.this.list.getSelectedValue();
            if (element == null)
            {
                return;
            }

            String vysledok = "--------------------------------------------------------------\n";
            vysledok       += "--------------- Dynamické hešovanie ---------------\n";
            vysledok       += "--------------------------------------------------------------\n\n";

            if (element instanceof DummyParcela dummyParcela)
            {
                int parcelaID = dummyParcela.getParcelaID();
                vysledok += prezenter.vyhladajToString(parcelaID, Parcela.class);
            }
            else if (element instanceof DummyNehnutelnost dummyNehnutelnost)
            {
                int nehnutelnostID = dummyNehnutelnost.getNehnutelnostID();
                vysledok += prezenter.vyhladajToString(nehnutelnostID, Nehnutelnost.class);
            }

            vysledok += "\n--------------------------------------------------------\n";
            vysledok += "------------------- Quad strom -------------------\n";
            vysledok += "--------------------------------------------------------\n\n";

            vysledok += element + "\n";

            vysledok += "\n\nPrekrýva sa s:\n\n";
            if (element instanceof DummyParcela dummyParcela)
            {
                ArrayList<DummyNehnutelnost> prekryvDummyNehnutelnosti = new ArrayList<>();
                prekryvDummyNehnutelnosti.addAll(prezenter.vyhladajDummyNehnutelnosti(element.getVlavoDoleX(), element.getVlavoDoleY(),
                                                                                 element.getVpravoHoreX(), element.getVpravoHoreY()));

                for (DummyNehnutelnost dummyNehnutelnost : prekryvDummyNehnutelnosti)
                {
                    vysledok += dummyNehnutelnost + "\n";
                }
            }
            else if (element instanceof DummyNehnutelnost dummyNehnutelnost)
            {
                ArrayList<DummyParcela> prekryvDummyParcely = new ArrayList<>();
                prekryvDummyParcely.addAll(prezenter.vyhladajDummyParcely(element.getVlavoDoleX(), element.getVlavoDoleY(),
                                                                          element.getVpravoHoreX(), element.getVpravoHoreY()));

                for (DummyParcela dummyParcela : prekryvDummyParcely)
                {
                    vysledok += dummyParcela + "\n";
                }
            }

            this.detail.setText(vysledok);
        });
    }

    public JPanel getJPanel()
    {
        return this.panel;
    }
}
