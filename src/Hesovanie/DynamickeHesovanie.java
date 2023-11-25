package Hesovanie;

import Hesovanie.DynamickyZnakovyStrom.DynamickyZnakovyStrom;
import Rozhrania.IData;

import java.io.RandomAccessFile;

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

    public int getPocetElementov()
    {
        return this.dynamickyZnakovyStrom.getPocetElementov();
    }

    public void vypis(Class<T> typ)
    {
        RandomAccessFile subor = this.spravcaSuborov.getHlavnyPristupovySubor();
        Block<T> block = new Block<>(this.spravcaSuborov.getBlokovaciFaktorPreplnujuciSubor(), typ);
        int velkostBlocku = block.getVelkost();
    }
}
