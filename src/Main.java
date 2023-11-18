import Hesovanie.Block;
import Hesovanie.DynamickeHesovanie;
import Hesovanie.DynamickyZnakovyStrom.ExternyVrchol;
import Hesovanie.Subor;
import Objekty.Nehnutelnost;
import Objekty.Parcela;
import Objekty.Suradnica;
import Ostatne.Generator;
import com.sun.source.tree.TryTree;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.BitSet;

public class Main
{
    public static void main(String[] args) throws IOException
    {
        Suradnica s = new Suradnica();

        DynamickeHesovanie<Parcela> h = new DynamickeHesovanie<>(1, 2, "hlavny", "preplnujuci");
        Parcela p1 = new Parcela(1, "1", s, s);
        Parcela p2 = new Parcela(2, "2", s, s);
        h.vloz(p1, Parcela.class);
        h.vloz(p2, Parcela.class);
    }
}
