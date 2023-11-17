package Hesovanie;

import Ostatne.Konstanty;
import Rozhrania.IData;
import Rozhrania.IRecord;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Block<T extends IData> implements IRecord
{
    private int blokovaciFaktor;

    private ArrayList<T> zaznamy;
    private int pocetPlatnychZaznamov;

    // Za ucelom volania operacii
    private final T dummyZaznam;

    public Block(int blokovaciFaktor, T dummyZaznam)
    {
        this.blokovaciFaktor = blokovaciFaktor;

        this.zaznamy = new ArrayList<>();
        this.pocetPlatnychZaznamov = 0;

        this.dummyZaznam = dummyZaznam;
    }

    // Pre ucely inicializacie z pola bajtov
    public Block(T dummyZaznam)
    {
        this.dummyZaznam = dummyZaznam;
    }

    public void vloz(T pridavany)
    {
        if (this.pocetPlatnychZaznamov >= this.blokovaciFaktor)
        {
            throw new RuntimeException("Block je plny, nemozno vlozit dalsi zaznam!");
        }

        this.zaznamy.add(pridavany);
        this.pocetPlatnychZaznamov++;
    }

    public T vyhladaj(T vyhladavany)
    {
        for (int i = 0; i < this.pocetPlatnychZaznamov; i++)
        {
            if (this.zaznamy.get(i).jeRovnaky(vyhladavany))
            {
                return this.zaznamy.get(i);
            }
        }

        // Dany Zaznam sa v Blocku nenachadza
        return null;
    }

    public T vymaz(T vymazavany)
    {
        int index = -1;

        for (int i = 0; i < this.pocetPlatnychZaznamov; i++)
        {
            if (this.zaznamy.get(i).jeRovnaky(vymazavany))
            {
                index = i;
                break;
            }
        }

        if (index == -1)
        {
            // Dany Zaznam sa v Blocku nenachadza
            return null;
        }

        Collections.swap(this.zaznamy, index, this.pocetPlatnychZaznamov - 1);
        this.pocetPlatnychZaznamov--;
        return this.zaznamy.remove(this.pocetPlatnychZaznamov);
    }

    // Kolko bajtov zabera 1 Block
    @Override
    public int getVelkost()
    {
        int velkost = 0;

        // Blokovaci faktor - int
        velkost += Konstanty.VELKOST_INT;

        // Pocet platnych Zaznamov - int
        velkost += Konstanty.VELKOST_INT;

        // Velkost samotnych Zaznamov, uvazujem o maximalnej moznej velkosti,
        // tym padom do uvahy beriem blokovaci faktor, nie pocet platnych zaznamov
        velkost += this.dummyZaznam.getVelkost() * this.blokovaciFaktor;

        return velkost;
    }

    @Override
    public byte[] prevedNaPoleBajtov()
    {
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteOutputStream);

        try
        {
            dataOutputStream.writeInt(this.blokovaciFaktor);
            dataOutputStream.writeInt(this.pocetPlatnychZaznamov);

            for (int i = 0; i < this.pocetPlatnychZaznamov; i++)
            {
                dataOutputStream.write(this.zaznamy.get(i).prevedNaPoleBajtov());
            }

            // Zvysne miesto v Blocku dopln dummy znakmi
            int pocetNeplatnychZaznamov = this.blokovaciFaktor - this.pocetPlatnychZaznamov;
            int velkostZaznamu = this.dummyZaznam.getVelkost();
            int pocetBajtov = velkostZaznamu * pocetNeplatnychZaznamov;

            byte[] dummyPoleBajov = new byte[pocetBajtov];
            Arrays.fill(dummyPoleBajov, (byte)Konstanty.DUMMY_ZNAK);

            dataOutputStream.write(dummyPoleBajov);

            return byteOutputStream.toByteArray();
        }
        catch (Exception ex)
        {
            String zaznamNazov = this.dummyZaznam.getClass().getName();
            throw new IllegalStateException("Konverzia Blocku s typom " + zaznamNazov + " na pole bajtov sa nepodarila!");
        }
    }

    @Override
    public void prevedZPolaBajtov(byte[] poleBajtov)
    {
        ByteArrayInputStream byteInputStream = new ByteArrayInputStream(poleBajtov);
        DataInputStream dataInputStream = new DataInputStream(byteInputStream);

        try
        {
            this.blokovaciFaktor = dataInputStream.readInt();
            if (this.getVelkost() != poleBajtov.length)
            {
                throw new RuntimeException("Velkost pola bajtov sa nezhoduje s velkostou Blocku!");
            }

            this.pocetPlatnychZaznamov = dataInputStream.readInt();

            this.zaznamy = new ArrayList<>();
            int velkostZaznamu = this.dummyZaznam.getVelkost();

            for (int i = 0; i < this.pocetPlatnychZaznamov; i++)
            {
                byte[] poleBajtovZaznam = dataInputStream.readNBytes(velkostZaznamu);

                // Nova instancia typu T a jej inicializacia pomocou nacitaneho pola bajtov
                T novyZaznam = (T)this.dummyZaznam.getClass().getDeclaredConstructor().newInstance();
                novyZaznam.prevedZPolaBajtov(poleBajtovZaznam);
                this.zaznamy.add(novyZaznam);
            }
        }
        catch (Exception ex)
        {
            throw new IllegalStateException("Konverzia pola bajtov na Block sa nepodarila!");
        }
    }
}
