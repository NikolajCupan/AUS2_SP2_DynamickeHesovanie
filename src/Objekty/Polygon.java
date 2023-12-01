package Objekty;

import Rozhrania.IPolygon;

public class Polygon implements IPolygon
{
    protected Suradnica surVlavoDole;
    protected Suradnica surVpravoHore;

    public void nastavSuradnice(Suradnica suradnica1, Suradnica suradnica2)
    {
        this.surVlavoDole = new Suradnica();
        this.surVpravoHore = new Suradnica();

        this.surVlavoDole.setX(Math.min(suradnica1.getX(), suradnica2.getX()));
        this.surVlavoDole.setY(Math.min(suradnica1.getY(), suradnica2.getY()));

        this.surVpravoHore.setX(Math.max(suradnica1.getX(), suradnica2.getX()));
        this.surVpravoHore.setY(Math.max(suradnica1.getY(), suradnica2.getY()));
    }

    // Metoda vrati true ak dana suradnica lezi vo vnutri polygonu
    @Override
    public boolean leziVnutri(double x, double y)
    {
        return x >= this.surVlavoDole.getX() &&
               y >= this.surVlavoDole.getY() &&
               x <= this.surVpravoHore.getX() &&
               y <= this.surVpravoHore.getY();
    }

    // Metoda vrati true ak cely obsah polygonu lezi vo vnutri polygonu
    @Override
    public boolean leziVnutri(IPolygon vnutorny)
    {
        if (vnutorny.getVlavoDoleX() >= this.surVlavoDole.getX() &&
            vnutorny.getVlavoDoleY() >= this.surVlavoDole.getY() &&
            vnutorny.getVpravoHoreX() <= this.surVpravoHore.getX() &&
            vnutorny.getVpravoHoreY() <= this.surVpravoHore.getY())
        {
            return true;
        }

        return false;
    }

    // Metoda vrati true ak sa polygony prekryvaju
    @Override
    public boolean prekryva(IPolygon polygon)
    {
        if (this.surVlavoDole.getX() > polygon.getVpravoHoreX() ||
            this.surVlavoDole.getY() > polygon.getVpravoHoreY() ||
            polygon.getVlavoDoleX() > this.surVpravoHore.getX() ||
            polygon.getVlavoDoleY() > this.surVpravoHore.getY())
        {
            return false;
        }

        return true;
    }

    @Override
    public double getVlavoDoleX()
    {
        return this.surVlavoDole.getX();
    }

    @Override
    public double getVlavoDoleY()
    {
        return this.surVlavoDole.getY();
    }

    @Override
    public double getVpravoHoreX()
    {
        return this.surVpravoHore.getX();
    }

    @Override
    public double getVpravoHoreY()
    {
        return this.surVpravoHore.getY();
    }

    @Override
    public boolean jeRovnakyPolygon(IPolygon zaznam)
    {
        throw new RuntimeException("Polygon nejde porovnat!");
    }
}
