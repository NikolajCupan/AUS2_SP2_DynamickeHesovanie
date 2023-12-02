package Aplikacia;

import Hesovanie.DynamickeHesovanie;
import Objekty.Nehnutelnost;
import Objekty.Parcela;
import QuadStrom.Objekty.DummyNehnutelnost;
import QuadStrom.Objekty.DummyParcela;
import QuadStrom.QuadStrom;

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

    public void resetuj()
    {
        this.databaza.zacniZnova();
    }

    public void uloz()
    {
        this.databaza.ukonciAplikaciu();
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
