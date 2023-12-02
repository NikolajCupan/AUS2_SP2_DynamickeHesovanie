package Aplikacia;

import Hesovanie.DynamickeHesovanie;
import Objekty.Nehnutelnost;
import Objekty.Parcela;
import Objekty.Suradnica;
import QuadStrom.Objekty.DummyNehnutelnost;
import QuadStrom.Objekty.DummyParcela;
import QuadStrom.QuadStrom;

import java.util.ArrayList;

public class Databaza
{
    private DynamickeHesovanie<Parcela> dhParcely;
    private DynamickeHesovanie<Nehnutelnost> dhNehnutelnosti;

    private QuadStrom<DummyParcela> qsParcely;
    private QuadStrom<DummyNehnutelnost> qsNehnutelnosti;

    // Pocitadla identifikacnych cisel
    private int curParcelaID;
    private int curNehnutelnostID;

    public Databaza()
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

    // Aktualizacia parcely vratane zmeny suradnic
    public boolean aktualizujParcelu(int parcelaID, String novyPopis,
                                     double noveVlavoDoleX, double noveVlavoDoleY, double noveVpravoHoreX, double noveVpravoHoreY)
    {
        this.skontrolujVstupy(noveVlavoDoleX, noveVlavoDoleY, noveVpravoHoreX, noveVpravoHoreY);

        // Vstupy su korektne
        Parcela vymazanaDatabaza = this.vymazParcelu(parcelaID);
        if (vymazanaDatabaza == null)
        {
            // Dana parcela sa v databaze nenachadza
            return false;
        }

        // Vlozenie aktualizovanej parcely naspat do databazy
        if (!this.vlozParcelu(novyPopis, noveVlavoDoleX, noveVlavoDoleY, noveVpravoHoreX, noveVpravoHoreY,
                              vymazanaDatabaza.getParcelaID(), true))
        {
            // Vlozenie zlyhalo, pravdepodobne sa aktualizovane suradnice
            // prekryvaju s prilis velkym poctom nehnutelnosti

            // Do databazy je vlozena originalna parcela
            if (!this.vlozParcelu(vymazanaDatabaza.getPopis(), vymazanaDatabaza.getVlavoDoleX(), vymazanaDatabaza.getVlavoDoleY(),
                                  vymazanaDatabaza.getVpravoHoreX(), vymazanaDatabaza.getVpravoHoreY(),
                                  vymazanaDatabaza.getParcelaID(), true))
            {
                throw new RuntimeException("Originalna parcela nebola vlozena!");
            }

            return false;
        }

        // Aktualizovanie bolo uspesne vykonane
        return true;
    }

    // Aktualizacia nehnutelnosti vratane zmeny suradnic
    public boolean aktualizujNehnutelnost(int nehnutelnostID, int noveSupisneCislo, String novyPopis,
                                          double noveVlavoDoleX, double noveVlavoDoleY, double noveVpravoHoreX, double noveVpravoHoreY)
    {
        this.skontrolujVstupy(noveVlavoDoleX, noveVlavoDoleY, noveVpravoHoreX, noveVpravoHoreY);

        // Vstupy su korektne
        Nehnutelnost vymazanaDatabaza = this.vymazNehnutelnost(nehnutelnostID);
        if (vymazanaDatabaza == null)
        {
            // Dana nehnutelnost sa v databaze nenachadza
            return false;
        }

        // Vlozenie aktualizovanej nehnutelnosti naspat do databazy
        if (!this.vlozNehnutelnost(noveSupisneCislo, novyPopis, noveVlavoDoleX, noveVlavoDoleY, noveVpravoHoreX, noveVpravoHoreY,
                                   vymazanaDatabaza.getNehnutelnostID(), true))
        {
            // Vlozenie zlyhalo, pravdepodobne sa aktualizovane suradnice
            // prekryvaju s prilis velkym poctom parciel

            // Do databazy je vlozena originalna nehnutelnost
            if (!this.vlozNehnutelnost(vymazanaDatabaza.getSupisneCislo(), vymazanaDatabaza.getPopis(),
                                       vymazanaDatabaza.getVlavoDoleX(), vymazanaDatabaza.getVlavoDoleY(),
                                       vymazanaDatabaza.getVpravoHoreX(), vymazanaDatabaza.getVpravoHoreY(),
                                       vymazanaDatabaza.getNehnutelnostID(), true))
            {
                throw new RuntimeException("Originalna nehnutelnost nebola vlozena!");
            }

            return false;
        }

        // Aktualizovanie bolo uspesne vykonane
        return true;
    }

