package Hesovanie;

import Hesovanie.DigitalnyZnakovyStrom.DigitalnyZnakovyStrom;
import Rozhrania.IData;

public class DynamickeHesovanie<T extends IData>
{
    private DigitalnyZnakovyStrom digitalnyZnakovyStrom;
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

    // Metoda najde Zaznam pomocou primarneho kluca,
    // a aktualizuje ho (nahradi ho parametrom aktualizovany)
    public boolean aktualizuj(T aktualizovany)
    {
        return this.digitalnyZnakovyStrom.aktualizuj(aktualizovany, this.typ, this.spravcaSuborov);
    }

    // Navratova hodnota metody:
    // True -> pridavany prvok bol uspesne vlozeny
    // False -> pridavany prvok uz v strukture existuje, preto nebol vlozeny
    public boolean vloz(T pridavany)
    {
        T najdeny = this.digitalnyZnakovyStrom.vyhladaj(pridavany, this.typ, this.spravcaSuborov);
        if (najdeny != null)
        {
            return false;
        }

        this.digitalnyZnakovyStrom.vloz(pridavany, this.typ, this.spravcaSuborov);
        return true;
    }

    public T vyhladaj(T vyhladavany)
    {
        return this.digitalnyZnakovyStrom.vyhladaj(vyhladavany, this.typ, this.spravcaSuborov);
    }

    public T vymaz(T vymazavany)
    {
        return this.digitalnyZnakovyStrom.vymaz(vymazavany, this.typ, this.spravcaSuborov);
    }

    public void inicializujOffsety(int offsetPrvyVolnyHlavnySubor, int offsetPrvyVolnyPreplnujuciSubor)
    {
        this.spravcaSuborov.inicializujOffsety(offsetPrvyVolnyHlavnySubor, offsetPrvyVolnyPreplnujuciSubor);
    }

    public String getStringHlavnySubor()
    {
        return this.spravcaSuborov.getStringHlavnySubor(this.typ);
    }

    public String getStringPreplnujuciSubor()
    {
        return this.spravcaSuborov.getStringPreplnujuciSubor(this.typ);
    }

    public String getStringHlavnySuborZretazenie()
    {
        return this.spravcaSuborov.getStringHlavnySuborZretazenie(this.typ);
    }

    public String getStringPreplnujuciSuborZretazenie()
    {
        return this.spravcaSuborov.getStringPreplnujuciSuborZretazenie(this.typ);
    }

    public String getStringStrom()
    {
        return this.digitalnyZnakovyStrom.getStringStrom(this.spravcaSuborov, this.typ);
    }

    public SpravcaSuborov getSpravcaSuborov()
    {
        return this.spravcaSuborov;
    }

    public DigitalnyZnakovyStrom getDigitalnyZnakovyStrom()
    {
        return this.digitalnyZnakovyStrom;
    }

    public int getPocetElementov()
    {
        return this.digitalnyZnakovyStrom.getPocetElementov();
    }

    public void setDigitalnyZnakovyStrom(DigitalnyZnakovyStrom digitalnyZnakovyStrom)
    {
        this.digitalnyZnakovyStrom = digitalnyZnakovyStrom;
    }

    public void zavriSubory()
    {
        this.spravcaSuborov.zavriSubory();
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
