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
        this.offset = offset;
        this.pocetZaznamovBlock = pridavanyBlock.getPocetPlatnychZaznamov();

        try
        {
            hlavnySubor.uloz(this.offset, pridavanyBlock.prevedNaPoleBajtov());
        }
        catch (Exception ex)
        {
            throw new RuntimeException("Chyba pri vkladani do Externeho vrcholu!");
        }
    }

    // Vloz novy Zaznam do Blocku
    public<T extends IData> void vloz(T pridavany, int blokovaciFaktorHlavnySubor, Subor hlavnySubor)
    {
        try
        {
            T dummyZaznam = (T)pridavany.getClass().getDeclaredConstructor().newInstance();

            // Inicializacia Blocku
            Block<T> block = new Block<>(blokovaciFaktorHlavnySubor, dummyZaznam);

            if (this.offset == -1)
            {
                // Block nema alokovane miesto v Subore,
                // nastav sa na koniec suboru
                this.offset = hlavnySubor.getVelkostSuboru();
            }

            byte[] poleBajtovSubor = hlavnySubor.citaj(this.offset, block.getVelkost());
            block.prevedZPolaBajtov(poleBajtovSubor);

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
    public<T extends IData> Block<T> getBlock(T dummyZaznam, int blokovaciFaktorHlavnySubor, Subor hlavnySubor)
    {
        try
        {
            Block<T> block = new Block<>(blokovaciFaktorHlavnySubor, dummyZaznam);

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
