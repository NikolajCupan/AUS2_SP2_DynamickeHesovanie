package Ostatne;

import Objekty.Nehnutelnost;
import Objekty.Parcela;
import Objekty.Polygon;
import Objekty.Suradnica;

import java.util.Random;

import static java.lang.Math.abs;

public class Generator
{
    private double faktorZmensenia;

    private int curNehnutelnostID;
    private int curSupisneCislo;
    private int curParcelaID;

    // Hranice generovanych suradnic
    private double surMinX;
    private double surMinY;
    private double surMaxX;
    private double surMaxY;

    private Random random;
    private final String znaky = "abcdefghijklmnopqrstuvwxyz";

    public Generator(int startNehnutelnostID, int startSupisneCislo, int startParcelaID, double surMinX, double surMinY, double surMaxX, double surMaxY, double faktorZmensenia)
    {
        this.nastavPremenne(startNehnutelnostID, startSupisneCislo, startParcelaID, surMinX, surMinY, surMaxX, surMaxY, faktorZmensenia);
    }

    public Generator(int startNehnutelnostID, int startSupisneCislo, int startParcelaID, double surMinX, double surMinY, double surMaxX, double surMaxY, double faktorZmensenia, long seed)
    {
        this.nastavPremenne(startNehnutelnostID, startSupisneCislo, startParcelaID, surMinX, surMinY, surMaxX, surMaxY, faktorZmensenia);
        this.random.setSeed(seed);
    }

    private void nastavPremenne(int startNehnutelnostID, int startSupisneCislo, int startParcelaID, double surMinX, double surMinY, double surMaxX, double surMaxY, double faktorZmensenia)
    {
        this.curNehnutelnostID = startNehnutelnostID;
        this.curSupisneCislo = startSupisneCislo;
        this.curParcelaID = startParcelaID;

        this.surMinX = surMinX;
        this.surMinY = surMinY;
        this.surMaxX = surMaxX;
        this.surMaxY = surMaxY;

        this.faktorZmensenia = faktorZmensenia;

        this.random = new Random();
    }

    // Vrati nehnutelnost alebo parcelu (s rovnakou pravdepodobnostou)
    public Polygon getPolygon()
    {
        return (this.random.nextBoolean()) ? this.getNehnutelnost() : this.getParcela();
    }

    public Nehnutelnost getNehnutelnost()
    {
        Suradnica suradnica1 = new Suradnica(0, 0);
        Suradnica suradnica2 = new Suradnica(0, 0);
        this.vygenerujSuradnice(suradnica1, suradnica2);

        String popis = this.randomString(Konstanty.MAX_DLZKA_POPIS_NEHNUTELNOST);

        Nehnutelnost nehnutelnost = new Nehnutelnost(this.curNehnutelnostID, this.curSupisneCislo, popis, suradnica1, suradnica2);
        this.curNehnutelnostID++;
        this.curSupisneCislo++;

        return nehnutelnost;
    }

    public Parcela getParcela()
    {
        Suradnica suradnica1 = new Suradnica(0, 0);
        Suradnica suradnica2 = new Suradnica(0, 0);
        this.vygenerujSuradnice(suradnica1, suradnica2);

        String popis = this.randomString(Konstanty.MAX_DLZKA_POPIS_PARCELA);

        Parcela parcela = new Parcela(this.curParcelaID, popis, suradnica1, suradnica2);
        this.curParcelaID++;

        return parcela;
    }

    // Pomocna metoda na vygenerovanie suradnic,
    // dlzky jednotlivych rozmerov suradnic su zmensene faktor-krat
    private void vygenerujSuradnice(Suradnica surVlavoDole, Suradnica surVpravoHore)
    {
        double x1 = this.randomDouble(this.surMinX, this.surMaxX);
        double y1 = this.randomDouble(this.surMinY, this.surMaxY);
        double x2 = this.randomDouble(this.surMinX, this.surMaxX);
        double y2 = this.randomDouble(this.surMinY, this.surMaxY);

        double minX = Math.min(x1, x2);
        double minY = Math.min(y1, y2);
        double maxX = Math.max(x1, x2);
        double maxY = Math.max(y1, y2);

        // K zmenseniu moze dojst smerom dolava alebo doprava
        // (s rovnakou pravdepodobnostou)
        boolean zmensiDolava = this.random.nextBoolean();

        double vzdialenostX = abs(maxX - minX);
        double vzdialenostY = abs(maxY - minY);
        vzdialenostX /= this.faktorZmensenia;
        vzdialenostY /= this.faktorZmensenia;

        if (zmensiDolava)
        {
            maxX = minX + vzdialenostX;
            maxY = minY + vzdialenostY;
        }
        else
        {
            minX = maxX - vzdialenostX;
            minY = maxY - vzdialenostY;
        }

        surVlavoDole.setX(minX);
        surVlavoDole.setY(minY);
        surVpravoHore.setX(maxX);
        surVpravoHore.setY(maxY);
    }

    public double randomDouble(double min, double max)
    {
        return min + (max - min) * this.random.nextDouble();
    }

    private String randomString(int dlzka)
    {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < dlzka; i++)
        {
            int index = this.random.nextInt(this.znaky.length());
            char randomChar = this.znaky.charAt(index);
            builder.append(randomChar);
        }

        return builder.toString();
    }

    public void setCurNehnutelnostID(int curNehnutelnostID)
    {
        if (curNehnutelnostID < 0)
        {
            throw new RuntimeException("ID nehnutelnosti nemoze byt zaporne!");
        }

        this.curNehnutelnostID = curNehnutelnostID;
    }

    public void setCurSupisneCislo(int curSupisneCislo)
    {
        if (curSupisneCislo < 0)
        {
            throw new RuntimeException("Supisne cislo nemoze byt zaporne!");
        }

        this.curSupisneCislo = curSupisneCislo;
    }

    public void setCurParcelaID(int curParcelaID)
    {
        if (curParcelaID < 0)
        {
            throw new RuntimeException("ID parcely nemoze byt zaporne!");
        }

        this.curParcelaID = curParcelaID;
    }

    public void setFaktorZmensenia(double faktorZmensenia)
    {
        if (faktorZmensenia < 1.0)
        {
            throw new RuntimeException("Faktor zmensenia musi byt rovny alebo vacsi ako 1!");
        }

        this.faktorZmensenia = faktorZmensenia;
    }
}
