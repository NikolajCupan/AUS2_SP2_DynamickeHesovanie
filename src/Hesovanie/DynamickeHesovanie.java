package Hesovanie;

import Hesovanie.DynamickyZnakovyStrom.DynamickyZnakovyStrom;
import Rozhrania.IData;

public class DynamickeHesovanie<T extends IData>
{
    private DynamickyZnakovyStrom dynamickyZnakovyStrom;

    // Udava kolko Recordov sa nachadza v 1 Blocku
    private final int blokovaciFaktorHlavnySubor;
    private final int blokovaciFaktorPreplnujuciSubor;

    // Samotne subory
    private final Subor hlavnySubor;
    private final Subor preplnujuciSubor;


    public DynamickeHesovanie(int blokovaciFaktorHlavnySubor, int blokovaciFaktorPreplnujuciSubor, String nazovHlavnySubor, String nazovPreplnujuciSubor)
    {
        this.dynamickyZnakovyStrom = new DynamickyZnakovyStrom();

        this.blokovaciFaktorHlavnySubor = blokovaciFaktorHlavnySubor;
        this.blokovaciFaktorPreplnujuciSubor = blokovaciFaktorPreplnujuciSubor;

        this.hlavnySubor = new Subor(nazovHlavnySubor);
        this.preplnujuciSubor = new Subor(nazovPreplnujuciSubor);
    }

    public void vloz(T pridavany)
    {
        this.dynamickyZnakovyStrom.vloz(pridavany,
                                        this.blokovaciFaktorHlavnySubor, this.blokovaciFaktorPreplnujuciSubor,
                                        this.hlavnySubor, this.preplnujuciSubor);
    }
}
