package Hesovanie.DynamickyZnakovyStrom;

import Hesovanie.Block;
import Hesovanie.Subor;
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
    public<T extends IData> void vlozBlock(Block<T> pridavanyBlock, Subor hlavnySubor, long offset)
    {
        this.offset = (this.offset == -1) ? offset : this.offset;
        this.pocetZaznamovBlock = pridavanyBlock.getPocetPlatnychZaznamov();

        try
        {
            hlavnySubor.uloz(this.offset, pridavanyBlock.prevedNaPoleBajtov());
        }
        catch (Exception ex)
        {
            throw new RuntimeException("Chyba pri vkladani Blocku do Externeho vrcholu!");
        }
    }

    // Vloz novy Zaznam do Block, novyOffset nastaveny
    // na hodnotu -1 znaci, ze sa pouzije aktualny offset
    public<T extends IData> void vloz(T pridavany, Class<T> typ,
                                      int blokovaciFaktorHlavnySubor, Subor hlavnySubor, long novyOffset)
    {
        this.offset = (this.offset == -1) ? novyOffset : this.offset;

        try
        {
            // Nacitanie Blocku do operacnej pamati
            Block<T> block = new Block<>(blokovaciFaktorHlavnySubor, typ);

            byte[] poleBajtovSubor = hlavnySubor.citaj(this.offset, block.getVelkost());
            block.prevedZPolaBajtov(poleBajtovSubor);

            // Aktualizovanie Blocku a ulozenie do Suboru
            block.vloz(pridavany);
            hlavnySubor.uloz(this.offset, block.prevedNaPoleBajtov());

            this.pocetZaznamovBlock++;
        }
        catch (Exception ex)
        {
            throw new RuntimeException("Chyba pri vkladani do Externeho vrcholu!");
        }
    }

    // Vrati cely Block
    public<T extends IData> Block<T> getBlock(Class<T> typ, int blokovaciFaktorHlavnySubor, Subor hlavnySubor)
    {
        try
        {
            // Nacitaj Blocku zo Suboru a vrat ho
            Block<T> block = new Block<>(blokovaciFaktorHlavnySubor, typ);

            byte[] poleBajtovSubor = hlavnySubor.citaj(this.offset, block.getVelkost());
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
