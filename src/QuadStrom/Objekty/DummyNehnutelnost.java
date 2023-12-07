package QuadStrom.Objekty;

import Objekty.Nehnutelnost;
import Objekty.Polygon;
import Objekty.Suradnica;
import Rozhrania.IPolygon;

import java.text.DecimalFormat;

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
        DecimalFormat formatovac = new DecimalFormat("#.##");
        return "Nehnuteľnosť (" + this.nehnutelnostID + "), {" + formatovac.format(this.surVlavoDole.getX()) + ", " +
               formatovac.format(this.surVlavoDole.getY()) + "}, {" + formatovac.format(this.surVpravoHore.getX()) +
               ", " + formatovac.format(this.surVpravoHore.getY()) + "}";
    }
}
