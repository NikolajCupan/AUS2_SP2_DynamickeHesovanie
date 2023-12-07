package GUI;
import Aplikacia.Prezenter;
import GUI.Editovanie.EditovanieNehnutelnost;
import GUI.Editovanie.EditovanieParcela;
import GUI.Pridanie.Generovanie;
import GUI.Pridanie.PridanieNehnutelnost;
import GUI.Pridanie.PridanieParcela;
import GUI.Vyhladanie.Vyhladanie;
import GUI.Vyhladanie.VyhladanieObdlznik;
import GUI.Vyhladanie.VyhladanieSuradnica;
import Objekty.Parcela;
import Objekty.Polygon;
import QuadStrom.Objekty.DummyNehnutelnost;
import QuadStrom.Objekty.DummyParcela;

import javax.swing.*;
import java.util.ArrayList;

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
                boolean ulozene = GUI.this.prezenter.uloz();

                if (ulozene)
                {
                    JOptionPane.showMessageDialog(GUI.this, "Súbory boli úspešné uložené!");
                }
            }
        });
    }

    public void zobrazOknoDebug()
    {
        Debug debug = new Debug(this.prezenter, this);
        this.zmenObsah(debug.getJPanel());
    }

    public void zobrazOknoEditovanieParcela(int parcelaID)
    {
        EditovanieParcela editovanieParcela = new EditovanieParcela(this.prezenter, this, parcelaID);
        this.zmenObsah(editovanieParcela.getJPanel());
    }

    public void zobrazOknoEditovanieNehnutelnost(int nehnutelnostID)
    {
        EditovanieNehnutelnost editovanieNehnutelnost = new EditovanieNehnutelnost(this.prezenter, this, nehnutelnostID);
        this.zmenObsah(editovanieNehnutelnost.getJPanel());
    }

    public void zobrazOknoVyhladanie()
    {
        Vyhladanie vyhladanie = new Vyhladanie(this.prezenter, this);
        this.zmenObsah(vyhladanie.getJPanel());
    }

    public void zobrazVyhladavanieSuradnica()
    {
        VyhladanieSuradnica vyhladanieSuradnica = new VyhladanieSuradnica(this.prezenter, this);
        this.zmenObsah(vyhladanieSuradnica.getJPanel());
    }

    public void zobrazVyhladavanieObdlznik()
    {
        VyhladanieObdlznik vyhladanieObdlznik = new VyhladanieObdlznik(this.prezenter, this);
        this.zmenObsah(vyhladanieObdlznik.getJPanel());
    }

    public void zobrazZoznamDummyParciel(ArrayList<DummyParcela> dummyParcely)
    {
        Zoznam<DummyParcela> zoznam = new Zoznam<DummyParcela>(this.prezenter, this, dummyParcely);
        this.zmenObsah(zoznam.getJPanel());
    }

    public void zobrazZoznamDummyNehnutelnosti(ArrayList<DummyNehnutelnost> dummyNehnutelnosti)
    {
        Zoznam<DummyNehnutelnost> zoznam = new Zoznam<DummyNehnutelnost>(this.prezenter, this, dummyNehnutelnosti);
        this.zmenObsah(zoznam.getJPanel());
    }

    public void zobrazZoznamPolygonov(ArrayList<Polygon> polygony)
    {
        Zoznam<Polygon> zoznam = new Zoznam<Polygon>(this.prezenter, this, polygony);
        this.zmenObsah(zoznam.getJPanel());
    }

    public void zobrazVsetkyParcely()
    {
        ArrayList<DummyParcela> parcely = new ArrayList<>(this.prezenter.getVsetkyParcely());
        Zoznam<DummyParcela> zoznam = new Zoznam<DummyParcela>(this.prezenter, this, parcely);
        this.zmenObsah(zoznam.getJPanel());
    }

    public void zobrazVsetkyNehnutelnosti()
    {
        ArrayList<DummyNehnutelnost> polygony = new ArrayList<>(this.prezenter.getVsetkyNehnutelnosti());
        Zoznam<DummyNehnutelnost> zoznam = new Zoznam<DummyNehnutelnost>(this.prezenter, this, polygony);
        this.zmenObsah(zoznam.getJPanel());
    }

    public void zobrazVsetkyPolygony()
    {
        ArrayList<Polygon> polygony = new ArrayList<>(this.prezenter.getVsetkyPolygony());
        Zoznam<Polygon> zoznam = new Zoznam<Polygon>(this.prezenter, this, polygony);
        this.zmenObsah(zoznam.getJPanel());
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
