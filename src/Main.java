import Hesovanie.Block;
import Objekty.Nehnutelnost;
import Objekty.Parcela;
import Objekty.Suradnica;
import Ostatne.Generator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

public class Main
{
    public static void main(String[] args) throws IOException
    {
        Suradnica suradnica1 = new Suradnica();
        Suradnica suradnica2 = new Suradnica();

        Generator generator = new Generator(1, 1, 1, 0, 0, 100, 100, 1);

        Block<Nehnutelnost> block1 = new Block<>(3, new Nehnutelnost());
        block1.vloz(generator.getNehnutelnost());
        block1.vloz(generator.getNehnutelnost());
        block1.vloz(generator.getNehnutelnost());

        try (FileOutputStream stream = new FileOutputStream("test"))
        {
            stream.write(block1.prevedNaPoleBajtov());
        }

        File subor = new File("test");
        byte[] poleBajtov = Files.readAllBytes(subor.toPath());
        Block<Nehnutelnost> block2 = new Block<>(new Nehnutelnost());
        block2.prevedZPolaBajtov(poleBajtov);

        Nehnutelnost hladana = new Nehnutelnost(2, 1, "a", suradnica1, suradnica2);
        Nehnutelnost najdena = block2.vyhladaj(hladana);

        int x = 100;
    }
}
