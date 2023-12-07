package Aplikacia;

import Hesovanie.DynamickeHesovanie;
import Objekty.Nehnutelnost;
import Objekty.Parcela;
import Objekty.Polygon;
import QuadStrom.Objekty.DummyNehnutelnost;
import QuadStrom.Objekty.DummyParcela;
import QuadStrom.QuadStrom;
import Rozhrania.IData;

import java.util.ArrayList;

public class Prezenter
{
    private final Databaza databaza;

    public Prezenter()
    {
        this.databaza = new Databaza();
    }

    public boolean skusObnovit()
    {
        boolean obnovene = this.databaza.obnovAplikaciu();
        if (obnovene)
        {
            // Obnovenie bolo uspesne
            return true;
        }

        return false;
    }

    public void inicializujNove(int blokovaciFaktorHlavnySuborParcely, int blokovaciFaktorPreplnujuciSuborParcely,
                                int blokovaciFaktorHlavnySuborNehnutelnosti, int blokovaciFaktorPreplnujuciSuborNehnutelnosti,
                                double vlavoDoleX, double vlavoDoleY, double vpravoHoreX, double vpravoHoreY, int maxUrovenParcely, int maxUrovenNehnutelnosti)
    {
        this.databaza.resetuj(blokovaciFaktorHlavnySuborParcely, blokovaciFaktorPreplnujuciSuborParcely,
                              blokovaciFaktorHlavnySuborNehnutelnosti, blokovaciFaktorPreplnujuciSuborNehnutelnosti,
                              vlavoDoleX, vlavoDoleY, vpravoHoreX, vpravoHoreY, maxUrovenParcely, maxUrovenNehnutelnosti);
    }

    public boolean skusVlozitParcelu(String popis, double vlavoDoleX, double vlavoDoleY, double vpravoHoreX, double vpravoHoreY)
    {
        return this.databaza.vlozParcelu(popis, vlavoDoleX, vlavoDoleY, vpravoHoreX, vpravoHoreY, -1, false);
    }

    public boolean skusVlozitNehnutelnost(int supisneCislo, String popis, double vlavoDoleX, double vlavoDoleY, double vpravoHoreX, double vpravoHoreY)
    {
        return this.databaza.vlozNehnutelnost(supisneCislo, popis, vlavoDoleX, vlavoDoleY, vpravoHoreX, vpravoHoreY, -1, false);
    }

    public boolean skusVymazatParcelu(int parcelaID)
    {
        Parcela realnaVymazana = this.databaza.vymazParcelu(parcelaID);
        if (realnaVymazana == null)
        {
            return false;
        }

        return true;
    }

    public boolean skusVymazatNehnutelnost(int nehnutelnostID)
    {
        Nehnutelnost realnaVymazana = this.databaza.vymazNehnutelnost(nehnutelnostID);
        if (realnaVymazana == null)
        {
            return false;
        }

        return true;
    }

    public boolean aktualizujParcelu(int parcelaID, String novyPopis)
    {
        return this.databaza.aktualizujParcelu(parcelaID, novyPopis);
    }

    public boolean aktualizujParcelu(int parcelaID, String novyPopis,
                                     double noveVlavoDoleX, double noveVlavoDoleY, double noveVpravoHoreX, double noveVpravoHoreY)
    {
        return this.databaza.aktualizujParcelu(parcelaID, novyPopis,
                                               noveVlavoDoleX, noveVlavoDoleY, noveVpravoHoreX, noveVpravoHoreY);
    }

    public boolean aktualizujNehnutelnost(int nehnutelnostID, int noveSupisneCislo, String novyPopis)
    {
        return this.databaza.aktualizujNehnutelnost(nehnutelnostID, noveSupisneCislo, novyPopis);
    }

    public boolean aktualizujNehnutelnost(int nehnutelnostID, int noveSupisneCislo, String novyPopis,
                                          double noveVlavoDoleX, double noveVlavoDoleY, double noveVpravoHoreX, double noveVpravoHoreY)
    {
        return this.databaza.aktualizujNehnutelnost(nehnutelnostID, noveSupisneCislo, novyPopis,
                                                    noveVlavoDoleX, noveVlavoDoleY, noveVpravoHoreX, noveVpravoHoreY);
    }

