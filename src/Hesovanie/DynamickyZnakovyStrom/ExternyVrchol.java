package Hesovanie.DynamickyZnakovyStrom;

import Hesovanie.Block;
import Hesovanie.Subor;
import Rozhrania.IData;

public class ExternyVrchol extends Vrchol
{
    // Udava, na ktorom bajte zacina dany Block
    private long offset;
    private int pocetZaznamovBlock;

    public ExternyVrchol(long offset)
    {
        this.offset = offset;
        this.pocetZaznamovBlock = 0;
    }

    public int getPocetZaznamovBlock()
    {
        return this.pocetZaznamovBlock;
    }

    public<T extends IData> void vloz(T pridavany, int blokovaciFaktorHlavnySubor, Subor hlavnySubor)
    {
        try
        {
            // Inicializacia Blocku
            Block<T> block = new Block<>(blokovaciFaktorHlavnySubor, (T)pridavany.getClass().getDeclaredConstructor().newInstance());
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
}
