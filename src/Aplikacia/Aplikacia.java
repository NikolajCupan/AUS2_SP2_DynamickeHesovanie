package Aplikacia;

import Hesovanie.DynamickeHesovanie;
import Objekty.Nehnutelnost;
import Objekty.Parcela;
import Objekty.Suradnica;
import QuadStrom.Objekty.DummyNehnutelnost;
import QuadStrom.Objekty.DummyParcela;
import QuadStrom.QuadStrom;

import java.util.ArrayList;

public class Aplikacia
{
    private DynamickeHesovanie<Parcela> dhParcely;
    private DynamickeHesovanie<Nehnutelnost> dhNehnutelnosti;

    private QuadStrom<DummyParcela> qsParcely;
    private QuadStrom<DummyNehnutelnost> qsNehnutelnosti;

    // Pocitadla identifikacnych cisel
    private int curParcelaID;
    private int curNehnutelnostID;

    public Aplikacia()
    {
        this.dhParcely = null;
        this.dhNehnutelnosti = null;

        this.qsParcely = null;
        this.qsNehnutelnosti = null;

        this.curParcelaID = 1;
        this.curNehnutelnostID = 1;
    }

    // V pripade ak sa zacina s prazdnymi subormi
    public void inicializuj(int blokovaciFaktorHlavnySubor, int blokovaciFaktorPreplnujuciSubor, String nazovHlavnySubor, String nazovPreplnujuciSubor,
                            double vlavoDoleX, double vlavoDoleY, double vpravoHoreX, double vpravoHoreY, int maxUroven)
    {
        this.dhParcely = new DynamickeHesovanie<>(blokovaciFaktorHlavnySubor, blokovaciFaktorPreplnujuciSubor,
                                  "PARCELY_" + nazovHlavnySubor, "PARCELY_" + nazovPreplnujuciSubor, Parcela.class);
        this.dhNehnutelnosti = new DynamickeHesovanie<>(blokovaciFaktorHlavnySubor, blokovaciFaktorPreplnujuciSubor,
                                        "NEHNUTELNOSTI_" + nazovHlavnySubor, "NEHNUTELNOSTI_" + nazovPreplnujuciSubor, Nehnutelnost.class);

        this.qsParcely = new QuadStrom<DummyParcela>(vlavoDoleX, vlavoDoleY, vpravoHoreX, vpravoHoreY, maxUroven);
        this.qsNehnutelnosti = new QuadStrom<DummyNehnutelnost>(vlavoDoleX, vlavoDoleY, vpravoHoreX, vpravoHoreY, maxUroven);
    }

    public void resetuj()
    {
        this.dhParcely.vymazSubory();
        this.dhNehnutelnosti.vymazSubory();
    }

    public boolean vlozParcelu(String popis, double vlavoDoleX, double vlavoDoleY, double vpravoHoreX, double vpravoHoreY)
    {
        this.skontrolujVstupySuradnice(vlavoDoleX, vlavoDoleY, vpravoHoreX, vpravoHoreY);

        // Vstupy su korektne
        Parcela novaParcela = new Parcela(this.curParcelaID, popis, new Suradnica(vlavoDoleX, vlavoDoleY), new Suradnica(vpravoHoreX, vpravoHoreY));
        ArrayList<DummyNehnutelnost> novaParcelaPrekryv = this.qsNehnutelnosti.vyhladaj(vlavoDoleX, vlavoDoleY, vpravoHoreX, vpravoHoreY);

        if (novaParcelaPrekryv.size() >= Parcela.getMaxPocetReferencii())
        {
            // Parcelu nemozno pridat, nakolko sa prekryva
            // s prilis velkym poctom nehnutelnosti
            return false;
        }

        for (DummyNehnutelnost dummyNehnutelnost : novaParcelaPrekryv)
        {
            Nehnutelnost nehnutelnost = new Nehnutelnost(dummyNehnutelnost);
            ArrayList<DummyParcela> prekryvNehnutelnosti = this.qsParcely.vyhladaj(nehnutelnost.getVlavoDoleX(), nehnutelnost.getVlavoDoleY(),
                                                                                   nehnutelnost.getVpravoHoreX(), nehnutelnost.getVpravoHoreY());

            // Ak ktorakolvek nehnutelnost ma plny zoznam referencii,
            // tak pridanie nie je mozne uskutocnit
            if (prekryvNehnutelnosti.size() >= Nehnutelnost.getMaxPocetReferencii())
            {
                return false;
            }
        }

        // Pridanie je mozne uskutocnit
        this.qsParcely.vloz(new DummyParcela(this.curParcelaID, new Suradnica(vlavoDoleX, vlavoDoleY), new Suradnica(vpravoHoreX, vpravoHoreY)));

        // Napln zoznam referencii
        for (DummyNehnutelnost dummyNehnutelnost : novaParcelaPrekryv)
        {
            Nehnutelnost nehnutelnost = new Nehnutelnost(dummyNehnutelnost);
            novaParcela.skusPridatNehnutelnost(nehnutelnost);
        }
        this.dhParcely.vloz(novaParcela);

        // Rovnako je nutne upravit aj vsetky nehnutelnosti, s ktorymi sa nova
        // parcela prekryva (je nutne nastavit novu referenciu)
        for (DummyNehnutelnost dummyNehnutelnost : novaParcelaPrekryv)
        {
            Nehnutelnost nehnutelnost = this.dhNehnutelnosti.vymaz(new Nehnutelnost(dummyNehnutelnost));
            nehnutelnost.skusPridatParcelu(novaParcela);
            this.dhNehnutelnosti.vloz(nehnutelnost);
        }

        this.curParcelaID++;
        return true;
    }

