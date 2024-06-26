package Objekty;

import QuadStrom.Objekty.DummyNehnutelnost;
import Rozhrania.IData;
import Rozhrania.IRecord;
import Ostatne.Helper;
import Ostatne.Konstanty;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.BitSet;

public class Nehnutelnost extends Polygon implements IData
{
    private static final int MAX_POCET_REFERENCII = 6;
    private static final int POCET_BITOV_HASH = 32;

    // Unikatny kluc
    private int nehnutelnostID;

    // Ostatne atributy
    private int supisneCislo;
    private String popis;

    // Zoznam ID parciel, na ktorych lezi dana nehnutelnost
    private ArrayList<Integer> parcelyID;

    public Nehnutelnost(int nehnutelnostID, int supisneCislo, String popis, Suradnica suradnica1, Suradnica suradnica2)
    {
        this.nastavSuradnice(suradnica1, suradnica2);

        this.nehnutelnostID = nehnutelnostID;
        this.supisneCislo = supisneCislo;
        this.setPopis(popis);
        this.parcelyID = new ArrayList<>();
    }

    public Nehnutelnost(DummyNehnutelnost dummyNehnutelnost)
    {
        this.nastavSuradnice(dummyNehnutelnost.surVlavoDole, dummyNehnutelnost.surVpravoHore);
        this.nehnutelnostID = dummyNehnutelnost.getNehnutelnostID();
    }

    // Pre ucely vyhladavania v Aplikacii
    public Nehnutelnost(int nehnutelnostID)
    {
        this.nehnutelnostID = nehnutelnostID;
    }

    // Pre ucely inicializacie z pola bajtov
    public Nehnutelnost() {}

    // Metoda sa pokusi pridat ID parcely do zoznamu ID parciel, na ktorych lezi nehnutelnost,
    // ak pridanie zlyha (nehnutelnost nelezi na danej parcele alebo je zoznam plny), vyhodi sa vynimka
    public void skusPridatParcelu(Parcela parcela)
    {
        if (!this.prekryva(parcela))
        {
            throw new RuntimeException("Nehnutelnost nelezi na danej parcele!");
        }

        if (this.parcelyID.size() < MAX_POCET_REFERENCII)
        {
            // Pridaj iba v pripade ak nie je presiahnuty limit
            this.parcelyID.add(parcela.getParcelaID());
        }
        else
        {
            throw new RuntimeException("Zoznam referencii je plny!");
        }
    }

    // Metoda sa pokusi odobrat ID parcely zo zoznamu ID parciel, na ktorych lezi nehnutelnost,
    // ak odobratie zlyha (nehnutelnost nelezi na danej parcele), vyhodi sa vynimka
    public void skusOdobratParcelu(Parcela parcela)
    {
        if (!this.prekryva(parcela))
        {
            throw new RuntimeException("Nehnutelnost nelezi na danej parcele!");
        }

        this.parcelyID.remove(Integer.valueOf(parcela.getParcelaID()));
    }

    public int getNehnutelnostID()
    {
        return this.nehnutelnostID;
    }

    public int getSupisneCislo()
    {
        return this.supisneCislo;
    }

    public String getPopis()
    {
        return popis;
    }

    public ArrayList<Integer> getParcelyID()
    {
        return this.parcelyID;
    }

    public void setSupisneCislo(int supisneCislo)
    {
        this.supisneCislo = supisneCislo;
    }

    public void setPopis(String popis)
    {
        this.popis = popis.substring(0, Math.min(popis.length(), Konstanty.MAX_DLZKA_POPIS_NEHNUTELNOST));
    }

    public void setSuradnice(Suradnica suradnica1, Suradnica suradnica2)
    {
        this.nastavSuradnice(suradnica1, suradnica2);
    }

    @Override
    public String toString()
    {
        DecimalFormat formatovac = new DecimalFormat("#.##");
        String string = "";
        string += "Nehnuteľnosť:\n";
        string += "- identifikačné číslo: " + this.nehnutelnostID + "\n";
        string += "- súpisné číslo: " + this.supisneCislo + "\n";
        string += "- popis: " + this.popis + "\n";
        string += "- súradnice: {" + formatovac.format(this.getVlavoDoleX()) + ", " + formatovac.format(this.getVlavoDoleY()) + "},"
                  + " {" + formatovac.format(this.getVpravoHoreX()) + ", " + formatovac.format(this.getVpravoHoreY()) + "}\n";

        string += "- identifikačné čísla parciel, s ktorými sa prekrýva: ";
        for (Integer parcelaID : this.parcelyID)
        {
            string += "[" + parcelaID + "] ";
        }
        string += "\n";

        string += "- hash: " + Helper.hashToString(this.getHash(), POCET_BITOV_HASH) + "\n";

        return string;
    }

    @Override
    public boolean jeRovnaky(IRecord zaznam)
    {
        if (!(zaznam instanceof Nehnutelnost nehnutelnost))
        {
            return false;
        }

        return this.nehnutelnostID == nehnutelnost.getNehnutelnostID();
    }

