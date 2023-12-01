package QuadStrom.Objekty;

import Objekty.Polygon;
import Objekty.Suradnica;

public class DummyNehnutelnost extends Polygon
{
    private final int nehnutelnostID;

    public DummyNehnutelnost(int nehnutelnostID, Suradnica suradnica1, Suradnica suradnica2)
    {
        this.nastavSuradnice(suradnica1, suradnica2);
        this.nehnutelnostID = nehnutelnostID;
    }

    public int getNehnutelnostID()
    {
        return this.nehnutelnostID;
    }
}
