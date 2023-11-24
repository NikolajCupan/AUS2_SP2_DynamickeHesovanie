package Testovanie;

import Hesovanie.DynamickeHesovanie;
import Objekty.Parcela;
import Ostatne.Generator;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class Tester
{
    // Dynamicke hesovanie
    private static final int BF_HS_SUBOR_MIN = 1;
    private static final int BF_HS_SUBOR_MAX = 1;

    private static final int BF_PS_SUBOR_MIN = 1;
    private static final int BF_PS_SUBOR_MAX = 1;

    // Subory
    private static final String NAZOV_HS = "hlavny";
    private static final String NAZOV_PS = "preplnujuci";

    // Hranice quad stromu a generovanych suradnic
    private static final double GEN_X_MIN = -100;
    private static final double GEN_Y_MIN = -100;
    private static final double GEN_X_MAX = 100;
    private static final double GEN_Y_MAX = 100;

    // Hranice pre faktor zmensenia vygenerovanych suradnic
    private static final double FAKTOR_ZMENSENIA_MIN = 1;
    private static final double FAKTOR_ZMENSENIA_MAX = 10000;

    private static final int DEAULT_MAX_UROVEN_MIN = 0;
    private static final int DEFAULT_MAX_UROVEN_MAX = 15;

    private final Random random;

    public Tester()
    {
        this.random = new Random();
    }

    public Tester(long seed)
    {
        this.random = new Random();
        this.random.setSeed(seed);
    }

    public void replikacie(int opakovania)
    {
        for (int i = 0; i < opakovania; i++)
        {
            this.vymazSubory();

            int blokovaciFaktorHlavnySubor = this.randomInt(BF_HS_SUBOR_MIN, BF_HS_SUBOR_MAX);
            int blokovaciFaktorPreplnujuciSubor = this.randomInt(BF_PS_SUBOR_MIN, BF_PS_SUBOR_MAX);

            double x1 = this.randomDouble(GEN_X_MIN, GEN_X_MAX);
            double y1 = this.randomDouble(GEN_Y_MIN, GEN_Y_MAX);
            double x2 = this.randomDouble(GEN_X_MIN, GEN_X_MAX);
            double y2 = this.randomDouble(GEN_Y_MIN, GEN_Y_MAX);

            // Hranice najvacsieho quadu
            double minX = Math.min(x1, x2);
            double minY = Math.min(y1, y2);
            double maxX = Math.max(x1, x2);
            double maxY = Math.max(y1, y2);

            // O kolko su vygenerovane nehnutelnosti zmensene pred vkladanim
            double faktorZmensenia = this.randomDouble(FAKTOR_ZMENSENIA_MIN, FAKTOR_ZMENSENIA_MAX);
            int maxUroven = this.randomInt(DEAULT_MAX_UROVEN_MIN, DEFAULT_MAX_UROVEN_MAX);

            long seedReplikacia = this.random.nextLong();
            Generator generator = new Generator(1, 1, 1, minX, minY, maxX, maxY, faktorZmensenia, seedReplikacia);

            System.out.println("Spusta sa replikacia cislo: " + i + ", seed: " + seedReplikacia);

            this.testZakladneOperacie(blokovaciFaktorHlavnySubor, blokovaciFaktorPreplnujuciSubor, generator);
        }
    }

    private void testZakladneOperacie(int blokovaciFaktorHlavnySubor, int blokovaciFaktorPreplnujuciSubor, Generator generator)
    {
        // Vlastne parametre testu
        final int ZACIATOCNA_VELKOST = 50000;
        final int POCET_OPERACII = 10000;

        final int PRST_VYHLADAJ = 50;
        final int PRST_VLOZ = 50;

        ArrayList<Parcela> zoznam = new ArrayList<>();
        DynamickeHesovanie<Parcela> dh = new DynamickeHesovanie<>(blokovaciFaktorHlavnySubor, blokovaciFaktorPreplnujuciSubor, NAZOV_HS, NAZOV_PS);

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
            int nahoda = this.randomInt(0, PRST_VYHLADAJ + PRST_VLOZ);

            if (nahoda < PRST_VYHLADAJ)
            {
                // Vyhladavanie
                int index = this.randomInt(0, zoznam.size() - 1);
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

    private void vymazSubory()
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

    private double randomDouble(double min, double max)
    {
        return min + (max - min) * this.random.nextDouble();
    }

    private int randomInt(int min, int max)
    {
        if (min == max)
        {
            return min;
        }

        return min + this.random.nextInt(max - min + 1);
    }
}
