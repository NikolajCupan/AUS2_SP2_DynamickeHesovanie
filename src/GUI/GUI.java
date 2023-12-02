package GUI;
import Aplikacia.Prezenter;

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
