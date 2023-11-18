import Hesovanie.Block;
import Hesovanie.DynamickeHesovanie;
import Hesovanie.DynamickyZnakovyStrom.ExternyVrchol;
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
        while (true)
        {
            try
            {
                Suradnica suradnica1 = new Suradnica();
                Suradnica suradnica2 = new Suradnica();
                Generator generator = new Generator(1, 1, 1, 0, 0, 100, 100, 1);
                DynamickeHesovanie<Parcela> dh = new DynamickeHesovanie<>(10, 10, "hlavny", "preplnujuci");
                for (int i = 0; i < 1; i++)
                {
                    dh.vloz(generator.getParcela());
                }
                System.out.println(dh.getPocetElemtov());
                BitSet s = new BitSet();
                s.set(100);
                boolean a = s.get(3338);
                int x = 100;
            }
            catch (Exception ex)
            {
            }
        }
    }
}
