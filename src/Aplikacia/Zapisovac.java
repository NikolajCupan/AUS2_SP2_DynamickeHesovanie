package Aplikacia;

import Hesovanie.SpravcaSuborov;
import QuadStrom.Objekty.DummyNehnutelnost;
import QuadStrom.Objekty.DummyParcela;
import QuadStrom.Quad;
import QuadStrom.QuadStrom;
import Rozhrania.IPolygon;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public class Zapisovac
{
    public static boolean ulozQuadStromy(QuadStrom<DummyParcela> qsParcely, QuadStrom<DummyNehnutelnost> qsNehnutelnosti,
                                         String nazovSuboruParcely, String nazovSuboruNehnutelnosti)
    {
        if (!zapisQuadStromDoSuboru(nazovSuboruParcely, qsParcely, DummyParcela.class) ||
            !zapisQuadStromDoSuboru(nazovSuboruNehnutelnosti, qsNehnutelnosti, DummyNehnutelnost.class))
        {
            return false;
        }

        return true;
    }

    private static <T extends IPolygon> boolean zapisQuadStromDoSuboru(String nazovSuboru, QuadStrom<T> strom, Class<T> typ)
    {
        try
        {
            PrintWriter zapisovac = new PrintWriter(nazovSuboru, StandardCharsets.UTF_8);

            zapisovac.println("" + strom.getRootQuad().getVlavoDoleX());
            zapisovac.println("" + strom.getRootQuad().getVlavoDoleY());
            zapisovac.println("" + strom.getRootQuad().getVpravoHoreX());
            zapisovac.println("" + strom.getRootQuad().getVpravoHoreY());
            zapisovac.println("" + strom.getMaxUroven());

            for (Quad<T> quad : strom)
            {
                for (T element : quad.getData())
                {
                    if (typ.equals(DummyNehnutelnost.class))
                    {
                        DummyNehnutelnost dummyNehnutelnost = (DummyNehnutelnost)element;
                        zapisovac.println(dummyNehnutelnost.getNehnutelnostID() + ";" +
                                          dummyNehnutelnost.getVlavoDoleX() + ";" + dummyNehnutelnost.getVlavoDoleY() + ";" +
                                          dummyNehnutelnost.getVpravoHoreX() + ";" + dummyNehnutelnost.getVpravoHoreY());
                    }
                    else if (typ.equals(DummyParcela.class))
                    {
                        DummyParcela dummyParcela = (DummyParcela)element;
                        zapisovac.println(dummyParcela.getParcelaID() + ";" +
                                          dummyParcela.getVlavoDoleX() + ";" + dummyParcela.getVlavoDoleY() + ";" +
                                          dummyParcela.getVpravoHoreX() + ";" + dummyParcela.getVpravoHoreY());
                    }
                }
            }

            zapisovac.close();

            // Vsetky data boli uspesne zapisane do suboru
            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
    }

    public static boolean ulozSpravcovSuborov(SpravcaSuborov ssParcely, SpravcaSuborov ssNehnutelnosti,
                                              String nazovSuboruParcely, String nazovSuboruNehnutelnosti)
    {
        if (!zapisSpravcuSuborovDoSuboru(nazovSuboruParcely, ssParcely) ||
            !zapisSpravcuSuborovDoSuboru(nazovSuboruNehnutelnosti, ssNehnutelnosti))
        {
            return false;
        }

        return true;
    }

    private static boolean zapisSpravcuSuborovDoSuboru(String nazovSuboru, SpravcaSuborov spravcaSuborov)
    {
        try
        {
            PrintWriter zapisovac = new PrintWriter(nazovSuboru, StandardCharsets.UTF_8);

            zapisovac.println(spravcaSuborov.getBlokovaciFaktorHlavnySubor());
            zapisovac.println(spravcaSuborov.getBlokovaciFaktorPreplnujuciSubor());

            zapisovac.println(spravcaSuborov.getOffsetPrvyVolnyHlavnySubor());
            zapisovac.println(spravcaSuborov.getOffsetPrvyVolnyPreplnujuciSubor());

            zapisovac.close();

            // Vsetky data boli uspesne zapisane do suboru
            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
    }
}
