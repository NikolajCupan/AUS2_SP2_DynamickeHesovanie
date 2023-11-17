package Ostatne;

import java.io.DataInputStream;
import java.util.BitSet;

public class Helper
{
    public static String rozsirString(String string, int novaDlzka)
    {
        StringBuilder builder = new StringBuilder(string);
        while (builder.length() < novaDlzka)
        {
            builder.append(Konstanty.DUMMY_ZNAK);
        }

        return builder.toString();
    }

    public static String nacitajString(DataInputStream dataInputStream, byte dlzka, int pocetBajtovSubor)
    {
        StringBuilder builder = new StringBuilder();

        try
        {
            for (int i = 1; i <= dlzka; i++)
            {
                byte znak = dataInputStream.readByte();
                builder.append((char)znak);
            }

            int zvysneBajty = pocetBajtovSubor - dlzka;
            dataInputStream.skipNBytes(zvysneBajty);
        }
        catch (Exception ex)
        {
            throw new RuntimeException("Chyba pri nacitani Stringu zo suboru!");
        }

        return builder.toString();
    }

    public static BitSet generateHash(int cislo)
    {
        BitSet bitSet = new BitSet();
        long hash = (cislo * 2654435761L) % (1L << Konstanty.POCET_BITOV_HASH);

        for (int i = 0; i < Konstanty.POCET_BITOV_HASH; i++)
        {
            byte bit = (byte)((hash >> i) & 1);

            if (bit == 1)
            {
                bitSet.set(i);
            }
        }

        return bitSet;
    }
}
