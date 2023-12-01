package Hesovanie;

import Hesovanie.DigitalnyZnakovyStrom.DigitalnyZnakovyStrom;
import Rozhrania.IData;

public class DynamickeHesovanie<T extends IData>
{
    private final DigitalnyZnakovyStrom digitalnyZnakovyStrom;
    private final SpravcaSuborov spravcaSuborov;

    public DynamickeHesovanie(int blokovaciFaktorHlavnySubor, int blokovaciFaktorPreplnujuciSubor, String nazovHlavnySubor, String nazovPreplnujuciSubor)
    {
        this.digitalnyZnakovyStrom = new DigitalnyZnakovyStrom();
        this.spravcaSuborov = new SpravcaSuborov(blokovaciFaktorHlavnySubor, blokovaciFaktorPreplnujuciSubor,
                                                 nazovHlavnySubor, nazovPreplnujuciSubor);
    }

    public void vloz(T pridavany, Class<T> typ)
    {
        this.digitalnyZnakovyStrom.vloz(pridavany, typ, this.spravcaSuborov);
    }

    public T vyhladaj(T vyhladavany, Class<T> typ)
    {
        return this.digitalnyZnakovyStrom.vyhladaj(vyhladavany, typ, this.spravcaSuborov);
    }

    public T vymaz(T vymazavany, Class<T> typ)
    {
        return this.digitalnyZnakovyStrom.vymaz(vymazavany, typ, this.spravcaSuborov);
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
        this.digitalnyZnakovyStrom.vypisStrom(this.spravcaSuborov, typ);
    }

    public int getPocetElementov()
    {
        return this.digitalnyZnakovyStrom.getPocetElementov();
    }

    public void vymazSubory()
    {
        this.spravcaSuborov.vymazSubory();
    }

    public boolean suboryPrazdne()
    {
        return this.spravcaSuborov.suboryPrazdne();
    }
}
