package QuadStrom.Objekty;

import Objekty.Parcela;
import Objekty.Polygon;
import Objekty.Suradnica;
import Rozhrania.IPolygon;

public class DummyParcela extends Polygon
{
    private final int parcelaID;

    public DummyParcela(int parcelaID, Suradnica suradnica1, Suradnica suradnica2)
    {
        this.nastavSuradnice(suradnica1, suradnica2);
        this.parcelaID = parcelaID;
    }

    public DummyParcela(Parcela parcela)
    {
        Suradnica suradnica1 = new Suradnica(parcela.getVlavoDoleX(), parcela.getVlavoDoleY());
        Suradnica suradnica2 = new Suradnica(parcela.getVpravoHoreX(), parcela.getVpravoHoreY());
        this.nastavSuradnice(suradnica1, suradnica2);

        this.parcelaID = parcela.getParcelaID();
    }

    public int getParcelaID()
    {
        return this.parcelaID;
    }

    @Override
    public boolean jeRovnakyPolygon(IPolygon polygon)
    {
        if (!(polygon instanceof DummyParcela dummyParcela))
        {
            return false;
        }

        return this.parcelaID == dummyParcela.getParcelaID();
    }
}
