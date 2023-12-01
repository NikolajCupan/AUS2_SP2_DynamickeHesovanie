package Hesovanie;

import Hesovanie.DigitalnyZnakovyStrom.DigitalnyZnakovyStrom;
import Rozhrania.IData;

public class DynamickeHesovanie<T extends IData>
{
    private final DigitalnyZnakovyStrom digitalnyZnakovyStrom;
    private final SpravcaSuborov spravcaSuborov;

    private final Class<T> typ;

    public DynamickeHesovanie(int blokovaciFaktorHlavnySubor, int blokovaciFaktorPreplnujuciSubor, String nazovHlavnySubor, String nazovPreplnujuciSubor, Class<T> typ)
    {
        if (blokovaciFaktorHlavnySubor <= 0 || blokovaciFaktorPreplnujuciSubor <= 0)
        {
            throw new RuntimeException("Blokovaci faktor musi byt kladne cele cislo!");
        }

        this.digitalnyZnakovyStrom = new DigitalnyZnakovyStrom();
        this.spravcaSuborov = new SpravcaSuborov(blokovaciFaktorHlavnySubor, blokovaciFaktorPreplnujuciSubor,
                                                 nazovHlavnySubor, nazovPreplnujuciSubor);

        this.typ = typ;
    }

    public void vloz(T pridavany)
    {
        this.digitalnyZnakovyStrom.vloz(pridavany, this.typ, this.spravcaSuborov);
    }

    public T vyhladaj(T vyhladavany)
    {
        return this.digitalnyZnakovyStrom.vyhladaj(vyhladavany, this.typ, this.spravcaSuborov);
    }

    public T vymaz(T vymazavany)
    {
        return this.digitalnyZnakovyStrom.vymaz(vymazavany, this.typ, this.spravcaSuborov);
    }

    public void vypisHlavnySubor()
    {
        this.spravcaSuborov.vypisHlavnySubor(this.typ);
    }

    public void vypisPreplnujuciSubor()
    {
        this.spravcaSuborov.vypisPreplnujuciSubor(this.typ);
    }

    public void vypisZretazenie()
    {
        this.spravcaSuborov.vypisZretazenie(this.typ);
    }

    public void vypisStrom()
    {
        this.digitalnyZnakovyStrom.vypisStrom(this.spravcaSuborov, this.typ);
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
