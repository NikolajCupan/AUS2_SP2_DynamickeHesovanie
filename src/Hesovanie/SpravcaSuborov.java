package Hesovanie;

import Rozhrania.IData;

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

    private long offsetPrvyVolnyHlavnySubor;

    public SpravcaSuborov(int blokovaciFaktorHlavnySubor, int blokovaciFaktorPreplnujuciSubor, String nazovHlavnySubor, String nazovPreplnujuciSubor)
    {
        this.blokovaciFaktorHlavnySubor = blokovaciFaktorHlavnySubor;
        this.blokovaciFaktorPreplnujuciSubor = blokovaciFaktorPreplnujuciSubor;

        this.hlavnySubor = new File(nazovHlavnySubor);
        this.preplnujuciSubor = new File(nazovPreplnujuciSubor);

        this.offsetPrvyVolnyHlavnySubor = -1;

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

    public<T extends IData> long dajVolnyBlockHlavnySubor(Class<T> typ)
    {
        // Zatial vzdy prideli offset na konci suboru
        if (this.offsetPrvyVolnyHlavnySubor == -1)
        {
            // Nutne pridelit na konci suboru
            return this.getVelkostSuboru();
        }

        Block<T> prvyVolnyBlock = new Block<>(this.blokovaciFaktorHlavnySubor, typ);
        prvyVolnyBlock.prevedZPolaBajtov(this.citaj(this.offsetPrvyVolnyHlavnySubor, prvyVolnyBlock.getVelkost()));

        if (prvyVolnyBlock.getOffsetNextVolny() == -1)
        {
            // Nema nasledovnika
            long pridelenyOffset = this.offsetPrvyVolnyHlavnySubor;
            this.offsetPrvyVolnyHlavnySubor = -1;
            return pridelenyOffset;
        }

        // Ma nasledovnika, nutne spracovat
        Block<T> nasledovnik = new Block<>(this.blokovaciFaktorHlavnySubor, typ);
        nasledovnik.prevedZPolaBajtov(this.citaj(prvyVolnyBlock.getOffsetNextVolny(), nasledovnik.getVelkost()));
        nasledovnik.setOffsetPrevVolny(-1);

        this.uloz(prvyVolnyBlock.getOffsetNextVolny(), nasledovnik.prevedNaPoleBajtov());

        long pridelenyOffset = this.offsetPrvyVolnyHlavnySubor;
        this.offsetPrvyVolnyHlavnySubor = prvyVolnyBlock.getOffsetNextVolny();

        return pridelenyOffset;
    }

    public<T extends IData> void uvolniBlockHlavnySubor(long novyPrvyOffset, Class<T> typ)
    {
        this.skontrolujOffset(novyPrvyOffset);

        if (this.offsetPrvyVolnyHlavnySubor == -1)
        {
            Block<T> prazdnyBlock = new Block<>(this.getBlokovaciFaktorHlavnySubor(), typ);
            this.uloz(novyPrvyOffset, prazdnyBlock.prevedNaPoleBajtov());
            this.offsetPrvyVolnyHlavnySubor = novyPrvyOffset;
        }
        else
        {
            long staryPrvyOffset = this.offsetPrvyVolnyHlavnySubor;

            Block<T> novyPrvyVolny = new Block<>(this.getBlokovaciFaktorHlavnySubor(), typ);

            Block<T> staryPrvyVolny = new Block<>(this.getBlokovaciFaktorHlavnySubor(), typ);
            staryPrvyVolny.prevedZPolaBajtov(this.citaj(staryPrvyOffset, staryPrvyVolny.getVelkost()));

            staryPrvyVolny.setOffsetPrevVolny(novyPrvyOffset);
            novyPrvyVolny.setOffsetNextVolny(staryPrvyOffset);

            this.offsetPrvyVolnyHlavnySubor = novyPrvyOffset;

            this.uloz(staryPrvyOffset, staryPrvyVolny.prevedNaPoleBajtov());
            this.uloz(novyPrvyOffset, novyPrvyVolny.prevedNaPoleBajtov());
        }

        this.skusZmensitHlavnySubor(typ);
    }

    private<T extends IData> void skusZmensitHlavnySubor(Class<T> typ)
    {
        try
        {
            while (true)
            {
                Block<T> block = new Block<>(this.getBlokovaciFaktorHlavnySubor(), typ);

                long velkostSuboru = this.getVelkostSuboru();
                long offsetPoslednehoBlocku = velkostSuboru - block.getVelkost();

                // Moznost zmensit subor
                Block<T> blockNaKonciSuboru = new Block<>(this.getBlokovaciFaktorHlavnySubor(), typ);
                blockNaKonciSuboru.prevedZPolaBajtov(this.citaj(offsetPoslednehoBlocku, blockNaKonciSuboru.getVelkost()));

                if (blockNaKonciSuboru.getPocetPlatnychZaznamov() == 0)
                {
                    // Block mozno zmazat
                    long nextOffset = blockNaKonciSuboru.getOffsetNextVolny();
                    long prevOffset = blockNaKonciSuboru.getOffsetPrevVolny();

                    if (nextOffset != -1 && prevOffset != -1)
                    {
                        Block<T> prevBlock = new Block<>(this.blokovaciFaktorHlavnySubor, typ);
                        Block<T> nextBlock = new Block<>(this.blokovaciFaktorHlavnySubor, typ);
                        prevBlock.prevedZPolaBajtov(this.citaj(prevOffset, prevBlock.getVelkost()));
                        nextBlock.prevedZPolaBajtov(this.citaj(nextOffset, nextBlock.getVelkost()));

                        prevBlock.setOffsetNextVolny(nextOffset);
                        nextBlock.setOffsetPrevVolny(prevOffset);

                        this.uloz(prevOffset, prevBlock.prevedNaPoleBajtov());
                        this.uloz(nextOffset, nextBlock.prevedNaPoleBajtov());

                        this.hlavnyPristupovySubor.setLength(offsetPoslednehoBlocku);
                    }
                    else if (nextOffset == -1 && prevOffset != -1)
                    {
                        Block<T> prevBlock = new Block<>(this.blokovaciFaktorHlavnySubor, typ);
                        prevBlock.prevedZPolaBajtov(this.citaj(prevOffset, prevBlock.getVelkost()));

                        prevBlock.setOffsetNextVolny(-1);

                        this.uloz(prevOffset, prevBlock.prevedNaPoleBajtov());
                    }
                    else if (nextOffset != -1 && prevOffset == -1)
                    {
                        Block<T> nextBlock = new Block<>(this.blokovaciFaktorHlavnySubor, typ);
                        nextBlock.prevedZPolaBajtov(this.citaj(nextOffset, nextBlock.getVelkost()));

                        nextBlock.setOffsetPrevVolny(-1);

                        this.uloz(nextOffset, nextBlock.prevedNaPoleBajtov());
                        this.offsetPrvyVolnyHlavnySubor = nextOffset;

                        this.hlavnyPristupovySubor.setLength(offsetPoslednehoBlocku);
                    }
                    else if (nextOffset == -1 && prevOffset == -1)
                    {
                        // Jednoducho subor orezem
                        this.offsetPrvyVolnyHlavnySubor = -1;
                        this.hlavnyPristupovySubor.setLength(offsetPoslednehoBlocku);
                        break;
                    }
                }
                else
                {
                    break;
                }
            }
        }
        catch (Exception ex)
        {
            throw new RuntimeException("Chyba pri zmensovani suboru");
        }
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