    public Parcela vyhladajParcelu(int parcelaID)
    {
        return this.databaza.vyhladajParcelu(parcelaID);
    }

    public Nehnutelnost vyhladajNehnutelnost(int nehnutelnostID)
    {
        return this.databaza.vyhladajNehnutelnost(nehnutelnostID);
    }

    public ArrayList<DummyParcela> vyhladajDummyParcely(double x, double y)
    {
        return this.databaza.vyhladajDummyParcely(x, y);
    }

    public ArrayList<DummyNehnutelnost> vyhladajDummyNehnutelnosti(double x, double y)
    {
        return this.databaza.vyhladajDummyNehnutelnosti(x, y);
    }

    public ArrayList<Polygon> vyhladajPolygony(double x, double y)
    {
        ArrayList<Polygon> polygony = new ArrayList<>(this.databaza.vyhladajDummyParcely(x, y));
        polygony.addAll(this.databaza.vyhladajDummyNehnutelnosti(x, y));
        return polygony;
    }

    public ArrayList<DummyParcela> vyhladajDummyParcely(double vlavoDoleX, double vlavoDoleY, double vpravoHoreX, double vpravoHoreY)
    {
        return this.databaza.vyhladajDummyParcely(vlavoDoleX, vlavoDoleY, vpravoHoreX, vpravoHoreY);
    }

    public ArrayList<DummyNehnutelnost> vyhladajDummyNehnutelnosti(double vlavoDoleX, double vlavoDoleY, double vpravoHoreX, double vpravoHoreY)
    {
        return this.databaza.vyhladajDummyNehnutelnosti(vlavoDoleX, vlavoDoleY, vpravoHoreX, vpravoHoreY);
    }

    public ArrayList<Polygon> vyhladajPolygony(double vlavoDoleX, double vlavoDoleY, double vpravoHoreX, double vpravoHoreY)
    {
        ArrayList<Polygon> polygony = new ArrayList<>(this.databaza.vyhladajDummyParcely(vlavoDoleX, vlavoDoleY, vpravoHoreX, vpravoHoreY));
        polygony.addAll(this.databaza.vyhladajDummyNehnutelnosti(vlavoDoleX, vlavoDoleY, vpravoHoreX, vpravoHoreY));
        return polygony;
    }

    public<T extends IData> String vyhladajToString(int ID, Class<T> typ)
    {
        String vysledok = "";
        if (typ.equals(Parcela.class))
        {
            Parcela najdena = this.databaza.vyhladajParcelu(ID);
            if (najdena == null)
            {
                vysledok = "Parcela s daným identifikačným číslom neexistuje!";
                return vysledok;
            }

            vysledok += najdena.toString();
            vysledok += "\n\nNehnuteľnosti, s ktorými sa prekrýva:\n\n";

            for (Integer nehnutelnostID : najdena.getNehnutelnostiID())
            {
                Nehnutelnost nehnutelnost = this.databaza.vyhladajNehnutelnost(nehnutelnostID);
                vysledok += nehnutelnost.toString() + "\n";
            }
        }
        else
        {
            Nehnutelnost najdena = this.databaza.vyhladajNehnutelnost(ID);
            if (najdena == null)
            {
                vysledok = "Nehnuteľnosť s daným identifikačným číslom neexistuje!";
                return vysledok;
            }

            vysledok += najdena.toString();
            vysledok += "\n\nParcely, s ktorými sa prekrýva:\n\n";

            for (Integer parcelaID : najdena.getParcelyID())
            {
                Parcela parcela = this.databaza.vyhladajParcelu(parcelaID);
                vysledok += parcela.toString() + "\n";
            }
        }

        return vysledok;
    }

    public void resetuj()
    {
        this.databaza.zacniZnova();
    }

    public boolean uloz()
    {
        return this.databaza.ukonciAplikaciu();
    }

    public DynamickeHesovanie<Parcela> getDhParcely()
    {
        return this.databaza.getDhParcely();
    }

    public DynamickeHesovanie<Nehnutelnost> getDhNehnutelnosti()
    {
        return this.databaza.getDhNehnutelnosti();
    }

    public QuadStrom<DummyParcela> getQsParcely()
    {
        return this.databaza.getQsParcely();
    }

    public QuadStrom<DummyNehnutelnost> getQsNehnutelnosti()
    {
        return this.databaza.getQsNehnutelnosti();
    }
}
