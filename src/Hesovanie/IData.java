package Hesovanie;

import java.util.BitSet;

public interface IData extends IRecord
{
    boolean jeRovnaky(IRecord zaznam);
    BitSet getHash();
}
