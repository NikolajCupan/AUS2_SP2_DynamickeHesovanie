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
    private ArrayList<T> zaznamy;

    private final int maxPocetZaznamov;
    private int pocetPlatnychZaznamov;

    // Za ucelom volania operacii a vytvorenia
    // instancii pri nacitani z pola bajtov
    private final T dummyZaznam;

    private long offsetPreplnujuciSubor;
    private long offsetPrevVolny;
    private long offsetNextVolny;

    public Block(int maxPocetZaznamov, Class<T> typ)
    {
        this.maxPocetZaznamov = maxPocetZaznamov;

        this.zaznamy = new ArrayList<>();
        this.pocetPlatnychZaznamov = 0;

        this.offsetPreplnujuciSubor = -1;
        this.offsetPrevVolny = -1;
        this.offsetNextVolny = -1;

        try
        {
            this.dummyZaznam = typ.getDeclaredConstructor().newInstance();
        }
        catch (Exception ex)
        {
            throw new RuntimeException("Chyba pri vytvarani dummy Zaznamu v Blocku!");
        }
    }

    // Zaznam musi byt vlozeny iba do daneho Blocku,
    // ak sa tam nezmesti, tak je vyvolana vynimka
    public void forceVloz(T pridavany)
    {
        if (this.pocetPlatnychZaznamov >= this.maxPocetZaznamov)
        {
            throw new RuntimeException("V blocku nie je miesto pre dalsi Zaznam!");
        }

        this.zaznamy.add(pridavany);
        this.pocetPlatnychZaznamov++;
    }

    // Pokus o vlozenie zaznamu do daneho Blocku
    // True -> zaznam bol uspesne vlozeny
    // False -> zaznam sa vlozit nepodarilo
    public boolean skusVlozit(T pridavany)
    {
        if (this.pocetPlatnychZaznamov >= this.maxPocetZaznamov)
        {
            return false;
        }

        this.zaznamy.add(pridavany);
        this.pocetPlatnychZaznamov++;
        return true;
    }

    // Navratova hodnota metody:
    // True -> bol vytvoreny novy Preplnujici block
    // False -> nebol vytvoreny novy Preplnujuci block
    public boolean vloz(T pridavany, SpravcaSuborov spravcaSuborov)
    {
        if (this.pocetPlatnychZaznamov >= this.maxPocetZaznamov)
        {
            // Nutne vlozit do Preplnujuceho suboru
            return this.vlozDoPreplnujucehoSuboru(pridavany, spravcaSuborov);
        }
        else
        {
            // Mozno vlozit do Hlavneho suboru
            this.zaznamy.add(pridavany);
            this.pocetPlatnychZaznamov++;

            // Nebolo nutne vytvorit novy Preplnujuci block
            return false;
        }
    }

    // Navratova hodnota metody:
    // True -> bol vytvoreny novy Preplnujici block
    // False -> nebol vytvoreny novy Preplnujuci block
    private boolean vlozDoPreplnujucehoSuboru(T pridavany, SpravcaSuborov spravcaSuborov)
    {
        Block<T> curBlock = this;
        long odlozenyOffset = -1;

        while (true)
        {
            if (curBlock.offsetPreplnujuciSubor != -1)
            {
                // Block ma prideleny Preplnujuci Block
                Block<T> preplnujuciBlock = (Block<T>)new Block<>(spravcaSuborov.getBlokovaciFaktorPreplnujuciSubor(), pridavany.getClass());
                preplnujuciBlock.prevedZPolaBajtov(spravcaSuborov.citajPreplnujuciSubor(curBlock.offsetPreplnujuciSubor, preplnujuciBlock.getVelkost()));

                boolean uspesneVlozene = this.skusVlozitDoPreplnujucehoBlocku(pridavany, preplnujuciBlock, spravcaSuborov);
                if (uspesneVlozene)
                {
                    spravcaSuborov.ulozPreplnujuciSubor(curBlock.offsetPreplnujuciSubor, preplnujuciBlock.prevedNaPoleBajtov());

                    // Zaznam bol vlozeny do existujuceho Preplnujuceho blocku
                    return false;
                }

                // Uchovam si offset daneho Preplnujuceho blocku
                odlozenyOffset = curBlock.getOffsetPreplnujuciSubor();

                // V danom Preplnujucom Blocku nie je dostatok miesta
                // pre dalsi Zaznam, tym padom je nutne cely proces opakovat
                curBlock = preplnujuciBlock;
            }
            else
            {
                // Block potrebuje dalsi Preplnujuci subor
                curBlock.offsetPreplnujuciSubor = spravcaSuborov.dajVolnyBlockPreplnujuciSubor(pridavany.getClass());
                Block<T> preplnujuciBlock = (Block<T>)new Block<>(spravcaSuborov.getBlokovaciFaktorPreplnujuciSubor(), pridavany.getClass());

                // Pridaj a uloz
                boolean uspesneVlozene = this.skusVlozitDoPreplnujucehoBlocku(pridavany, preplnujuciBlock, spravcaSuborov);
                if (uspesneVlozene)
                {
                    spravcaSuborov.ulozPreplnujuciSubor(curBlock.offsetPreplnujuciSubor, preplnujuciBlock.prevedNaPoleBajtov());

                    if (!curBlock.equals(this))
                    {
                        // Block, s ktorym aktualne pracujem je Preplnujuci block,
                        // preto ho musim ulozit, ukladanie Blockov v Hlavnom subore
                        // zabezpeci volajuca metoda
                        spravcaSuborov.ulozPreplnujuciSubor(odlozenyOffset, curBlock.prevedNaPoleBajtov());
                    }

                    // Pre vlozenie Zaznamu bolo nutne alokovat novy Preplnujuci block
                    return true;
                }

                throw new RuntimeException("Novo vytvoreny preplnujuci subor je plny!");
            }
        }
    }

    private boolean skusVlozitDoPreplnujucehoBlocku(T pridavany, Block<T> preplnujuciBlock, SpravcaSuborov spravcaSuborov)
    {
        if (preplnujuciBlock.pocetPlatnychZaznamov >= spravcaSuborov.getBlokovaciFaktorPreplnujuciSubor())
        {
            return false;
        }

        // V Preplnujucom blocku je miesto pre novy Zaznam
        preplnujuciBlock.zaznamy.add(pridavany);
        preplnujuciBlock.pocetPlatnychZaznamov++;
        return true;
    }

    public T vyhladaj(T vyhladavany, SpravcaSuborov spravcaSuborov)
    {
        T najdeny = this.prehladajBlock(vyhladavany, this);
        if (najdeny != null)
        {
            return najdeny;
        }

        Block<T> preplnujuciBlock = (Block<T>)new Block<>(spravcaSuborov.getBlokovaciFaktorPreplnujuciSubor(), vyhladavany.getClass());
        int velkostPreplnujuciBlock = preplnujuciBlock.getVelkost();

        Block<T> curBlock = this;
        while (curBlock.offsetPreplnujuciSubor != -1)
        {
            byte[] nacitanyBlock = spravcaSuborov.citajPreplnujuciSubor(curBlock.offsetPreplnujuciSubor, velkostPreplnujuciBlock);
            curBlock.prevedZPolaBajtov(nacitanyBlock);

            najdeny = this.prehladajBlock(vyhladavany, curBlock);
            if (najdeny != null)
            {
                return najdeny;
            }
        }

        // Zaznam nebol najdeny
        return null;
    }

    private T prehladajBlock(T vyhladavany, Block<T> prehladavanyBlock)
    {
        for (int i = 0; i < prehladavanyBlock.pocetPlatnychZaznamov; i++)
        {
            if (prehladavanyBlock.zaznamy.get(i).jeRovnaky(vyhladavany))
            {
                return prehladavanyBlock.zaznamy.get(i);
            }
        }

        return null;
    }

    public T vymaz(T vymazavany, SpravcaSuborov spravcaSuborov, boolean[] dealokovanyPreplnujuciBlock)
    {
        long offsetPrevPreplnujuci = -1;
        boolean prevBlockPreplnujuci = false;

        Block<T> prevBlock = null;
        Block<T> curBlock = this;

        while (true)
        {
            int indexVymazavaneho = -1;
            for (int i = 0; i < curBlock.pocetPlatnychZaznamov; i++)
            {
                if (curBlock.zaznamy.get(i).jeRovnaky(vymazavany))
                {
                    // Zaznam bol najdeny v curBlock-u
                    indexVymazavaneho = i;
                    break;
                }
            }

            if (indexVymazavaneho != -1)
            {
                Collections.swap(curBlock.zaznamy, indexVymazavaneho, curBlock.pocetPlatnychZaznamov - 1);
                curBlock.pocetPlatnychZaznamov--;
                T realneZmazany = curBlock.zaznamy.remove(curBlock.pocetPlatnychZaznamov);

                if (prevBlock != null)
                {
                    // Ak prevBlock nie je null, tak to znamena,
                    // ze Zaznam bol vymazany z Preplnujuceho blocku
                    if (curBlock.pocetPlatnychZaznamov != 0)
                    {
                        // Block nie je prazdny, iba ho ulozim
                        spravcaSuborov.ulozPreplnujuciSubor(prevBlock.offsetPreplnujuciSubor, curBlock.prevedNaPoleBajtov());
                    }
                    else
                    {
                        // Preplnujuci block bol vyprazdneny, nie je potrebne,
                        // aby mal nadalej alokovane miesto v subore
                        dealokovanyPreplnujuciBlock[0] = true;
                        long offsetDealokovanyBlock = prevBlock.offsetPreplnujuciSubor;

                        if (curBlock.offsetPreplnujuciSubor != -1)
                        {
                            // Dochadza k dealokovaniu blocku uprostred zretazenia,
                            // pred dealokovciou modifikujem zretazenie
                            prevBlock.offsetPreplnujuciSubor = curBlock.offsetPreplnujuciSubor;
                        }
                        else
                        {
                            prevBlock.offsetPreplnujuciSubor = -1;
                        }

                        spravcaSuborov.uvolniBlockPreplnujuciSubor(offsetDealokovanyBlock, vymazavany.getClass());

                        if (prevBlockPreplnujuci)
                        {
                            // Ak je aj predchadzajuci block Preplnujicim blockom,
                            // je tento nutne ulozit aj ten
                            spravcaSuborov.ulozPreplnujuciSubor(offsetPrevPreplnujuci, prevBlock.prevedNaPoleBajtov());
                        }
                    }
                }

                return realneZmazany;
            }

            if (curBlock.offsetPreplnujuciSubor != -1)
            {
                if (prevBlock != null)
                {
                    prevBlockPreplnujuci = true;
                    offsetPrevPreplnujuci = prevBlock.getOffsetPreplnujuciSubor();
                }

                prevBlock = curBlock;

                curBlock = (Block<T>)new Block<>(spravcaSuborov.getBlokovaciFaktorPreplnujuciSubor(), vymazavany.getClass());
                curBlock.prevedZPolaBajtov(spravcaSuborov.citajPreplnujuciSubor(prevBlock.offsetPreplnujuciSubor, curBlock.getVelkost()));
            }
            else
            {
                // Zaznam nebol najdeny
                return null;
            }
        }
    }

    public boolean jeBlockPlny()
    {
        return this.pocetPlatnychZaznamov >= this.maxPocetZaznamov;
    }

    public long getOffsetPreplnujuciSubor()
    {
        return this.offsetPreplnujuciSubor;
    }

    public long getOffsetPrevVolny()
    {
        return this.offsetPrevVolny;
    }

    public long getOffsetNextVolny()
    {
        return this.offsetNextVolny;
    }

    public int getPocetPlatnychZaznamov()
    {
        return this.pocetPlatnychZaznamov;
    }

    public ArrayList<T> getZaznamy()
    {
        return this.zaznamy;
    }

    private T getDummyInstancia()
    {
        try
        {
            return (T)this.dummyZaznam.getClass().getDeclaredConstructor().newInstance();
        }
        catch (Exception ex)
        {
            throw new RuntimeException("Chyba pri vytvarani instancie dummy Zaznamu v Blocku!");
        }
    }

    public void setOffsetPreplnujuciSubor(long offsetPreplnujuciSubor)
    {
        this.offsetPreplnujuciSubor = offsetPreplnujuciSubor;
    }

    public void setOffsetPrevVolny(long offsetPrevVolny)
    {
        this.offsetPrevVolny = offsetPrevVolny;
    }

    public void setOffsetNextVolny(long offsetNextVolny)
    {
        this.offsetNextVolny = offsetNextVolny;
    }

    // Kolko bajtov zabera 1 Block
    @Override
    public int getVelkost()
    {
        int velkost = 0;

        // Offset predchadzajuceho a nasledujuceho volneho Blocku
        velkost += 2 * Konstanty.VELKOST_LONG;

        // Offest do Preplnujuceho subotu
        velkost += Konstanty.VELKOST_LONG;

        // Pocet platnych Zaznamov - int
        velkost += Konstanty.VELKOST_INT;

        // Velkost samotnych Zaznamov, uvazujem o maximalnej moznej velkosti,
        // tym padom do uvahy beriem blokovaci faktor, nie pocet platnych zaznamov
        velkost += this.dummyZaznam.getVelkost() * this.maxPocetZaznamov;

        return velkost;
    }

    @Override
    public byte[] prevedNaPoleBajtov()
    {
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteOutputStream);

        try
        {
            dataOutputStream.writeLong(this.offsetPrevVolny);
            dataOutputStream.writeLong(this.offsetNextVolny);
            dataOutputStream.writeLong(this.offsetPreplnujuciSubor);

            dataOutputStream.writeInt(this.pocetPlatnychZaznamov);

            for (int i = 0; i < this.pocetPlatnychZaznamov; i++)
            {
                dataOutputStream.write(this.zaznamy.get(i).prevedNaPoleBajtov());
            }

            // Zvysne miesto v Blocku dopln dummy znakmi
            int pocetNeplatnychZaznamov = this.maxPocetZaznamov - this.pocetPlatnychZaznamov;
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
            this.offsetPrevVolny = dataInputStream.readLong();
            this.offsetNextVolny = dataInputStream.readLong();
            this.offsetPreplnujuciSubor = dataInputStream.readLong();

            this.pocetPlatnychZaznamov = dataInputStream.readInt();

            this.zaznamy = new ArrayList<>();
            int velkostZaznamu = this.dummyZaznam.getVelkost();

            for (int i = 0; i < this.pocetPlatnychZaznamov; i++)
            {
                byte[] poleBajtovZaznam = dataInputStream.readNBytes(velkostZaznamu);

                // Nova instancia typu T a jej inicializacia pomocou nacitaneho pola bajtov
                T novyZaznam = this.getDummyInstancia();
                novyZaznam.prevedZPolaBajtov(poleBajtovZaznam);
                this.zaznamy.add(novyZaznam);
            }
        }
        catch (Exception ex)
        {
            throw new IllegalStateException("Konverzia pola bajtov na Block sa nepodarila!");
        }
    }

    @Override
    public boolean equals(Object object)
    {
        if (!(object instanceof Block block))
        {
            return false;
        }

        if (this.maxPocetZaznamov == block.maxPocetZaznamov &&
            this.pocetPlatnychZaznamov == block.pocetPlatnychZaznamov &&
            this.zaznamy.size() == block.zaznamy.size())
        {
            for (int i = 0; i < this.pocetPlatnychZaznamov; i++)
            {
                if (!this.zaznamy.get(i).equals(block.zaznamy.get(i)))
                {
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    @Override
    public String toString()
    {
        String string = "";
        string += "- max pocet zaznamov: " + this.maxPocetZaznamov + "\n";
        string += "- pocet platnych zaznamov: " + this.pocetPlatnychZaznamov + "\n";
        string += "- offset preplnujuci subor: " + this.offsetPreplnujuciSubor + "\n";
        string += "- offset prev volny: " + this.offsetPrevVolny + "\n";
        string += "- offset next bolny: " + this.offsetNextVolny + "\n";

        string += "- zaznamy:\n";
        for (int i = 0; i < this.pocetPlatnychZaznamov; i++)
        {
            string += this.zaznamy.get(i).toString();
        }

        return string;
    }
}