    // Aktualizacia parcely bez zmeny suradnic
    public boolean aktualizujParcelu(int parcelaID, String novyPopis)
    {
        Parcela parcela = new Parcela(parcelaID);
        Parcela realneNajdena = this.dhParcely.vyhladaj(parcela);

        if (realneNajdena == null)
        {
            // Dana parcela neexistuje, tym padom nemoze byt aktualizovana
            return false;
        }
        else
        {
            // Parcela existuje a moze byt aktualizovana

            // Samotna aktualizacia
            realneNajdena.setPopis(novyPopis);

            // Ulozenie stavu
            this.dhParcely.aktualizuj(realneNajdena);

            return true;
        }
    }

    // Aktualizacia nehnutelnosti bez zmeny suradnic
    public boolean aktualizujNehnutelnost(int nehnutelnostID, int noveSupisneCislo, String novyPopis)
    {
        Nehnutelnost nehnutelnost = new Nehnutelnost(nehnutelnostID);
        Nehnutelnost realneNajdena = this.dhNehnutelnosti.vyhladaj(nehnutelnost);

        if (realneNajdena == null)
        {
            // Dana nehnutelnost neexistuje, tym padom nemoze byt aktualizovana
            return false;
        }
        else
        {
            // Nehnutelnost existuje a moze byt aktualizovana

            // Samotna aktualizacia
            realneNajdena.setSupisneCislo(noveSupisneCislo);
            realneNajdena.setPopis(novyPopis);

            // Ulozenie stavu
            this.dhNehnutelnosti.aktualizuj(realneNajdena);

            return true;
        }
    }

    public Parcela vyhladajParcelu(int parcelaID)
    {
        Parcela parcela = new Parcela(parcelaID);
        return this.dhParcely.vyhladaj(parcela);
    }

    public Nehnutelnost vyhladajNehnutelnost(int nehnutelnostID)
    {
        Nehnutelnost nehnutelnost = new Nehnutelnost(nehnutelnostID);
        return this.dhNehnutelnosti.vyhladaj(nehnutelnost);
    }

    public boolean vlozParcelu(String popis, double vlavoDoleX, double vlavoDoleY, double vpravoHoreX, double vpravoHoreY,
                               int forcedParcelaID, boolean forceParcelaID)
    {
        this.skontrolujVstupy(vlavoDoleX, vlavoDoleY, vpravoHoreX, vpravoHoreY);
        int pouziteParcelaID = forceParcelaID ? forcedParcelaID : this.curParcelaID++;

        // Vstupy su korektne
        Parcela novaParcela = new Parcela(pouziteParcelaID, popis, new Suradnica(vlavoDoleX, vlavoDoleY), new Suradnica(vpravoHoreX, vpravoHoreY));
        ArrayList<DummyNehnutelnost> novaParcelaPrekryv = this.qsNehnutelnosti.vyhladaj(vlavoDoleX, vlavoDoleY, vpravoHoreX, vpravoHoreY);

        if (novaParcelaPrekryv.size() > Parcela.getMaxPocetReferencii())
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
        this.qsParcely.vloz(new DummyParcela(pouziteParcelaID, new Suradnica(vlavoDoleX, vlavoDoleY), new Suradnica(vpravoHoreX, vpravoHoreY)));

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
            Nehnutelnost nehnutelnost = this.dhNehnutelnosti.vyhladaj(new Nehnutelnost(dummyNehnutelnost));
            nehnutelnost.skusPridatParcelu(novaParcela);
            this.dhNehnutelnosti.aktualizuj(nehnutelnost);
        }

