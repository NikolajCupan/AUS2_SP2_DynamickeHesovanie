package Objekty;

import Hesovanie.IData;

public abstract class Polygon implements IData
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
    public boolean leziVnutri(double x, double y)
    {
        return x >= this.surVlavoDole.getX() &&
               y >= this.surVlavoDole.getY() &&
               x <= this.surVpravoHore.getX() &&
               y <= this.surVpravoHore.getY();
    }

    // Metoda vrati true ak cely obsah polygonu lezi vo vnutri polygonu
    public boolean leziVnutri(Polygon vnutorny)
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
    public boolean prekryva(Polygon polygon)
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

    public double getVlavoDoleX()
    {
        return this.surVlavoDole.getX();
    }

    public double getVlavoDoleY()
    {
        return this.surVlavoDole.getY();
    }

    public double getVpravoHoreX()
    {
        return this.surVpravoHore.getX();
    }

    public double getVpravoHoreY()
    {
        return this.surVpravoHore.getY();
    }
}
