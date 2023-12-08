package Hesovanie;

import Rozhrania.IData;

import java.io.File;
import java.io.RandomAccessFile;

public class SpravcaSuborov
{
    private enum Subor
    {
        hlavnySubor,
        preplnujuciSubor
    }

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
            // Vytvorenie prveho volneho Blocku
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

        this.skusZmensitSubor(typ, this.blokovaciFaktorHlavnySubor, this.hlavnySubor, this.hlavnyPristupovySubor, Subor.hlavnySubor);
    }

    public<T extends IData> void uvolniBlockPreplnujuciSubor(long novyPrvyOffset, Class<T> typ)
    {
        this.skontrolujOffset(novyPrvyOffset);

        if (this.offsetPrvyVolnyPreplnujuciSubor == -1)
        {
            // Vytvorenie prveho volneho Blocku
            Block<T> prazdnyBlock = new Block<>(this.getBlokovaciFaktorPreplnujuciSubor(), typ);
            this.ulozPreplnujuciSubor(novyPrvyOffset, prazdnyBlock.prevedNaPoleBajtov());
            this.offsetPrvyVolnyPreplnujuciSubor = novyPrvyOffset;
        }
        else
        {
            long staryPrvyOffset = this.offsetPrvyVolnyPreplnujuciSubor;

            Block<T> novyPrvyVolny = new Block<>(this.getBlokovaciFaktorPreplnujuciSubor(), typ);

            Block<T> staryPrvyVolny = new Block<>(this.getBlokovaciFaktorPreplnujuciSubor(), typ);
            staryPrvyVolny.prevedZPolaBajtov(this.citajPreplnujuciSubor(staryPrvyOffset, staryPrvyVolny.getVelkost()));

            staryPrvyVolny.setOffsetPrevVolny(novyPrvyOffset);
            novyPrvyVolny.setOffsetNextVolny(staryPrvyOffset);

            this.offsetPrvyVolnyPreplnujuciSubor = novyPrvyOffset;

            this.ulozPreplnujuciSubor(staryPrvyOffset, staryPrvyVolny.prevedNaPoleBajtov());
            this.ulozPreplnujuciSubor(novyPrvyOffset, novyPrvyVolny.prevedNaPoleBajtov());
        }

        this.skusZmensitSubor(typ, this.blokovaciFaktorPreplnujuciSubor, this.preplnujuciSubor, this.preplnujuciPrustupovySubor, Subor.preplnujuciSubor);
    }

    private<T extends IData> void skusZmensitSubor(Class<T> typ, int blokovaciFaktor, File subor, RandomAccessFile pristupovySubor, Subor typSuboru)
    {
        try
        {
            while (true)
            {
                Block<T> block = new Block<>(blokovaciFaktor, typ);

                long velkostSuboru = subor.length();
                long offsetPoslednehoBlocku = velkostSuboru - block.getVelkost();

                Block<T> blockNaKonciSuboru = new Block<>(blokovaciFaktor, typ);
                blockNaKonciSuboru.prevedZPolaBajtov(this.citaj(offsetPoslednehoBlocku, blockNaKonciSuboru.getVelkost(), pristupovySubor));

                if (blockNaKonciSuboru.getPocetPlatnychZaznamov() == 0 &&
                    blockNaKonciSuboru.getOffsetPreplnujuciSubor() == -1)
                {
                    // Block mozno zmazat
                    long nextOffset = blockNaKonciSuboru.getOffsetNextVolny();
                    long prevOffset = blockNaKonciSuboru.getOffsetPrevVolny();

                    if (nextOffset != -1 && prevOffset != -1)
                    {
                        // Block ma predchodcu aj nasledovnika,
                        // prerobim zretazenie a subor orezem
                        Block<T> prevBlock = new Block<>(blokovaciFaktor, typ);
                        Block<T> nextBlock = new Block<>(blokovaciFaktor, typ);
                        prevBlock.prevedZPolaBajtov(this.citaj(prevOffset, prevBlock.getVelkost(), pristupovySubor));
                        nextBlock.prevedZPolaBajtov(this.citaj(nextOffset, nextBlock.getVelkost(), pristupovySubor));

                        prevBlock.setOffsetNextVolny(nextOffset);
                        nextBlock.setOffsetPrevVolny(prevOffset);

                        this.uloz(prevOffset, prevBlock.prevedNaPoleBajtov(), pristupovySubor);
                        this.uloz(nextOffset, nextBlock.prevedNaPoleBajtov(), pristupovySubor);

                        // Realne orezenie suboru
                        pristupovySubor.setLength(offsetPoslednehoBlocku);
                    }
                    else if (nextOffset == -1 && prevOffset != -1)
                    {
                        // Block ma predchodcu, ale nema nasledovnika,
                        // jedna sa o posledny Block v zretazeni
                        Block<T> prevBlock = new Block<>(blokovaciFaktor, typ);
                        prevBlock.prevedZPolaBajtov(this.citaj(prevOffset, prevBlock.getVelkost(), pristupovySubor));

                        prevBlock.setOffsetNextVolny(-1);

                        this.uloz(prevOffset, prevBlock.prevedNaPoleBajtov(), pristupovySubor);

                        // Realne orezenie suboru
                        pristupovySubor.setLength(offsetPoslednehoBlocku);
                    }
                    else if (nextOffset != -1 && prevOffset == -1)
                    {
                        // Block ma nasledovnika, ale nema predchodcu,
                        // jedna sa o prvy Block v zretazeni
                        Block<T> nextBlock = new Block<>(blokovaciFaktor, typ);
                        nextBlock.prevedZPolaBajtov(this.citaj(nextOffset, nextBlock.getVelkost(), pristupovySubor));

                        nextBlock.setOffsetPrevVolny(-1);

                        this.uloz(nextOffset, nextBlock.prevedNaPoleBajtov(), pristupovySubor);

                        // Nutne aktualizovat offset prveho volneho Blocku
                        if (typSuboru == Subor.hlavnySubor)
                        {
                            this.offsetPrvyVolnyHlavnySubor = nextOffset;
                        }
                        else
                        {
                            this.offsetPrvyVolnyPreplnujuciSubor = nextOffset;
                        }

                        // Realne orezenie suboru
                        pristupovySubor.setLength(offsetPoslednehoBlocku);
                    }
                    else if (nextOffset == -1 && prevOffset == -1)
                    {
                        // Jedna sa o jediny volny Block, jednoducho subor orezem
                        if (typSuboru == Subor.hlavnySubor)
                        {
                            // Orezavam Hlavny subor
                            this.offsetPrvyVolnyHlavnySubor = -1;
                            this.hlavnyPristupovySubor.setLength(offsetPoslednehoBlocku);
                        }
                        else
                        {
                            // Orezavam Preplnujuci subor
                            this.offsetPrvyVolnyPreplnujuciSubor = -1;
                            this.preplnujuciPrustupovySubor.setLength(offsetPoslednehoBlocku);
                        }

                        // Neexistuje ziadny dalsi volny Block
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

    public<T extends IData> String getStringHlavnySubor(Class<T> typ)
    {
        Block<T> block = new Block<>(this.blokovaciFaktorHlavnySubor, typ);

        long curOffset = 0;
        int velkostBlocku = block.getVelkost();
        long velkostSuboru = this.getVelkostHlavnySubor();

        String vysledok = "";
        while (curOffset <= velkostSuboru - velkostBlocku)
        {
            block.prevedZPolaBajtov(this.citajHlavnySubor(curOffset, velkostBlocku));
            vysledok += "Offset " + curOffset + ":\n";
            vysledok += block + "\n";

            curOffset += velkostBlocku;
        }

        return vysledok;
    }

    public<T extends IData> String getStringPreplnujuciSubor(Class<T> typ)
    {
        Block<T> block = new Block<>(this.blokovaciFaktorPreplnujuciSubor, typ);

        long curOffset = 0;
        int velkostBlocku = block.getVelkost();
        long velkostSuboru = this.getVelkostPreplnujuciSubor();

        String vysledok = "";
        while (curOffset <= velkostSuboru - velkostBlocku)
        {
            block.prevedZPolaBajtov(this.citajPreplnujuciSubor(curOffset, velkostBlocku));
            vysledok += "Offset " + curOffset + ":\n";
            vysledok += block + "\n";

            curOffset += velkostBlocku;
        }

        return vysledok;
    }

    public<T extends IData> String getStringHlavnySuborZretazenie(Class<T> typ)
    {
        long curOffset = this.offsetPrvyVolnyHlavnySubor;

        String vysledok = "";
        while (curOffset != -1)
        {
            Block<T> curBlock = new Block<>(this.blokovaciFaktorHlavnySubor, typ);
            curBlock.prevedZPolaBajtov(this.citajHlavnySubor(curOffset, curBlock.getVelkost()));
            vysledok += "Offset " + curOffset + ":\n";
            vysledok += "- next offset: " + curBlock.getOffsetNextVolny() + "\n";
            vysledok += "- prev offset: " + curBlock.getOffsetPrevVolny() + "\n\n";

            curOffset = curBlock.getOffsetNextVolny();
        }

        return vysledok;
    }

    public<T extends IData> String getStringPreplnujuciSuborZretazenie(Class<T> typ)
    {
        long curOffset = this.offsetPrvyVolnyPreplnujuciSubor;

        String vysledok = "";
        while (curOffset != -1)
        {
            Block<T> curBlock = new Block<>(this.blokovaciFaktorPreplnujuciSubor, typ);
            curBlock.prevedZPolaBajtov(this.citajPreplnujuciSubor(curOffset, curBlock.getVelkost()));
            vysledok += "Offset " + curOffset + ":\n";
            vysledok += "Next offset: " + curBlock.getOffsetNextVolny() + "\n";
            vysledok += "Prev offset: " + curBlock.getOffsetPrevVolny() + "\n\n";

            curOffset = curBlock.getOffsetNextVolny();
        }

        return vysledok;
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

    public long getOffsetPrvyVolnyHlavnySubor()
    {
        return this.offsetPrvyVolnyHlavnySubor;
    }

    public long getOffsetPrvyVolnyPreplnujuciSubor()
    {
        return this.offsetPrvyVolnyPreplnujuciSubor;
    }

    public void zavriSubory()
    {
        try
        {
            this.hlavnyPristupovySubor.close();
            this.preplnujuciPrustupovySubor.close();
        }
        catch (Exception exception)
        {
            throw new RuntimeException("Chyba pri zatvarani Suborov!");
        }
    }

    public void vymazSubory()
    {
        try
        {
            this.hlavnyPristupovySubor.setLength(0);
            this.preplnujuciPrustupovySubor.setLength(0);

            this.hlavnySubor.delete();
            this.preplnujuciSubor.delete();
        }
        catch (Exception exception)
        {
           throw new RuntimeException("Chyba pri mazani Suborov!");
        }
    }

    public boolean suboryPrazdne()
    {
        if (this.hlavnySubor.length() != 0 || this.preplnujuciSubor.length() != 0)
        {
            return false;
        }

        return true;
    }

    public void inicializujOffsety(int offsetPrvyVolnyHlavnySubor, int offsetPrvyVolnyPreplnujuciSubor)
    {
        this.offsetPrvyVolnyHlavnySubor = offsetPrvyVolnyHlavnySubor;
        this.offsetPrvyVolnyPreplnujuciSubor = offsetPrvyVolnyPreplnujuciSubor;
    }
}
