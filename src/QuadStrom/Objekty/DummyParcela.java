package QuadStrom.Objekty;

import Objekty.Polygon;
import Objekty.Suradnica;

public class DummyParcela extends Polygon
{
    private final int parcelaID;

    public DummyParcela(int parcelaID, Suradnica suradnica1, Suradnica suradnica2)
    {
        this.nastavSuradnice(suradnica1, suradnica2);
        this.parcelaID = parcelaID;
    }

    public int getParcelaID()
    {
        return this.parcelaID;
    }
}
