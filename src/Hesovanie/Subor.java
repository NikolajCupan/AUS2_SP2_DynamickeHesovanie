package Hesovanie;

import java.io.File;
import java.io.RandomAccessFile;

public class Subor
{
    private File subor;
    private RandomAccessFile pristupovySubor;

    public Subor(String nazovSuboru)
    {
        this.subor = new File(nazovSuboru);

        try
        {
            if (!this.subor.exists())
            {
                this.subor.createNewFile();
            }

            this.pristupovySubor = new RandomAccessFile(this.subor, "rw");
        }
        catch (Exception ex)
        {
            throw new RuntimeException("Chyba pri vytvarani suboru!");
        }
    }

    // Nacita pocetBajtov bajtov zo Suboru zacinajuc
    // od miesta urceneho offsetom
    public byte[] citaj(long offset, int pocetBajtov)
    {
        this.skontrolujOffset(offset);
        byte[] buffer = new byte[pocetBajtov];

        try
        {
            this.pristupovySubor.seek(offset);
            this.pristupovySubor.read(buffer);
        }
        catch (Exception ex)
        {
            throw new RuntimeException("Chyba pri citani zo suboru!");
        }

        return buffer;
    }

    // Ulozi poleBajtov do Suboru zacinajuc od miesta urceneho offsetom,
    // pricom ak sa tam uz nachadzaju nejake data, tak tieto su prepisane
    public void uloz(long offset, byte[] poleBajtov)
    {
        this.skontrolujOffset(offset);

        try
        {
            this.pristupovySubor.seek(offset);
            this.pristupovySubor.write(poleBajtov);
        }
        catch (Exception ex)
        {
            throw new RuntimeException("Chyba pri ukladani suboru!");
        }
    }

    public long getNovyOffset()
    {
        // Zatial vzdy prideli offset na konci suboru
        return this.getVelkostSuboru();
    }

    private long getVelkostSuboru()
    {
        return this.subor.length();
    }

    private void skontrolujOffset(long offset)
    {
        if (offset < 0)
        {
            throw new RuntimeException("Offset nemoze byt mensi ako 0!");
        }
    }
}
