package Hesovanie;

import java.io.File;
import java.io.RandomAccessFile;

public class SpravcaSuborov
{
    // Udava kolko Recordov sa nachadza v 1 Blocku
    private final int blokovaciFaktorHlavnySubor;
    private final int blokovaciFaktorPreplnujuciSubor;

    private final File hlavnySubor;
    private final RandomAccessFile hlavnyPristupovySubor;

    private final File preplnujuciSubor;
    private final RandomAccessFile preplnujuciPrustupovySubor;

    public SpravcaSuborov(int blokovaciFaktorHlavnySubor, int blokovaciFaktorPreplnujuciSubor, String nazovHlavnySubor, String nazovPreplnujuciSubor)
    {
        this.blokovaciFaktorHlavnySubor = blokovaciFaktorHlavnySubor;
        this.blokovaciFaktorPreplnujuciSubor = blokovaciFaktorPreplnujuciSubor;

        this.hlavnySubor = new File(nazovHlavnySubor);
        this.preplnujuciSubor = new File(nazovPreplnujuciSubor);

        try
        {
            if (!this.hlavnySubor.exists())
            {
                this.hlavnySubor.createNewFile();
            }

            if (!this.preplnujuciSubor.exists())
            {
                this.preplnujuciSubor.createNewFile();
            }

            this.hlavnyPristupovySubor = new RandomAccessFile(this.hlavnySubor, "rw");
            this.preplnujuciPrustupovySubor = new RandomAccessFile(this.preplnujuciSubor, "rw");
        }
        catch (Exception ex)
        {
            throw new RuntimeException("Chyba pri vytvarani suborov!");
        }
    }

    // Nacita pocetBajtov bajtov z hlavneho suboru zacinajuc
    // od miesta urceneho offsetom
    public byte[] citaj(long offset, int pocetBajtov)
    {
        this.skontrolujOffset(offset);
        byte[] buffer = new byte[pocetBajtov];

        try
        {
            this.hlavnyPristupovySubor.seek(offset);
            this.hlavnyPristupovySubor.read(buffer);
        }
        catch (Exception ex)
        {
            throw new RuntimeException("Chyba pri citani zo suboru!");
        }

        return buffer;
    }

    // Ulozi poleBajtov do hlavneho suboru zacinajuc od miesta urceneho offsetom,
    // pricom ak sa tam uz nachadzaju nejake data, tak tieto su prepisane
    public void uloz(long offset, byte[] poleBajtov)
    {
        this.skontrolujOffset(offset);

        try
        {
            this.hlavnyPristupovySubor.seek(offset);
            this.hlavnyPristupovySubor.write(poleBajtov);
        }
        catch (Exception ex)
        {
            throw new RuntimeException("Chyba pri ukladani suboru!");
        }
    }

    public long dajVolnyBlockHlavnySubor()
    {
        // Zatial vzdy prideli offset na konci suboru
        return this.getVelkostSuboru();
    }

    public void uvolniBlockHlavnySubor(long offset)
    {

    }

    private void skontrolujOffset(long offset)
    {
        if (offset < 0)
        {
            throw new RuntimeException("Offset nemoze byt mensi ako 0!");
        }
    }

    private long getVelkostSuboru()
    {
        return this.hlavnySubor.length();
    }

    public int getBlokovaciFaktorHlavnySubor()
    {
        return blokovaciFaktorHlavnySubor;
    }

    public int getBlokovaciFaktorPreplnujuciSubor()
    {
        return blokovaciFaktorPreplnujuciSubor;
    }
}
