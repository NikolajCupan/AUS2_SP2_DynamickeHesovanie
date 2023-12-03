package Testovanie;

import Hesovanie.DynamickeHesovanie;
import Objekty.Parcela;
import Objekty.Suradnica;
import Ostatne.Generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Tester
{
    // Dynamicke hesovanie
    private static final int BF_HS_MIN = 1;
    private static final int BF_HS_MAX = 10;

    private static final int BF_PS_MIN = 1;
    private static final int BF_PS_MAX = 10;

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
            int blokovaciFaktorHlavnySubor = this.randomInt(BF_HS_MIN, BF_HS_MAX);
            int blokovaciFaktorPreplnujuciSubor = this.randomInt(BF_PS_MIN, BF_PS_MAX);

            long seedReplikacia = this.random.nextLong();
            Generator generator = new Generator(1, 1, 1, 0, 0, 0, 0, 1, seedReplikacia);

            System.out.println("Spusta sa replikacia cislo: " + i + ", BF_HS: " + blokovaciFaktorHlavnySubor + ", BF_PS: " + blokovaciFaktorPreplnujuciSubor+ ", seed: " + seedReplikacia);

            this.testZakladneOperacie01(blokovaciFaktorHlavnySubor, blokovaciFaktorPreplnujuciSubor, generator);
            this.testZakladneOperacie02(blokovaciFaktorHlavnySubor, blokovaciFaktorPreplnujuciSubor);
            this.testZakladneOperacie03(blokovaciFaktorHlavnySubor, blokovaciFaktorPreplnujuciSubor);
            this.testZakladneOperacie04(blokovaciFaktorHlavnySubor, blokovaciFaktorPreplnujuciSubor);
            this.testZakladneOperacie05(blokovaciFaktorHlavnySubor, blokovaciFaktorPreplnujuciSubor);
        }
    }

    private void testZakladneOperacie01(int blokovaciFaktorHlavnySubor, int blokovaciFaktorPreplnujuciSubor, Generator generator)
    {
        // Vlastne parametre testu
        final int ZACIATOCNA_VELKOST = 1000;
        final int POCET_OPERACII = 1000;

        final int PRST_VYHLADAJ = 30;
        final int PRST_VLOZ = 40;
        final int PRST_VYMAZ = 30;

        ArrayList<Parcela> zoznam = new ArrayList<>();

        DynamickeHesovanie<Parcela> dh = new DynamickeHesovanie<>(blokovaciFaktorHlavnySubor, blokovaciFaktorPreplnujuciSubor, NAZOV_HS, NAZOV_PS, Parcela.class);
        dh.vymazSubory();
        if (!dh.suboryPrazdne())
        {
            throw new RuntimeException("Subory na zaciatku testu nie su prazdne!");
        }

        for (int i = 0; i < ZACIATOCNA_VELKOST; i++)
        {
            Parcela parcela = generator.getParcela();
            zoznam.add(parcela);
            dh.vloz(parcela);
        }

        if (zoznam.size() != dh.getPocetElementov())
        {
            throw new RuntimeException("Prvotna velkost dynamickeho hesovania a zoznamu nie je rovnaka!");
        }

        for (int i = 0; i < POCET_OPERACII; i++)
        {
            int nahoda = this.randomInt(0, PRST_VYHLADAJ + PRST_VLOZ + PRST_VYMAZ);

            if (nahoda < PRST_VYHLADAJ)
            {
                // Vyhladavanie
                if (zoznam.size() == 0)
                {
                    continue;
                }

                int index = this.randomInt(0, zoznam.size() - 1);
                Parcela parcela = zoznam.get(index);
                Parcela najdenaDh = dh.vyhladaj(parcela);

                if (!najdenaDh.jeRovnaky(parcela))
                {
                    throw new RuntimeException("Najdene elementy sa nezhoduju!");
                }
            }
            else if (nahoda < PRST_VLOZ)
            {
                // Vkladanie
                Parcela parcela = generator.getParcela();
                zoznam.add(parcela);
                dh.vloz(parcela);

                if (zoznam.size() != dh.getPocetElementov())
                {
                    throw new RuntimeException("Pocet elementov po vkladani nie je rovnaky!");
                }
            }
            else
            {
                // Vymazavanie
                if (zoznam.size() == 0)
                {
                    continue;
                }

                int velkostPred = dh.getPocetElementov();

                int index = this.randomInt(0, zoznam.size() - 1);
                Parcela parcela = zoznam.remove(index);
                Parcela vymazanaDh = dh.vymaz(parcela);

                int velkostPo = dh.getPocetElementov();

                if (!vymazanaDh.jeRovnaky(parcela))
                {
                    throw new RuntimeException("Vymazane elementy sa nezhoduju!");
                }

                if (velkostPred != velkostPo + 1)
                {
                    throw new RuntimeException("Velkost po vymazavani nie je o 1 mensia!");
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
        final int ZACIATOCNA_VELKOST = 1000;

        ArrayList<Parcela> zoznam = new ArrayList<>();

        DynamickeHesovanie<Parcela> dh = new DynamickeHesovanie<>(blokovaciFaktorHlavnySubor, blokovaciFaktorPreplnujuciSubor, NAZOV_HS, NAZOV_PS, Parcela.class);
        dh.vymazSubory();
        if (!dh.suboryPrazdne())
        {
            throw new RuntimeException("Subory na zaciatku testu nie su prazdne!");
        }

        for (int i = 0; i < ZACIATOCNA_VELKOST; i++)
        {
            Parcela parcela = new Parcela(i, String.valueOf(i), new Suradnica(), new Suradnica());
            zoznam.add(parcela);
        }

        if (dh.getPocetElementov() != 0)
        {
            throw new RuntimeException("Pocet elementov v Dynamickom hesovani nie je nulovy!");
        }

        Collections.shuffle(zoznam, this.random);
        int curPocet = 0;

        for (Parcela parcela : zoznam)
        {
            dh.vloz(parcela);
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

        Collections.shuffle(zoznam, this.random);
        for (Parcela parcela : zoznam)
        {
            Parcela najdenaDh = dh.vyhladaj(parcela);

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
        final int ZACIATOCNA_VELKOST = 1000;
        final int MIN_POCET_BITOV_HASH = 0;
        final int MAX_POCET_BITOV_HASH = 100;

        int pocetBitovHash = this.randomInt(MIN_POCET_BITOV_HASH, MAX_POCET_BITOV_HASH);

        ArrayList<Integer> zoznamID = new ArrayList<>();
        for (int i = 0; i < ZACIATOCNA_VELKOST; i++)
        {
            zoznamID.add(i);
        }
        Collections.shuffle(zoznamID, this.random);

        ArrayList<Dummy> zoznam = new ArrayList<>();

        DynamickeHesovanie<Dummy> dh = new DynamickeHesovanie<>(blokovaciFaktorHlavnySubor, blokovaciFaktorPreplnujuciSubor, NAZOV_HS, NAZOV_PS, Dummy.class);
        dh.vymazSubory();
        if (!dh.suboryPrazdne())
        {
            throw new RuntimeException("Subory na zaciatku testu nie su prazdne!");
        }

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

        Collections.shuffle(zoznam, this.random);
        int curPocet = 0;

        for (Dummy dummy : zoznam)
        {
            dh.vloz(dummy);
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

        Collections.shuffle(zoznam, this.random);
        for (Dummy dummy : zoznam)
        {
            Dummy najdenaDh = dh.vyhladaj(dummy);

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

    // Test, ci subory na konci ostanu prazdne
    private void testZakladneOperacie04(int blokovaciFaktorHlavnySubor, int blokovaciFaktorPreplnujuciSubor)
    {
        // Vlastne parametre testu
        final int ZACIATOCNA_VELKOST = 1000;
        final int MIN_POCET_BITOV_HASH = 0;
        final int MAX_POCET_BITOV_HASH = 5;

        int pocetBitovHash = this.randomInt(MIN_POCET_BITOV_HASH, MAX_POCET_BITOV_HASH);

        DynamickeHesovanie<Dummy> dh = new DynamickeHesovanie<>(blokovaciFaktorHlavnySubor, blokovaciFaktorPreplnujuciSubor, NAZOV_HS, NAZOV_PS, Dummy.class);
        dh.vymazSubory();
        if (!dh.suboryPrazdne())
        {
            throw new RuntimeException("Subory na zaciatku testu nie su prazdne!");
        }

        ArrayList<Dummy> zoznam = new ArrayList<>();
        for (int i = 0; i < ZACIATOCNA_VELKOST; i++)
        {
            zoznam.add(new Dummy(i, pocetBitovHash));
        }
        Collections.shuffle(zoznam, this.random);

        for (int i = 0; i < ZACIATOCNA_VELKOST; i++)
        {
            dh.vloz(zoznam.get(i));
        }

        if (dh.getPocetElementov() != ZACIATOCNA_VELKOST)
        {
            throw new RuntimeException("Prvotna velkost Dynamickeho hesovania nie je spravna!");
        }

        Collections.shuffle(zoznam, this.random);
        for (int i = 0; i < ZACIATOCNA_VELKOST; i++)
        {
            Dummy dummy = zoznam.get(i);
            Dummy vymazanaDh = dh.vymaz(dummy);

            if (!vymazanaDh.jeRovnaky(dummy))
            {
                throw new RuntimeException("Vymazane elementy sa nezhoduju!");
            }
        }

        if (dh.getPocetElementov() != 0)
        {
            throw new RuntimeException("Dynamicke hesovanie nie je prazdne!");
        }

        if (!dh.suboryPrazdne())
        {
            throw new RuntimeException("Subory nie su prazdne!");
        }
    }

    private void testZakladneOperacie05(int blokovaciFaktorHlavnySubor, int blokovaciFaktorPreplnujuciSubor)
    {
        // Vlastne parametre testu
        final int ZACIATOCNA_VELKOST = 1000;
        final int MIN_POCET_BITOV_HASH = 0;
        final int MAX_POCET_BITOV_HASH = 5;

        int pocetBitovHash = this.randomInt(MIN_POCET_BITOV_HASH, MAX_POCET_BITOV_HASH);

        DynamickeHesovanie<Dummy> dh = new DynamickeHesovanie<>(blokovaciFaktorHlavnySubor, blokovaciFaktorPreplnujuciSubor, NAZOV_HS, NAZOV_PS, Dummy.class);
        dh.vymazSubory();
        if (!dh.suboryPrazdne())
        {
            throw new RuntimeException("Subory na zaciatku testu nie su prazdne!");
        }

        ArrayList<Dummy> zoznam = new ArrayList<>();
        for (int i = 0; i < ZACIATOCNA_VELKOST; i++)
        {
            Dummy vytvoreny = new Dummy(i, pocetBitovHash);
            zoznam.add(vytvoreny);

            Dummy najdenyPred = dh.vyhladaj(vytvoreny);
            if (najdenyPred != null)
            {
                throw new RuntimeException("Zaznam bol najdeny pred vlozenim!");
            }
            dh.vloz(vytvoreny);

            Dummy najdenyPo = dh.vyhladaj(vytvoreny);
            if (najdenyPo == null)
            {
                throw new RuntimeException("Zaznam nebol najdeny po vlozeni!");
            }
        }

        for (int i = 0; i < ZACIATOCNA_VELKOST; i++)
        {
            Dummy zmazany = zoznam.remove(zoznam.size() - 1);

            Dummy predMazanim = dh.vyhladaj(zmazany);
            if (predMazanim == null)
            {
                throw new RuntimeException("Zaznam nebol najdeny pred mazanim!");
            }

            Dummy realneZmazany = dh.vymaz(zmazany);
            if (realneZmazany == null)
            {
                throw new RuntimeException("Zaznam nebol vymazany!");
            }

            Dummy poMazani = dh.vyhladaj(zmazany);
            if (poMazani != null)
            {
                throw new RuntimeException("Zaznam bol najdeny po vymazani!");
            }
        }

        if (dh.getPocetElementov() != 0)
        {
            throw new RuntimeException("Dynamicke hesovanie nie je prazdne!");
        }

        if (!dh.suboryPrazdne())
        {
            throw new RuntimeException("Subory nie su prazdne!");
        }
    }

    private void vycistiKonzolu()
    {
        for (int i = 0; i < 50; i++)
        {
            System.out.println();
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