    public boolean vlozNehnutelnost(int supisneCislo, String popis, double vlavoDoleX, double vlavoDoleY, double vpravoHoreX, double vpravoHoreY)
    {
        this.skontrolujVstupySuradnice(vlavoDoleX, vlavoDoleY, vpravoHoreX, vpravoHoreY);

        // Vstupy su korektne
        Nehnutelnost novaNehnutelnost = new Nehnutelnost(this.curNehnutelnostID, supisneCislo, popis, new Suradnica(vlavoDoleX, vlavoDoleY), new Suradnica(vpravoHoreX, vpravoHoreY));
        ArrayList<DummyParcela> novaNehnutelnostPrekryv = this.qsParcely.vyhladaj(vlavoDoleX, vlavoDoleY, vpravoHoreX, vpravoHoreY);

        if (novaNehnutelnostPrekryv.size() >= Nehnutelnost.getMaxPocetReferencii())
        {
            // Nehnutelnost nemozno pridat, nakolko sa prekryva
            // s prilis velkym poctom parciel
            return false;
        }

        for (DummyParcela dummyParcela : novaNehnutelnostPrekryv)
        {
            Parcela parcela = new Parcela(dummyParcela);
            ArrayList<DummyNehnutelnost> prekryvParcely = this.qsNehnutelnosti.vyhladaj(parcela.getVlavoDoleX(), parcela.getVlavoDoleY(),
                                                                                        parcela.getVpravoHoreX(), parcela.getVpravoHoreY());

            // Ak ktorakolvek parcela ma plny zoznam referencii,
            // tak pridanie nie je mozne uskutocnit
            if (prekryvParcely.size() >= Parcela.getMaxPocetReferencii())
            {
                return false;
            }
        }

        // Pridanie je mozne uskutocnit
        this.qsNehnutelnosti.vloz(new DummyNehnutelnost(this.curNehnutelnostID, new Suradnica(vlavoDoleX, vlavoDoleY), new Suradnica(vpravoHoreX, vpravoHoreY)));

        // Napln zoznam referencii
        for (DummyParcela dummyParcela : novaNehnutelnostPrekryv)
        {
            Parcela parcela = new Parcela(dummyParcela);
            novaNehnutelnost.skusPridatParcelu(parcela);
        }
        this.dhNehnutelnosti.vloz(novaNehnutelnost);

        // Rovnako je nutne upravit aj vsetky parcely, s ktorymi sa nova
        // nehnutelnost prekryva (je nutne nastavit novu referenciu)
        for (DummyParcela dummyParcela : novaNehnutelnostPrekryv)
        {
            Parcela parcela = this.dhParcely.vymaz(new Parcela(dummyParcela));
            parcela.skusPridatNehnutelnost(novaNehnutelnost);
            this.dhParcely.vloz(parcela);
        }

        this.curNehnutelnostID++;
        return true;
    }

    // V pripade ak su vstupy neplatne, je vyhodena vynimka
    private void skontrolujVstupySuradnice(double vlavoDoleX, double vlavoDoleY, double vpravoHoreX, double vpravoHoreY)
    {
        if (vlavoDoleX >= vpravoHoreX || vlavoDoleY >= vpravoHoreY)
        {
            throw new RuntimeException("Neplatne zadane rozmery elementu!");
        }

        if (vlavoDoleX < this.qsParcely.getRootQuad().getVlavoDoleX() ||
            vlavoDoleY < this.qsParcely.getRootQuad().getVlavoDoleY() ||
            vpravoHoreX > this.qsParcely.getRootQuad().getVpravoHoreX() ||
            vpravoHoreY > this.qsParcely.getRootQuad().getVpravoHoreY())
        {
            throw new RuntimeException("Vkladany element je prilis velky!");
        }
    }
}
