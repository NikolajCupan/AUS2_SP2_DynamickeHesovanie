import Hesovanie.Block;
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

        Generator generator = new Generator(1, 1, 1, 0, 0, 100, 100, 1, 0);

        Block<Parcela> block1 = new Block<>(5, new Parcela());
        block1.vloz(generator.getParcela());
        block1.vloz(generator.getParcela());
        block1.vloz(generator.getParcela());
        block1.vloz(generator.getParcela());
        block1.vloz(generator.getParcela());

        Parcela p1 = new Parcela(2, "*", suradnica1, suradnica2);
        Parcela p2 = new Parcela(3, "*", suradnica1, suradnica2);
        block1.vymaz(p1);
        block1.vymaz(p2);

        try (FileOutputStream stream = new FileOutputStream("test"))
        {
            stream.write(block1.prevedNaPoleBajtov());
        }

        int i = generator.getParcela().getVelkost();
        int j = block1.getVelkost();

        File subor = new File("test");
        byte[] poleBajtov = Files.readAllBytes(subor.toPath());

        Block<Parcela> block2 = new Block<>(new Parcela());
        block2.prevedZPolaBajtov(poleBajtov);

        int x = 100;
    }
}
