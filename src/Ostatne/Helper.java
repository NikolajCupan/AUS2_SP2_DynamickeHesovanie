package Ostatne;

import java.io.DataInputStream;

public class Helper
{
    public static String rozsirString(String string, int novaDlzka)
    {
        final char dummyZnak = '*';

        StringBuilder builder = new StringBuilder(string);
        while (builder.length() < novaDlzka)
        {
            builder.append(dummyZnak);
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
}