    @Override
    public BitSet getHash()
    {
        BitSet bitSet = new BitSet();
        long hash = (this.nehnutelnostID * 2654435761L) % (1L << POCET_BITOV_HASH);

        for (int i = 0; i < POCET_BITOV_HASH; i++)
        {
            byte bit = (byte)((hash >> i) & 1);

            if (bit == 1)
            {
                bitSet.set(i);
            }
        }

        return bitSet;
    }

    @Override
    public int getPocetBitovHash()
    {
        return POCET_BITOV_HASH;
    }

    @Override
    public int getVelkost()
    {
        int velkost = 0;

        // ID nehnutelnosti - int
        velkost += Konstanty.VELKOST_INT;

        // Supisne cislo - int
        velkost += Konstanty.VELKOST_INT;

        // Bajt pre dlzku popisu
        velkost += Konstanty.VELKOST_BAJT;

        // Samotny String - 1 bajt pre kazdy znak v Stringu
        velkost += Konstanty.VELKOST_BAJT * Konstanty.MAX_DLZKA_POPIS_NEHNUTELNOST;

        // 2 suradnice, pricom kazda ma 2 double hodnoty
        velkost += 4 * Konstanty.VELKOST_DOUBLE;

        // Bajt pre dlzku zoznamu referencii na nehnutelnosti
        velkost += Konstanty.VELKOST_BAJT;

        // Samotne ID parciel - int
        velkost += MAX_POCET_REFERENCII * Konstanty.VELKOST_INT;

        return velkost;
    }

    @Override
    public byte[] prevedNaPoleBajtov()
    {
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteOutputStream);

        try
        {
            dataOutputStream.writeInt(this.nehnutelnostID);
            dataOutputStream.writeInt(this.supisneCislo);

            dataOutputStream.writeByte(this.popis.length());
            String rozsirenyPopis = Helper.rozsirString(this.popis, Konstanty.MAX_DLZKA_POPIS_NEHNUTELNOST);
            dataOutputStream.writeBytes(rozsirenyPopis);

            dataOutputStream.write(this.surVlavoDole.prevedNaPoleBajtov());
            dataOutputStream.write(this.surVpravoHore.prevedNaPoleBajtov());

            dataOutputStream.writeByte(this.parcelyID.size());
            for (Integer parcelaID : this.parcelyID)
            {
                dataOutputStream.writeInt(parcelaID);
            }

            for (int i = 0; i < MAX_POCET_REFERENCII - this.parcelyID.size(); i++)
            {
                dataOutputStream.writeInt(0);
            }

            return byteOutputStream.toByteArray();
        }
        catch (Exception ex)
        {
            throw new IllegalStateException("Konverzia Nehnutelnosti na pole bajtov sa nepodarila!");
        }
    }

    @Override
    public void prevedZPolaBajtov(byte[] poleBajtov)
    {
        if (this.getVelkost() != poleBajtov.length)
        {
            throw new RuntimeException("Dlzka pola bajtov sa nezhoduje s velkostou Nehnutelnosti!");
        }

        ByteArrayInputStream byteInputStream = new ByteArrayInputStream(poleBajtov);
        DataInputStream dataInputStream = new DataInputStream(byteInputStream);

        try
        {
            this.nehnutelnostID = dataInputStream.readInt();
            this.supisneCislo = dataInputStream.readInt();

            byte dlzkaPopisu = dataInputStream.readByte();
            this.popis = Helper.nacitajString(dataInputStream, dlzkaPopisu, Konstanty.MAX_DLZKA_POPIS_NEHNUTELNOST);

            Suradnica suradnica1 = new Suradnica(dataInputStream.readDouble(), dataInputStream.readDouble());
            Suradnica suradnica2 = new Suradnica(dataInputStream.readDouble(), dataInputStream.readDouble());
            this.nastavSuradnice(suradnica1, suradnica2);

            this.parcelyID = new ArrayList<>();
            byte pocetReferencii = dataInputStream.readByte();

            for (int i = 0; i < pocetReferencii; i++)
            {
                this.parcelyID.add(dataInputStream.readInt());
            }
        }
        catch (Exception ex)
        {
            throw new IllegalStateException("Konverzia pola bajtov na Parcelu sa nepodarila!");
        }
    }

    @Override
    public boolean equals(Object object)
    {
        if (!(object instanceof Nehnutelnost nehnutelnost))
        {
            return false;
        }

        final double epsilon = 0.00001;
        if (Math.abs(this.getVlavoDoleX() - nehnutelnost.getVlavoDoleX()) < epsilon &&
            Math.abs(this.getVlavoDoleY() - nehnutelnost.getVlavoDoleY()) < epsilon &&
            Math.abs(this.getVpravoHoreX() - nehnutelnost.getVpravoHoreX()) < epsilon &&
            Math.abs(this.getVpravoHoreY() - nehnutelnost.getVpravoHoreY()) < epsilon &&
            this.nehnutelnostID == nehnutelnost.getNehnutelnostID() &&
            this.popis.equals(nehnutelnost.getPopis()))
        {
            if (this.parcelyID.size() != nehnutelnost.getParcelyID().size())
            {
                return false;
            }

            for (int i = 0; i < this.parcelyID.size(); i++)
            {
                if (!this.parcelyID.get(i).equals(nehnutelnost.getParcelyID().get(i)))
                {
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    public static int getMaxPocetReferencii()
    {
        return MAX_POCET_REFERENCII;
    }
}
