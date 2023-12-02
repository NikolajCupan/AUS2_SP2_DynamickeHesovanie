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

        this.button_resetuj.addActionListener(e -> {
            prezenter.resetuj();
            gui.zobrazInicializacneOkno();
        });
    }

    public JPanel getJPanel()
    {
        return this.panel;
    }
}
