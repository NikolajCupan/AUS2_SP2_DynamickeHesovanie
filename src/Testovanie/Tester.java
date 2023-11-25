package Testovanie;

import Hesovanie.DynamickeHesovanie;
import Objekty.Parcela;
import Objekty.Suradnica;
import Ostatne.Generator;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Tester
{
    // Dynamicke hesovanie
    private static final int BF_HS_MIN = 1;
    private static final int BF_HS_MAX = 100;

    private static final int BF_PS_MIN = 1;
    private static final int BF_PS_MAX = 100;

    // Subory
    private static final String NAZOV_HS = "hlavny";
    private static final String NAZOV_PS = "preplnujuci";

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

            int blokovaciFaktorHlavnySubor = this.randomInt(BF_HS_MIN, BF_HS_MAX);
            int blokovaciFaktorPreplnujuciSubor = this.randomInt(BF_PS_MIN, BF_PS_MAX);

            long seedReplikacia = this.random.nextLong();
            Generator generator = new Generator(1, 1, 1, 0, 0, 0, 0, 1, seedReplikacia);

            System.out.println("Spusta sa replikacia cislo: " + i + ", BF_HS: " + blokovaciFaktorHlavnySubor + ", BF_PS: " + blokovaciFaktorPreplnujuciSubor+ ", seed: " + seedReplikacia);

            this.testZakladneOperacie01(blokovaciFaktorHlavnySubor, blokovaciFaktorPreplnujuciSubor, generator);
            this.testZakladneOperacie02(blokovaciFaktorHlavnySubor, blokovaciFaktorPreplnujuciSubor);
            this.testZakladneOperacie03(blokovaciFaktorHlavnySubor, blokovaciFaktorPreplnujuciSubor);
        }
    }

    private void testZakladneOperacie01(int blokovaciFaktorHlavnySubor, int blokovaciFaktorPreplnujuciSubor, Generator generator)
    {
        // Vlastne parametre testu
        final int ZACIATOCNA_VELKOST = 10000;
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

    private void testZakladneOperacie02(int blokovaciFaktorHlavnySubor, int blokovaciFaktorPreplnujuciSubor)
    {
        // Vlastne parametre testu
        final int ZACIATOCNA_VELKOST = 10000;

        ArrayList<Parcela> zoznam = new ArrayList<>();
        DynamickeHesovanie<Parcela> dh = new DynamickeHesovanie<>(blokovaciFaktorHlavnySubor, blokovaciFaktorPreplnujuciSubor, NAZOV_HS, NAZOV_PS);

        for (int i = 0; i < ZACIATOCNA_VELKOST; i++)
        {
            Parcela parcela = new Parcela(i, String.valueOf(i), new Suradnica(), new Suradnica());
            zoznam.add(parcela);
        }

        if (dh.getPocetElementov() != 0)
        {
            throw new RuntimeException("Pocet elementov v Dynamickom hesovani nie je nulovy!");
        }

        Collections.shuffle(zoznam);
        int curPocet = 0;

        for (Parcela parcela : zoznam)
        {
            dh.vloz(parcela, Parcela.class);
            curPocet++;

            if (curPocet != dh.getPocetElementov())
            {
                throw new RuntimeException("Pocet elementov v Dynamickom hesovani sa nezhoduje s ocakavanym poctom!");
            }
        }

        if (zoznam.size() != dh.getPocetElementov())
        {
            throw new RuntimeException("Pocet elementov po vykonani vsetkych vkladani nie je rovnaky!");
        }

        Collections.shuffle(zoznam);
        for (Parcela parcela : zoznam)
        {
            Parcela najdenaDh = dh.vyhladaj(parcela, Parcela.class);

            if (!parcela.jeRovnaky(najdenaDh))
            {
                throw new RuntimeException("Najdene elementy sa nezhoduju!");
            }
        }

        if (zoznam.size() != dh.getPocetElementov())
        {
            throw new RuntimeException("Pocet elementov v Dynamickom hesovani a Zozname sa nezhoduje!");
        }
    }

    private void testZakladneOperacie03(int blokovaciFaktorHlavnySubor, int blokovaciFaktorPreplnujuciSubor)
    {
        // Vlastne parametre testu
        final int ZACIATOCNA_VELKOST = 10000;
        final int MIN_POCET_BITOV_HASH = 1;
        final int MAX_POCET_BITOV_HASH = 100;

        int pocetBitovHash = this.randomInt(MIN_POCET_BITOV_HASH, MAX_POCET_BITOV_HASH);

        ArrayList<Integer> zoznamID = new ArrayList<>();
        for (int i = 0; i < ZACIATOCNA_VELKOST; i++)
        {
            zoznamID.add(i);
        }
        Collections.shuffle(zoznamID);

        ArrayList<Dummy> zoznam = new ArrayList<>();
        DynamickeHesovanie<Dummy> dh = new DynamickeHesovanie<>(blokovaciFaktorHlavnySubor, blokovaciFaktorPreplnujuciSubor, NAZOV_HS, NAZOV_PS);

        for (int i = 0; i < ZACIATOCNA_VELKOST; i++)
        {
            Integer ID = zoznamID.get(i);
            Dummy dummy = new Dummy(ID, pocetBitovHash);
            zoznam.add(dummy);
        }

        if (dh.getPocetElementov() != 0)
        {
            throw new RuntimeException("Pocet elementov v Dynamickom hesovani nie je nulovy!");
        }

        Collections.shuffle(zoznam);
        int curPocet = 0;

        for (Dummy dummy : zoznam)
        {
            dh.vloz(dummy, Dummy.class);
            curPocet++;

            if (curPocet != dh.getPocetElementov())
            {
                throw new RuntimeException("Pocet elementov v Dynamickom hesovani sa nezhoduje s ocakavanym poctom!");
            }
        }

        if (zoznam.size() != dh.getPocetElementov())
        {
            throw new RuntimeException("Pocet elementov po vykonani vsetkych vkladani nie je rovnaky!");
        }

        Collections.shuffle(zoznam);
        for (Dummy dummy : zoznam)
        {
            Dummy najdenaDh = dh.vyhladaj(dummy, Dummy.class);

            if (!dummy.jeRovnaky(najdenaDh))
            {
                throw new RuntimeException("Najdene elementy sa nezhoduju!");
            }
        }

        if (zoznam.size() != dh.getPocetElementov())
        {
            throw new RuntimeException("Pocet elementov v Dynamickom hesovani a Zozname sa nezhoduje!");
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
