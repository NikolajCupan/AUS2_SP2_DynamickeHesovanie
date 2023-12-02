package GUI;
import Aplikacia.Prezenter;
import GUI.Pridanie.Generovanie;
import GUI.Pridanie.PridanieNehnutelnost;
import GUI.Pridanie.PridanieParcela;

import javax.swing.*;

public class GUI extends JFrame
{
    private final Prezenter prezenter;
    private JPanel panel;

    public GUI(Prezenter prezenter)
    {
        this.prezenter = prezenter;
        boolean obnovene = this.prezenter.skusObnovit();

        setTitle("Aplikácia - Nikolaj Cupan");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(750, 750);
        setLocationRelativeTo(null);
        setVisible(true);

        if (obnovene)
        {
            this.zobrazHlavneOkno();
        }
        else
        {
            this.zobrazInicializacneOkno();
        }

        this.addWindowListener(new java.awt.event.WindowAdapter()
        {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent)
            {
                GUI.this.prezenter.uloz();
            }
        });
    }

    public void zobrazOknoVyhladanie()
    {
        Vyhladanie vyhladanie = new Vyhladanie(this.prezenter, this);
        this.zmenObsah(vyhladanie.getJPanel());
    }

    public void zobrazOknoGenerovanie()
    {
        Generovanie generovanie = new Generovanie(this.prezenter, this);
        this.zmenObsah(generovanie.getJPanel());
    }

    public void zobrazOknoPridanieParcela()
    {
        PridanieParcela pridanieParcela = new PridanieParcela(this.prezenter, this);
        this.zmenObsah(pridanieParcela.getJPanel());
    }

    public void zobrazOknoPridanieNehnutelnost()
    {
        PridanieNehnutelnost pridanieNehnutelnost = new PridanieNehnutelnost(this.prezenter, this);
        this.zmenObsah(pridanieNehnutelnost.getJPanel());
    }

    public void zobrazInicializacneOkno()
    {
        InicializacneOkno inicializacneOkno = new InicializacneOkno(this.prezenter, this);
        this.zmenObsah(inicializacneOkno.getJPanel());
    }

    public void zobrazHlavneOkno()
    {
        HlavneOkno hlavneOkno = new HlavneOkno(this.prezenter, this);
        this.zmenObsah(hlavneOkno.getJPanel());
    }

    private void zmenObsah(JPanel obsah)
    {
        setContentPane(obsah);
        revalidate();
        repaint();
    }

    public void uloz()
    {
        this.prezenter.uloz();
    }
}
