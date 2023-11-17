package Hesovanie.DynamickyZnakovyStrom;

import Hesovanie.Subor;
import Rozhrania.IData;

import java.io.RandomAccessFile;
import java.util.BitSet;

public class DynamickyZnakovyStrom
{
    private Vrchol root;

    public DynamickyZnakovyStrom()
    {
        // Prvy Block zacina od zaciatku suboru
        this.root = new ExternyVrchol(0);
    }

    public<T extends IData> void vloz(T pridavany,
                                      int blokovaciFaktorHlavnySubor, int blokovaciFaktorPreplnujuciSubor,
                                      Subor hlavnySubor, Subor preplnujuciSubor)
    {
        if (this.root instanceof ExternyVrchol externyVrchol && externyVrchol.getPocetZaznamovBlock() < blokovaciFaktorHlavnySubor)
        {
            // Vlozim do korena
            externyVrchol.vloz(pridavany, blokovaciFaktorHlavnySubor, hlavnySubor);
        }
    }
}
