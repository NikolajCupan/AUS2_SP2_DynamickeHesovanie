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

    public static String hashToString(BitSet bitSet, int pocetBitov)
    {
        String string = "";
        for (int i = 1; i <= pocetBitov; i++)
        {
            int hodnotaBitu = bitSet.get(i) ? 1 : 0;
            string += hodnotaBitu;

            if (i == 1 || i == pocetBitov)
            {
                continue;
            }

            if (i % 8 == 0)
            {
                string += "  ";
            }
            else if (i % 4 == 0)
            {
                string += " ";
            }
        }

        return new StringBuilder(string).reverse().toString();
    }
}
