package Testovanie;

import Ostatne.Helper;
import Ostatne.Konstanty;
import Rozhrania.IData;
import Rozhrania.IRecord;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.BitSet;

public class Dummy implements IData
{
    private static final int MAX_DLZKA_ID = 10;

    public String ID;
    public int pocetBitovHash;

    public Dummy(int ID, int pocetBitovHash)
    {
        this.ID = String.valueOf(ID);
        this.pocetBitovHash = pocetBitovHash;
    }

    // Pre ucely inicializacie z pola bajtov
    public Dummy() {}

    @Override
    public boolean jeRovnaky(IRecord zaznam)
    {
        if (!(zaznam instanceof Dummy dummy))
        {
            return false;
        }

        return this.ID.equals(dummy.ID);
    }

    @Override
    public BitSet getHash()
    {
        BitSet bitSet = new BitSet();
        long hash = (Integer.parseInt(this.ID) * 2654435761L) % (1L << this.pocetBitovHash);

        for (int i = 0; i < this.pocetBitovHash; i++)
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
        return this.pocetBitovHash;
    }

    @Override
    public int getVelkost()
    {
        int velkost = 0;

        // Velkost ID
        velkost += Konstanty.VELKOST_BAJT;
        // ID - String max 8 bitov
        velkost += MAX_DLZKA_ID * Konstanty.VELKOST_BAJT;

        // Pocet bitov hash - int
        velkost += Konstanty.VELKOST_INT;

        return velkost;
    }

    @Override
    public byte[] prevedNaPoleBajtov()
    {
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteOutputStream);

        try
        {
            dataOutputStream.writeByte(this.ID.length());
            String rozsireneID = Helper.rozsirString(this.ID, MAX_DLZKA_ID);
            dataOutputStream.writeBytes(rozsireneID);

            dataOutputStream.writeInt(this.pocetBitovHash);

            return byteOutputStream.toByteArray();
        }
        catch (Exception ex)
        {
            throw new IllegalStateException("Konverzia Dummy na pole bajtov sa nepodarila!");
        }
    }

    @Override
    public void prevedZPolaBajtov(byte[] poleBajtov)
    {
        if (this.getVelkost() != poleBajtov.length)
        {
            throw new RuntimeException("Dlzka pola bajtov sa nezhoduje s velkostou Dummy!");
        }

        ByteArrayInputStream byteInputStream = new ByteArrayInputStream(poleBajtov);
        DataInputStream dataInputStream = new DataInputStream(byteInputStream);

        try
        {
            byte dlzkaID = dataInputStream.readByte();
            this.ID = Helper.nacitajString(dataInputStream, dlzkaID, MAX_DLZKA_ID);
            this.pocetBitovHash = dataInputStream.readInt();
        }
        catch (Exception ex)
        {
            throw new IllegalStateException("Konverzia pola bajtov na Dummy sa nepodarila!");
        }
    }

    @Override
    public String toString()
    {
        String string = "";
        string += "\tDummy:\n";
        string += "\t- ID: " + this.ID + "\n";
        string += "\t- pocet bitov hash: " + this.pocetBitovHash + "\n";
        string += "\t- hash: " + Helper.hashToString(this.getHash(), this.pocetBitovHash) + "\n";

        return string;
    }
}
