package Hesovanie.DynamickyZnakovyStrom;

import Hesovanie.Block;
import Hesovanie.SpravcaSuborov;
import Rozhrania.IData;

public class ExternyVrchol extends Vrchol
{
    // Udava, na ktorom bajte zacina dany Block
    private long offset;

    // Vratane Zaznamov v Preplnujucich blockoch
    private int pocetZaznamovBlock;
    private int pocetPreplnujucichBlockov;

    public ExternyVrchol()
    {
        this.offset = -1;
        this.pocetZaznamovBlock = 0;
        this.pocetPreplnujucichBlockov = 0;

        this.otec = null;
    }

    // Vloz na dany offset cely Block
    public<T extends IData> void vlozBlock(Block<T> pridavanyBlock, Class<T> typ, SpravcaSuborov spravcaSuborov, long forcedOffset)
    {
        if (forcedOffset != -1)
        {
            this.offset = forcedOffset;
        }
        else
        {
            this.offset = (this.offset == -1) ? spravcaSuborov.dajVolnyBlockHlavnySubor(typ) : this.offset;
        }

        this.pocetZaznamovBlock = pridavanyBlock.getPocetPlatnychZaznamov();

        try
        {
            spravcaSuborov.ulozHlavnySubor(this.offset, pridavanyBlock.prevedNaPoleBajtov());
        }
        catch (Exception ex)
        {
            throw new RuntimeException("Chyba pri vkladani Blocku do Externeho vrcholu!");
        }
    }

    public<T extends IData> void vloz(T pridavany, Class<T> typ, SpravcaSuborov spravcaSuborov)
    {
        this.offset = (this.offset == -1) ? spravcaSuborov.dajVolnyBlockHlavnySubor(typ) : this.offset;

        try
        {
            // Nacitanie Blocku do operacnej pamati
            Block<T> block = new Block<>(spravcaSuborov.getBlokovaciFaktorHlavnySubor(), typ);

            byte[] poleBajtovSubor = spravcaSuborov.citajHlavnySubor(this.offset, block.getVelkost());
            block.prevedZPolaBajtov(poleBajtovSubor);

            // Aktualizovanie Blocku a ulozenie do Suboru
            boolean vytvorenyNovyPreplnujuciBlock = block.vloz(pridavany, spravcaSuborov);
            if (vytvorenyNovyPreplnujuciBlock)
            {
                this.pocetPreplnujucichBlockov++;
            }

            spravcaSuborov.ulozHlavnySubor(this.offset, block.prevedNaPoleBajtov());

            this.pocetZaznamovBlock++;
        }
        catch (Exception ex)
        {
            throw new RuntimeException("Chyba pri vkladani do Externeho vrcholu!");
        }
    }

    // Vrati cely Block
    public<T extends IData> Block<T> getBlock(Class<T> typ, SpravcaSuborov spravcaSuborov)
    {
        if (this.offset == -1)
        {
            return null;
        }

        try
        {
            // Nacitaj Block zo Suboru a vrat ho
            Block<T> block = new Block<>(spravcaSuborov.getBlokovaciFaktorHlavnySubor(), typ);

            byte[] poleBajtovSubor = spravcaSuborov.citajHlavnySubor(this.offset, block.getVelkost());
            block.prevedZPolaBajtov(poleBajtovSubor);

            return block;
        }
        catch (Exception ex)
        {
            throw new RuntimeException("Chyba pri nacitani Blocku z Externeho vrcholu!");
        }
    }

    public long getOffset()
    {
        return this.offset;
    }

    public int getPocetZaznamovBlock()
    {
        return this.pocetZaznamovBlock;
    }

    public void setOffset(long offset)
    {
        this.offset = offset;
    }

    public void setPocetZaznamovBlock(int pocetZaznamovBlock)
    {
        this.pocetZaznamovBlock = pocetZaznamovBlock;
    }

    public<T extends IData> String naString(SpravcaSuborov spravcaSuborov, Class<T> typ)
    {
        String string = "Externy vrchol:\n";
        string += "- offset: " + this.offset + "\n";
        string += "- pocet zaznamov block: " + this.pocetZaznamovBlock + "\n";
        string += "- pocet preplnujucich blockov: " + this.pocetPreplnujucichBlockov + "\n";

        if (this.offset != -1)
        {
            Block<T> block = new Block<>(spravcaSuborov.getBlokovaciFaktorHlavnySubor(), typ);
            block.prevedZPolaBajtov(spravcaSuborov.citajHlavnySubor(this.offset, block.getVelkost()));
            for (T zaznam : block.getZaznamy())
            {
                string += zaznam;
            }
        }

        return string;
    }
}
