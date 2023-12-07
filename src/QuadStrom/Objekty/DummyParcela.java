package QuadStrom.Objekty;

import Objekty.Parcela;
import Objekty.Polygon;
import Objekty.Suradnica;
import Rozhrania.IPolygon;

import java.text.DecimalFormat;

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

    @Override
    public String toString()
    {
        DecimalFormat formatovac = new DecimalFormat("#.##");
        return "Parcela (" + this.parcelaID + "), {" + formatovac.format(this.surVlavoDole.getX()) + ", " +
               formatovac.format(this.surVlavoDole.getY()) + "}, {" + formatovac.format(this.surVpravoHore.getX()) +
               ", " + formatovac.format(this.surVpravoHore.getY()) + "}";
    }
}
