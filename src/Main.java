import Objekty.Parcela;
import Objekty.Suradnica;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

public class Main
{
    public static void main(String[] args) throws IOException
    {
        Parcela zapisovana = new Parcela(9418349, "moja p", new Suradnica(3183, 7877), new Suradnica(-3838, -83883));

        try (FileOutputStream stream = new FileOutputStream("test"))
        {
            stream.write(zapisovana.prevedNaPoleBajtov());
        }

        File subor = new File("test");
        byte[] poleBajtov = Files.readAllBytes(subor.toPath());
        Parcela nacitana = new Parcela();
        nacitana.prevedZPolaBajtov(poleBajtov);

        boolean rovnake = zapisovana.equals(nacitana);

        int x = 100;
    }
}
