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
        Generator generator = new Generator(1, 1, 1, 0, 0, 100, 100, 1, 0);
        Block<Parcela> block = new Block<>(10, new Parcela());

        Subor subor = new Subor("test");
        byte[] poleBajtov = subor.citaj(0, block.getVelkost());

        block.prevedZPolaBajtov(poleBajtov);
        block = block;
        /*
        for (int i = 0; i < 8; i++)
        {
            block.vloz(generator.getParcela());
        }
        */
    }
}
