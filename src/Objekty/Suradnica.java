package Objekty;

import Ostatne.Helper;
import Ostatne.Konstanty;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Suradnica
{
    public static final char VYCHOD = 'V';
    public static final char ZAPAD = 'Z';
    public static final char SEVER = 'S';
    public static final char JUH = 'J';

    private double x;
    private double y;
    private char dlzka;
    private char sirka;

    public Suradnica(double x, double y)
    {
        this.x = x;
        this.y = y;

        this.dlzka = (x < 0) ? ZAPAD : VYCHOD;
        this.sirka = (y < 0) ? JUH : SEVER;
    }

    public Suradnica()
    {
        this.x = 0;
        this.y = 0;

        this.dlzka = VYCHOD;
        this.sirka = SEVER;
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
        this.dlzka = (x < 0) ? ZAPAD : VYCHOD;
    }

    public void setY(double y)
    {
        this.y = y;
        this.sirka = (y < 0) ? JUH : SEVER;
    }

    public byte[] prevedNaPoleBajtov()
    {
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteOutputStream);

        try
        {
            dataOutputStream.writeDouble(this.x);
            dataOutputStream.writeDouble(this.y);

            return byteOutputStream.toByteArray();
        }
        catch (IOException e)
        {
            throw new IllegalStateException("Konverzia Parcely na pole bajtov sa nepodarila!");
        }
    }
}
