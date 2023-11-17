import Hesovanie.Block;
import Hesovanie.DynamickeHesovanie;
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

        DynamickeHesovanie<Parcela> dh = new DynamickeHesovanie<>(3, 10,"hlavny", "preplnujuci");

        Parcela p1 = new Parcela(1, "prva", suradnica1, suradnica2);
        Parcela p2 = new Parcela(2, "druha", suradnica1, suradnica2);
        Parcela p3 = new Parcela(3, "tretia", suradnica1, suradnica2);
        dh.vloz(p1);
        dh.vloz(p2);
        dh.vloz(p3);
    }
}
