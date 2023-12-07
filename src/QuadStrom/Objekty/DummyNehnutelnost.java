package QuadStrom.Objekty;

import Objekty.Nehnutelnost;
import Objekty.Polygon;
import Objekty.Suradnica;
import Rozhrania.IPolygon;

public class DummyNehnutelnost extends Polygon
{
    private final int nehnutelnostID;

    public DummyNehnutelnost(int nehnutelnostID, Suradnica suradnica1, Suradnica suradnica2)
    {
        this.nastavSuradnice(suradnica1, suradnica2);
        this.nehnutelnostID = nehnutelnostID;
    }

    public DummyNehnutelnost(Nehnutelnost nehnutelnost)
    {
        Suradnica suradnica1 = new Suradnica(nehnutelnost.getVlavoDoleX(), nehnutelnost.getVlavoDoleY());
        Suradnica suradnica2 = new Suradnica(nehnutelnost.getVpravoHoreX(), nehnutelnost.getVpravoHoreY());
        this.nastavSuradnice(suradnica1, suradnica2);

        this.nehnutelnostID = nehnutelnost.getNehnutelnostID();
    }

    public int getNehnutelnostID()
    {
        return this.nehnutelnostID;
    }

    @Override
    public boolean jeRovnakyPolygon(IPolygon polygon)
    {
        if (!(polygon instanceof DummyNehnutelnost dummyNehnutelnost))
        {
            return false;
        }

        return this.nehnutelnostID == dummyNehnutelnost.getNehnutelnostID();
    }

    @Override
    public String toString()
    {
        return "Nehnuteľnosť (" + this.nehnutelnostID + "), {" + this.surVlavoDole.getX() + ", " + this.surVlavoDole.getY() +
                "}, {" + this.surVpravoHore.getX() + ", " + this.surVpravoHore.getY() + "}";
    }
}
