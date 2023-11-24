package Hesovanie.DynamickyZnakovyStrom;

import Hesovanie.Block;
import Hesovanie.SpravcaSuborov;
import Rozhrania.IData;

public class ExternyVrchol extends Vrchol
{
    // Udava, na ktorom bajte zacina dany Block
    private long offset;
    private int pocetZaznamovBlock;

    public ExternyVrchol()
    {
        this.offset = -1;
        this.pocetZaznamovBlock = 0;
    }

    // Vloz na dany offset cely Block
    public<T extends IData> void vlozBlock(Block<T> pridavanyBlock, SpravcaSuborov spravcaSuborov, long forcedOffset)
    {
        if (forcedOffset != -1)
        {
            this.offset = forcedOffset;
        }
        else
        {
            this.offset = (this.offset == -1) ? spravcaSuborov.getNovyOffset() : this.offset;
        }

        this.pocetZaznamovBlock = pridavanyBlock.getPocetPlatnychZaznamov();

        try
        {
            spravcaSuborov.uloz(this.offset, pridavanyBlock.prevedNaPoleBajtov());
        }
        catch (Exception ex)
        {
            throw new RuntimeException("Chyba pri vkladani Blocku do Externeho vrcholu!");
        }
    }

    public<T extends IData> void vloz(T pridavany, Class<T> typ, SpravcaSuborov spravcaSuborov)
    {
        this.offset = (this.offset == -1) ? spravcaSuborov.getNovyOffset() : this.offset;

        try
        {
            // Nacitanie Blocku do operacnej pamati
            Block<T> block = new Block<>(spravcaSuborov.getBlokovaciFaktorHlavnySubor(), typ);

            byte[] poleBajtovSubor = spravcaSuborov.citaj(this.offset, block.getVelkost());
            block.prevedZPolaBajtov(poleBajtovSubor);

            // Aktualizovanie Blocku a ulozenie do Suboru
            block.vloz(pridavany);
            spravcaSuborov.uloz(this.offset, block.prevedNaPoleBajtov());

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
        try
        {
            // Nacitaj Block zo Suboru a vrat ho
            Block<T> block = new Block<>(spravcaSuborov.getBlokovaciFaktorHlavnySubor(), typ);

            byte[] poleBajtovSubor = spravcaSuborov.citaj(this.offset, block.getVelkost());
            block.prevedZPolaBajtov(poleBajtovSubor);

            return block;
        }
        catch (Exception ex)
        {
            throw new RuntimeException("Chyba pri vkladani do Externeho vrcholu!");
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
}