        return true;
    }

    public boolean vlozNehnutelnost(int supisneCislo, String popis, double vlavoDoleX, double vlavoDoleY, double vpravoHoreX, double vpravoHoreY,
                                    int forcedNehnutelnostID, boolean forceNehnutelnostID)
    {
        this.skontrolujVstupy(vlavoDoleX, vlavoDoleY, vpravoHoreX, vpravoHoreY);
        int pouziteNehnutelnostID = forceNehnutelnostID ? forcedNehnutelnostID : this.curNehnutelnostID++;

        // Vstupy su korektne
        Nehnutelnost novaNehnutelnost = new Nehnutelnost(pouziteNehnutelnostID, supisneCislo, popis, new Suradnica(vlavoDoleX, vlavoDoleY), new Suradnica(vpravoHoreX, vpravoHoreY));
        ArrayList<DummyParcela> novaNehnutelnostPrekryv = this.qsParcely.vyhladaj(vlavoDoleX, vlavoDoleY, vpravoHoreX, vpravoHoreY);

        if (novaNehnutelnostPrekryv.size() > Nehnutelnost.getMaxPocetReferencii())
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
        this.qsNehnutelnosti.vloz(new DummyNehnutelnost(pouziteNehnutelnostID, new Suradnica(vlavoDoleX, vlavoDoleY), new Suradnica(vpravoHoreX, vpravoHoreY)));

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
            Parcela parcela = this.dhParcely.vyhladaj(new Parcela(dummyParcela));
            parcela.skusPridatNehnutelnost(novaNehnutelnost);
            this.dhParcely.aktualizuj(parcela);
        }

        return true;
    }

    public Parcela vymazParcelu(int parcelaID)
    {
        Parcela parcela = new Parcela(parcelaID);
        Parcela realneVymazana = this.dhParcely.vymaz(parcela);

        if (realneVymazana == null)
        {
            return null;
        }

        DummyParcela dummyParcela = new DummyParcela(realneVymazana);
        DummyParcela realneVymazanaDummy = this.qsParcely.vymaz((realneVymazana.getVlavoDoleX() + realneVymazana.getVpravoHoreX()) / 2,
                                                                (realneVymazana.getVlavoDoleY() + realneVymazana.getVpravoHoreY()) / 2,
                                                                   dummyParcela);

        if (realneVymazanaDummy == null)
        {
            throw new RuntimeException("Nastala chyba pri vymazavani parcely z Quad stromu!");
        }

        // Dalej je nutne upravit referencie
        for (Integer nehnutelnostID : realneVymazana.getNehnutelnostiID())
        {
            Nehnutelnost nehnutelnost = this.dhNehnutelnosti.vyhladaj(new Nehnutelnost(nehnutelnostID));
            nehnutelnost.skusOdobratParcelu(realneVymazana);
            this.dhNehnutelnosti.aktualizuj(nehnutelnost);
        }

        return realneVymazana;
    }

    public Nehnutelnost vymazNehnutelnost(int nehnutelnostID)
    {
        Nehnutelnost nehnutelnost = new Nehnutelnost(nehnutelnostID);
        Nehnutelnost realneVymazana = this.dhNehnutelnosti.vymaz(nehnutelnost);

        if (realneVymazana == null)
        {
            return null;
        }

        DummyNehnutelnost dummyNehnutelnost = new DummyNehnutelnost(realneVymazana);
        DummyNehnutelnost realneVymazanaDummy = this.qsNehnutelnosti.vymaz((realneVymazana.getVlavoDoleX() + realneVymazana.getVpravoHoreX()) / 2,
                                                                           (realneVymazana.getVlavoDoleY() + realneVymazana.getVpravoHoreY()) / 2,
                                                                              dummyNehnutelnost);

        if (realneVymazanaDummy == null)
        {
            throw new RuntimeException("Nastala chyba pri vymazavani nehnutelnosti z Quad stromu!");
        }

        // Dalej je nutne upravit referencie
        for (Integer parcelyID : realneVymazana.getParcelyID())
        {
            Parcela parcela = this.dhParcely.vyhladaj(new Parcela(parcelyID));
            parcela.skusOdobratNehnutelnost(realneVymazana);
            this.dhParcely.aktualizuj(parcela);
        }

        return realneVymazana;
    }

    // Skontroluje, ci pocet elementov v jednotlivych QS a DH sa zhoduju
    public boolean stavKorektny()
    {
        int pocetDhParcely = this.dhParcely.getPocetElementov();
        int pocetQsParcely = this.qsParcely.getPocetElementov();

        int pocetDhNehnutelnosti = this.dhNehnutelnosti.getPocetElementov();
        int pocetQsNehnutelnosti = this.qsNehnutelnosti.getPocetElementov();

        if (pocetDhParcely != pocetQsParcely ||
            pocetDhNehnutelnosti != pocetQsNehnutelnosti)
        {
            return false;
        }

        return true;
    }

    // V pripade ak su vstupy neplatne, je vyhodena vynimka
    private void skontrolujVstupy(double vlavoDoleX, double vlavoDoleY, double vpravoHoreX, double vpravoHoreY)
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
