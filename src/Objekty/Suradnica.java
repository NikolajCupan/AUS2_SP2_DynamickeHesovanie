package Objekty;

public class Suradnica
{
    private double x;
    private double y;

    public Suradnica(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    public Suradnica()
    {
        this.x = 0;
        this.y = 0;
    }

    public double getX()
    {
        return this.x;
    }

    public double getY()
    {
        return this.y;
    }

    public void setX(double x)
    {
        this.x = x;
    }

    public void setY(double y)
    {
        this.y = y;
    }
}
