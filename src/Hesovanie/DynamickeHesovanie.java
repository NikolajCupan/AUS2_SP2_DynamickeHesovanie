package Hesovanie;

import Hesovanie.DynamickyZnakovyStrom.DynamickyZnakovyStrom;
import Rozhrania.IData;

public class DynamickeHesovanie<T extends IData>
{
    private final DynamickyZnakovyStrom dynamickyZnakovyStrom;
    private final SpravcaSuborov spravcaSuborov;

    public DynamickeHesovanie(int blokovaciFaktorHlavnySubor, int blokovaciFaktorPreplnujuciSubor, String nazovHlavnySubor, String nazovPreplnujuciSubor)
    {
        this.dynamickyZnakovyStrom = new DynamickyZnakovyStrom();
        this.spravcaSuborov = new SpravcaSuborov(blokovaciFaktorHlavnySubor, blokovaciFaktorPreplnujuciSubor,
                                                 nazovHlavnySubor, nazovPreplnujuciSubor);
    }

    public void vloz(T pridavany, Class<T> typ)
    {
        this.dynamickyZnakovyStrom.vloz(pridavany, typ, this.spravcaSuborov);
    }

    public T vyhladaj(T vyhladavany, Class<T> typ)
    {
        return this.dynamickyZnakovyStrom.vyhladaj(vyhladavany, typ, this.spravcaSuborov);
    }

    public T vymaz(T vymazavany, Class<T> typ)
    {
        return this.dynamickyZnakovyStrom.vymaz(vymazavany, typ, this.spravcaSuborov);
    }

    public void vypisHlavnySubor(Class<T> typ)
    {
        this.spravcaSuborov.vypisHlavnySubor(typ);
    }

    public void vypisPreplnujuciSubor(Class<T> typ)
    {
        this.spravcaSuborov.vypisPreplnujuciSubor(typ);
    }

    public void vypisStrom(Class<T> typ)
    {
        this.dynamickyZnakovyStrom.vypisStrom(this.spravcaSuborov, typ);
    }

    public int getPocetElementov()
    {
        return this.dynamickyZnakovyStrom.getPocetElementov();
    }
}
