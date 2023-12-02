package GUI;

import Aplikacia.Prezenter;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HlavneOkno extends JPanel
{
    private JPanel panel;
    private JLabel text_blokovaciFaktorHlavnySuborParciel;
    private JLabel text_blokovaciFaktorPreplnujuciSuborParciel;
    private JLabel text_blokovaciFaktorHlavnySuborNehnutelnosti;
    private JLabel text_blokovaciFaktorPreplnujuciSuborNehnutelnosti;
    private JLabel text_minX;
    private JLabel text_minY;
    private JLabel text_maxX;
    private JLabel text_maxY;
    private JLabel text_maxUrovenParcely;
    private JLabel text_maxUrovenNehnutelnosti;
    private JButton button_resetuj;
    private JLabel text_pocetDhParcely;
    private JLabel text_pocetQsParcely;
    private JLabel text_pocetQsNehnutelnosti;
    private JLabel text_pocetDhNehnutelnosti;
    private JButton button_oknoGenerovanie;
    private JButton button_oknoPridanieParcela;
    private JButton button_oknoPridanieNehnutelnost;

    public HlavneOkno(Prezenter prezenter, GUI gui)
    {
        this.text_blokovaciFaktorHlavnySuborParciel.setText("Blokovací faktor hlavný súbor parciel: " + prezenter.getDhParcely().getSpravcaSuborov().getBlokovaciFaktorHlavnySubor());
        this.text_blokovaciFaktorPreplnujuciSuborParciel.setText("Blokovací faktor preplňujúci súbor parciel: " + prezenter.getDhParcely().getSpravcaSuborov().getBlokovaciFaktorPreplnujuciSubor());

        this.text_blokovaciFaktorHlavnySuborNehnutelnosti.setText("Blokovací faktor hlavný súbor nehnuteľností: " + prezenter.getDhNehnutelnosti().getSpravcaSuborov().getBlokovaciFaktorHlavnySubor());
        this.text_blokovaciFaktorPreplnujuciSuborNehnutelnosti.setText("Blokovací faktor preplňujúci súbor nehnuteľností: " + prezenter.getDhNehnutelnosti().getSpravcaSuborov().getBlokovaciFaktorPreplnujuciSubor());

        this.text_minX.setText("Min x: " + prezenter.getQsParcely().getRootQuad().getVlavoDoleX());
        this.text_minY.setText("Min y: " + prezenter.getQsParcely().getRootQuad().getVlavoDoleY());
        this.text_maxX.setText("Max x: " + prezenter.getQsParcely().getRootQuad().getVpravoHoreX());
        this.text_maxY.setText("Max y: " + prezenter.getQsParcely().getRootQuad().getVpravoHoreY());

        this.text_maxUrovenParcely.setText("Max úroveň (hĺbka) QS parciel: " + prezenter.getQsParcely().getMaxUroven());
        this.text_maxUrovenNehnutelnosti.setText("Max úroveň (hĺbka) QS nehnuteľností: " + prezenter.getQsNehnutelnosti().getMaxUroven());

        this.text_pocetDhParcely.setText("Počet elementov DH parcely: " + prezenter.getDhParcely().getPocetElementov());
        this.text_pocetQsParcely.setText("Počet elementov QS parcely: " + prezenter.getQsParcely().getPocetElementov());

        this.text_pocetDhNehnutelnosti.setText("Počet elementov DH nehnuteľnosti: " + prezenter.getDhNehnutelnosti().getPocetElementov());
        this.text_pocetQsNehnutelnosti.setText("Počet elementov QS nehnuteľnosti: " + prezenter.getQsNehnutelnosti().getPocetElementov());

        this.button_resetuj.addActionListener(e -> {
            prezenter.resetuj();
            gui.zobrazInicializacneOkno();
        });

        this.button_oknoGenerovanie.addActionListener(e -> gui.zobrazOknoGenerovanie());
        this.button_oknoPridanieParcela.addActionListener(e -> gui.zobrazOknoPridanieParcela());
        this.button_oknoPridanieNehnutelnost.addActionListener(e -> gui.zobrazOknoPridanieNehnutelnost());
    }

    public JPanel getJPanel()
    {
        return this.panel;
    }
}
