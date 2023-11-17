package Hesovanie;

import Rozhrania.IData;

import java.io.File;

public class DynamickeHesovanie<T extends IData>
{
    // Udava kolko Recordov sa nachadza v 1 Blocku
    private final int blokovaciFaktorHlavnySubor;
    private final int blokovaciFaktorPreplnujuciSubor;

    // Samotne subory
    private final File hlavnySubor;
    private final File preplnujuciSubor;

    public DynamickeHesovanie(int blokovaciFaktorHlavnySubor, int blokovaciFaktorPreplnujuciSubor, String nazovHlavnySubor, String nazovPreplnujuciSubor)
    {
        this.blokovaciFaktorHlavnySubor = blokovaciFaktorHlavnySubor;
        this.blokovaciFaktorPreplnujuciSubor = blokovaciFaktorPreplnujuciSubor;

        this.hlavnySubor = new File(nazovHlavnySubor);
        this.preplnujuciSubor = new File(nazovPreplnujuciSubor);

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
        }
        catch (Exception ex)
        {
            throw new RuntimeException("Chyba pri vytvarani suborov v Dynamickom Hesovani!");
        }
    }
}
