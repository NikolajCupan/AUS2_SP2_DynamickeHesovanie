import Hesovanie.DynamickeHesovanie;
import Objekty.Parcela;
import Objekty.Suradnica;
import Ostatne.Generator;
import Testovanie.Tester;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class Main
{
    private static final String NAZOV_HS = "hlavny";
    private static final String NAZOV_PS = "preplnujuci";

    public static void main(String[] args)
    {
        vymazSubory();

        /*
        DynamickeHesovanie<Parcela> dh = new DynamickeHesovanie<>(2, 2, NAZOV_HS, NAZOV_PS);
        Parcela p1 = new Parcela(1, "1", new Suradnica(), new Suradnica());
        Parcela p2 = new Parcela(2, "2", new Suradnica(), new Suradnica());
        Parcela p3 = new Parcela(3, "3", new Suradnica(), new Suradnica());
        Parcela p4 = new Parcela(4, "4", new Suradnica(), new Suradnica());
        Parcela p5 = new Parcela(5, "5", new Suradnica(), new Suradnica());
        Parcela p6 = new Parcela(6, "6", new Suradnica(), new Suradnica());
        Parcela p7 = new Parcela(7, "7", new Suradnica(), new Suradnica());
        Parcela p8 = new Parcela(8, "8", new Suradnica(), new Suradnica());

        dh.vloz(p1, Parcela.class);
        dh.vloz(p2, Parcela.class);
        dh.vloz(p3, Parcela.class);
        dh.vloz(p4, Parcela.class);
        dh.vloz(p5, Parcela.class);
        dh.vloz(p6, Parcela.class);
        dh.vloz(p7, Parcela.class);
        dh.vloz(p8, Parcela.class);

        dh.vypisPreplnujuciSubor(Parcela.class);
        */
        Generator generator = new Generator(1, 1, 1, 0, 0, 0, 0, 1, 1);
        Random random = new Random(2);

        // Vlastne parametre testu
        final int ZACIATOCNA_VELKOST = 1000;
        final int POCET_OPERACII = 1000;

        final int PRST_VYHLADAJ = 50;
        final int PRST_VLOZ = 50;

        final int BF_HS = 111;
        final int BF_PS = 11;

        ArrayList<Parcela> zoznam = new ArrayList<>();
        DynamickeHesovanie<Parcela> dh = new DynamickeHesovanie<>(BF_HS, BF_PS, NAZOV_HS, NAZOV_PS);

        for (int i = 0; i < ZACIATOCNA_VELKOST; i++)
        {
            Parcela parcela = generator.getParcela();
            zoznam.add(parcela);
            dh.vloz(parcela, Parcela.class);
        }

        if (zoznam.size() != dh.getPocetElementov())
        {
            throw new RuntimeException("Prvotna velkost dynamickeho hesovania a zoznamu nie je rovnaka!");
        }

        for (int i = 0; i < POCET_OPERACII; i++)
        {
            int nahoda = randomInt(random, 0, PRST_VYHLADAJ + PRST_VLOZ);

            if (nahoda < PRST_VYHLADAJ)
            {
                // Vyhladavanie
                int index = randomInt(random, 0, zoznam.size() - 1);
                Parcela parcela = zoznam.get(index);
                Parcela najdenaDh = dh.vyhladaj(parcela, Parcela.class);

                if (!najdenaDh.jeRovnaky(parcela))
                {
                    throw new RuntimeException("Najdene elementy sa nezhoduju!");
                }
            }
            else
            {
                // Vkladanie
                Parcela parcela = generator.getParcela();
                zoznam.add(parcela);
                dh.vloz(parcela, Parcela.class);

                if (zoznam.size() != dh.getPocetElementov())
                {
                    throw new RuntimeException("Pocet elementov po vkladani nie je rovnaky!");
                }
            }
        }

        if (zoznam.size() != dh.getPocetElementov())
        {
            throw new RuntimeException("Pocet elementov po vykonani vsetkych operacii nie je rovnaky!");
        }
    }

    private static double randomDouble(Random random, double min, double max)
    {
        return min + (max - min) * random.nextDouble();
    }

    private static int randomInt(Random random, int min, int max)
    {
        if (min == max)
        {
            return min;
        }

        return min + random.nextInt(max - min + 1);
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
