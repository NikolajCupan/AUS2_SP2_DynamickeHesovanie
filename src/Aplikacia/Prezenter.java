package Aplikacia;

import Hesovanie.DynamickeHesovanie;
import Objekty.Nehnutelnost;
import Objekty.Parcela;
import QuadStrom.Objekty.DummyNehnutelnost;
import QuadStrom.Objekty.DummyParcela;
import QuadStrom.QuadStrom;
import Rozhrania.IData;

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

    public<T extends IData> String vyhladaj(int ID, Class<T> typ)
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
