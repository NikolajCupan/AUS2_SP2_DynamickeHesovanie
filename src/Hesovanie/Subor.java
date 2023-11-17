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

    public byte[] citaj(long offset, int pocetBajtov)
    {
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

    public void uloz(long offset, byte[] poleBajtov)
    {
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
}
