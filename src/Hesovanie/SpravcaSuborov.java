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
    private long offsetPrvyVolnyPreplnujuciSubor;

    public SpravcaSuborov(int blokovaciFaktorHlavnySubor, int blokovaciFaktorPreplnujuciSubor, String nazovHlavnySubor, String nazovPreplnujuciSubor)
    {
        this.blokovaciFaktorHlavnySubor = blokovaciFaktorHlavnySubor;
        this.blokovaciFaktorPreplnujuciSubor = blokovaciFaktorPreplnujuciSubor;

        this.hlavnySubor = new File(nazovHlavnySubor);
        this.preplnujuciSubor = new File(nazovPreplnujuciSubor);

        this.offsetPrvyVolnyHlavnySubor = -1;
        this.offsetPrvyVolnyPreplnujuciSubor = -1;

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

    // Nacita pocetBajtov bajtov z Hlavneho suboru zacinajuc
    // od miesta urceneho offsetom
    public byte[] citajHlavnySubor(long offset, int pocetBajtov)
    {
        return this.citaj(offset, pocetBajtov, this.hlavnyPristupovySubor);
    }

    // Nacita pocetBajtov bajtov z Preplnujuceho suboru zacinajuc
    // od miesta urceneho offsetom
    public byte[] citajPreplnujuciSubor(long offset, int pocetBajtov)
    {
        return this.citaj(offset, pocetBajtov, this.preplnujuciPrustupovySubor);
    }

    private byte[] citaj(long offset, int pocetBajtov, RandomAccessFile pristupovySubor)
    {
        this.skontrolujOffset(offset);
        byte[] buffer = new byte[pocetBajtov];

        try
        {
            pristupovySubor.seek(offset);
            pristupovySubor.read(buffer);
        }
        catch (Exception ex)
        {
            throw new RuntimeException("Chyba pri citani zo suboru!");
        }

        return buffer;
    }

    // Ulozi poleBajtov do Hlavneho suboru zacinajuc od miesta urceneho offsetom,
    // pricom ak sa tam uz nachadzaju nejake data, tak tieto su prepisane
    public void ulozHlavnySubor(long offset, byte[] poleBajtov)
    {
        this.uloz(offset, poleBajtov, this.hlavnyPristupovySubor);
    }

    // Ulozi poleBajtov do Preplnujuceho suboru zacinajuc od miesta urceneho offsetom,
    // pricom ak sa tam uz nachadzaju nejake data, tak tieto su prepisane
    public void ulozPreplnujuciSubor(long offset, byte[] poleBajtov)
    {
        this.uloz(offset, poleBajtov, this.preplnujuciPrustupovySubor);
    }

    private void uloz(long offset, byte[] poleBajtov, RandomAccessFile pristupovySubor)
    {
        this.skontrolujOffset(offset);

        try
        {
            pristupovySubor.seek(offset);
            pristupovySubor.write(poleBajtov);
        }
        catch (Exception ex)
        {
            throw new RuntimeException("Chyba pri ukladani suboru!");
        }
    }

    public<T extends IData> long dajVolnyBlockHlavnySubor(Class<T> typ)
    {
        if (this.offsetPrvyVolnyHlavnySubor == -1)
        {
            // Nutne pridelit na konci suboru
            return this.getVelkostHlavnySubor();
        }

        Block<T> prvyVolnyBlock = new Block<>(this.blokovaciFaktorHlavnySubor, typ);
        prvyVolnyBlock.prevedZPolaBajtov(this.citajHlavnySubor(this.offsetPrvyVolnyHlavnySubor, prvyVolnyBlock.getVelkost()));

        if (prvyVolnyBlock.getOffsetNextVolny() == -1)
        {
            // Nema nasledovnika
            long pridelenyOffset = this.offsetPrvyVolnyHlavnySubor;
            this.offsetPrvyVolnyHlavnySubor = -1;
            return pridelenyOffset;
        }

        // Ma nasledovnika, nutne spracovat
        Block<T> nasledovnik = new Block<>(this.blokovaciFaktorHlavnySubor, typ);
        nasledovnik.prevedZPolaBajtov(this.citajHlavnySubor(prvyVolnyBlock.getOffsetNextVolny(), nasledovnik.getVelkost()));
        nasledovnik.setOffsetPrevVolny(-1);

        this.ulozHlavnySubor(prvyVolnyBlock.getOffsetNextVolny(), nasledovnik.prevedNaPoleBajtov());

        long pridelenyOffset = this.offsetPrvyVolnyHlavnySubor;
        this.offsetPrvyVolnyHlavnySubor = prvyVolnyBlock.getOffsetNextVolny();

        return pridelenyOffset;
    }

    public<T extends IData> long dajVolnyBlockPreplnujuciSubor(Class<T> typ)
    {
        if (this.offsetPrvyVolnyPreplnujuciSubor == -1)
        {
            // Nutne pridelit na konci suboru
            return this.getVelkostPreplnujuciSubor();
        }

        Block<T> prvyVolnyBlock = new Block<>(this.blokovaciFaktorPreplnujuciSubor, typ);
        prvyVolnyBlock.prevedZPolaBajtov(this.citajPreplnujuciSubor(this.offsetPrvyVolnyPreplnujuciSubor, prvyVolnyBlock.getVelkost()));

        if (prvyVolnyBlock.getOffsetNextVolny() == -1)
        {
            // Nema nasledovnika
            long pridelenyOffset = this.offsetPrvyVolnyPreplnujuciSubor;
            this.offsetPrvyVolnyPreplnujuciSubor = -1;
            return pridelenyOffset;
        }

        // Ma nasledovnika, nutne spracovat
        Block<T> nasledovnik = new Block<>(this.blokovaciFaktorPreplnujuciSubor, typ);
        nasledovnik.prevedZPolaBajtov(this.citajPreplnujuciSubor(prvyVolnyBlock.getOffsetNextVolny(), nasledovnik.getVelkost()));
        nasledovnik.setOffsetPrevVolny(-1);

        this.ulozPreplnujuciSubor(prvyVolnyBlock.getOffsetNextVolny(), nasledovnik.prevedNaPoleBajtov());

        long pridelenyOffset = this.offsetPrvyVolnyPreplnujuciSubor;
        this.offsetPrvyVolnyPreplnujuciSubor = prvyVolnyBlock.getOffsetNextVolny();

        return pridelenyOffset;
    }

    public<T extends IData> void uvolniBlockHlavnySubor(long novyPrvyOffset, Class<T> typ)
    {
        this.skontrolujOffset(novyPrvyOffset);

        if (this.offsetPrvyVolnyHlavnySubor == -1)
        {
            Block<T> prazdnyBlock = new Block<>(this.getBlokovaciFaktorHlavnySubor(), typ);
            this.ulozHlavnySubor(novyPrvyOffset, prazdnyBlock.prevedNaPoleBajtov());
            this.offsetPrvyVolnyHlavnySubor = novyPrvyOffset;
        }
        else
        {
            long staryPrvyOffset = this.offsetPrvyVolnyHlavnySubor;

            Block<T> novyPrvyVolny = new Block<>(this.getBlokovaciFaktorHlavnySubor(), typ);

            Block<T> staryPrvyVolny = new Block<>(this.getBlokovaciFaktorHlavnySubor(), typ);
            staryPrvyVolny.prevedZPolaBajtov(this.citajHlavnySubor(staryPrvyOffset, staryPrvyVolny.getVelkost()));

            staryPrvyVolny.setOffsetPrevVolny(novyPrvyOffset);
            novyPrvyVolny.setOffsetNextVolny(staryPrvyOffset);

            this.offsetPrvyVolnyHlavnySubor = novyPrvyOffset;

            this.ulozHlavnySubor(staryPrvyOffset, staryPrvyVolny.prevedNaPoleBajtov());
            this.ulozHlavnySubor(novyPrvyOffset, novyPrvyVolny.prevedNaPoleBajtov());
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

                long velkostSuboru = this.getVelkostHlavnySubor();
                long offsetPoslednehoBlocku = velkostSuboru - block.getVelkost();

                // Moznost zmensit subor
                Block<T> blockNaKonciSuboru = new Block<>(this.getBlokovaciFaktorHlavnySubor(), typ);
                blockNaKonciSuboru.prevedZPolaBajtov(this.citajHlavnySubor(offsetPoslednehoBlocku, blockNaKonciSuboru.getVelkost()));

                if (blockNaKonciSuboru.getPocetPlatnychZaznamov() == 0)
                {
                    // Block mozno zmazat
                    long nextOffset = blockNaKonciSuboru.getOffsetNextVolny();
                    long prevOffset = blockNaKonciSuboru.getOffsetPrevVolny();

                    if (nextOffset != -1 && prevOffset != -1)
                    {
                        Block<T> prevBlock = new Block<>(this.blokovaciFaktorHlavnySubor, typ);
                        Block<T> nextBlock = new Block<>(this.blokovaciFaktorHlavnySubor, typ);
                        prevBlock.prevedZPolaBajtov(this.citajHlavnySubor(prevOffset, prevBlock.getVelkost()));
                        nextBlock.prevedZPolaBajtov(this.citajHlavnySubor(nextOffset, nextBlock.getVelkost()));

                        prevBlock.setOffsetNextVolny(nextOffset);
                        nextBlock.setOffsetPrevVolny(prevOffset);

                        this.ulozHlavnySubor(prevOffset, prevBlock.prevedNaPoleBajtov());
                        this.ulozHlavnySubor(nextOffset, nextBlock.prevedNaPoleBajtov());

                        this.hlavnyPristupovySubor.setLength(offsetPoslednehoBlocku);
                    }
                    else if (nextOffset == -1 && prevOffset != -1)
                    {
                        Block<T> prevBlock = new Block<>(this.blokovaciFaktorHlavnySubor, typ);
                        prevBlock.prevedZPolaBajtov(this.citajHlavnySubor(prevOffset, prevBlock.getVelkost()));

                        prevBlock.setOffsetNextVolny(-1);

                        this.ulozHlavnySubor(prevOffset, prevBlock.prevedNaPoleBajtov());

                        this.hlavnyPristupovySubor.setLength(offsetPoslednehoBlocku);
                    }
                    else if (nextOffset != -1 && prevOffset == -1)
                    {
                        Block<T> nextBlock = new Block<>(this.blokovaciFaktorHlavnySubor, typ);
                        nextBlock.prevedZPolaBajtov(this.citajHlavnySubor(nextOffset, nextBlock.getVelkost()));

                        nextBlock.setOffsetPrevVolny(-1);

                        this.ulozHlavnySubor(nextOffset, nextBlock.prevedNaPoleBajtov());
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

    public<T extends IData> void vypisHlavnySubor(Class<T> typ)
    {
        Block<T> block = new Block<>(this.blokovaciFaktorHlavnySubor, typ);

        long curOffset = 0;
        int velkostBlocku = block.getVelkost();
        long velkostSuboru = this.getVelkostHlavnySubor();

        while (curOffset <= velkostSuboru - velkostBlocku)
        {
            block.prevedZPolaBajtov(this.citajHlavnySubor(curOffset, velkostBlocku));
            System.out.println("Offset " + curOffset + ":");
            System.out.println(block + "\n");

            curOffset += velkostBlocku;
        }
    }

    private void skontrolujOffset(long offset)
    {
        if (offset < 0)
        {
            throw new RuntimeException("Offset nemoze byt mensi ako 0!");
        }
    }

    private long getVelkostHlavnySubor()
    {
        return this.hlavnySubor.length();
    }

    private long getVelkostPreplnujuciSubor()
    {
        return this.preplnujuciSubor.length();
    }

    public int getBlokovaciFaktorHlavnySubor()
    {
        return this.blokovaciFaktorHlavnySubor;
    }

    public int getBlokovaciFaktorPreplnujuciSubor()
    {
        return this.blokovaciFaktorPreplnujuciSubor;
    }
}
