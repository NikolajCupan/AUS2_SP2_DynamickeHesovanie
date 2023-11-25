import Hesovanie.DynamickeHesovanie;
import Objekty.Parcela;
import Objekty.Suradnica;
import Ostatne.Generator;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class Main
{
    private static final String NAZOV_HS = "hlavny";
    private static final String NAZOV_PS = "preplnujuci";

    public static void main(String[] args)
    {
        vymazSubory();

        /*
        Generator generator = new Generator(1, 1, 1, 0, 0, 0, 0, 1, 0);

        DynamickeHesovanie<Parcela> dh = new DynamickeHesovanie<>(10, 10, NAZOV_HS, NAZOV_PS);
        ArrayList<Parcela> zoznam = new ArrayList<>();

        for (int i = 0; i < 500000; i++)
        {
            Parcela parcela = generator.getParcela();
            dh.vloz(parcela, Parcela.class);
            zoznam.add(parcela);
        }

        if (dh.getPocetElementov() != zoznam.size())
        {
            throw new RuntimeException("Rozdielna zakladna velkost");
        }

        Collections.shuffle(zoznam);
        for (int i = 0; i < 500000; i++)
        {
            Parcela zoznamZmazana = zoznam.remove(zoznam.size() - 1);
            Parcela dhZmazana = dh.vymaz(zoznamZmazana, Parcela.class);

            if (!zoznamZmazana.jeRovnaky(dhZmazana))
            {
                throw new RuntimeException("Rozdielne zmazane parcely");
            }

            if (dh.getPocetElementov() != zoznam.size())
            {
                throw new RuntimeException("Rozdielna velkost po mazani");
            }

            if (i % 10000 == 0)
            {
                System.out.println(i);
            }
        }

        if (dh.getPocetElementov() != zoznam.size())
        {
            throw new RuntimeException("Rozdielna velkost na konci");
        }
        */

        Suradnica suradnica = new Suradnica();

        Generator generator = new Generator(1, 1, 1, 0, 0, 100, 100, 1);
        DynamickeHesovanie<Parcela> dh = new DynamickeHesovanie<>(2, 2, "hlavny", "preplnujuci");

        Parcela p1 = new Parcela(1, "1", suradnica, suradnica);
        Parcela p2 = new Parcela(2, "2", suradnica, suradnica);
        Parcela p3 = new Parcela(3, "3", suradnica, suradnica);
        Parcela p4 = new Parcela(4, "4", suradnica, suradnica);
        Parcela p5 = new Parcela(5, "5", suradnica, suradnica);
        Parcela p6 = new Parcela(6, "6", suradnica, suradnica);
        Parcela p7 = new Parcela(7, "7", suradnica, suradnica);
        Parcela p8 = new Parcela(8, "8", suradnica, suradnica);

        dh.vloz(p1, Parcela.class);
        dh.vloz(p2, Parcela.class);
        dh.vloz(p3, Parcela.class);
        dh.vloz(p4, Parcela.class);
        dh.vloz(p5, Parcela.class);
        dh.vloz(p6, Parcela.class);
        dh.vloz(p7, Parcela.class);
        dh.vloz(p8, Parcela.class);

        dh.vymaz(p1, Parcela.class);
        dh.vymaz(p2, Parcela.class);
        dh.vymaz(p3, Parcela.class);
        dh.vymaz(p4, Parcela.class);
        dh.vymaz(p5, Parcela.class);
        dh.vymaz(p6, Parcela.class);
        dh.vymaz(p7, Parcela.class);
        dh.vymaz(p8, Parcela.class);

        int x = 100;
    }

    public static void vymazSubory()
    {
        File hlavnySubor = new File(NAZOV_HS);
        File preplnujuciSubor = new File(NAZOV_PS);

        if (hlavnySubor.exists())
        {
            hlavnySubor.delete();
        }

        if (preplnujuciSubor.exists())
        {
            preplnujuciSubor.delete();
        }
    }
}
