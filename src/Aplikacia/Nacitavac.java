package Aplikacia;

import Objekty.Nehnutelnost;
import Objekty.Parcela;
import Objekty.Suradnica;
import QuadStrom.Objekty.DummyNehnutelnost;
import QuadStrom.Objekty.DummyParcela;
import QuadStrom.QuadStrom;
import Rozhrania.IData;
import Rozhrania.IPolygon;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class Nacitavac
{
    public static boolean nacitajQuadStromy(QuadStrom<DummyParcela> qsParcely, QuadStrom<DummyNehnutelnost> qsNehnutelnosti,
                                            String nazovSuboruParcely, String nazovSuboruNehnutelnosti)
    {
        if (!nacitajQuadStromZoSuboru(nazovSuboruParcely, qsParcely, DummyParcela.class) ||
            !nacitajQuadStromZoSuboru(nazovSuboruNehnutelnosti, qsNehnutelnosti, DummyNehnutelnost.class))
        {
            return false;
        }

        return true;
    }

    private static <T extends IPolygon> boolean nacitajQuadStromZoSuboru(String nazovSuboru, QuadStrom<T> strom, Class<T> typ)
    {
        File subor = new File(nazovSuboru);

        if (!subor.exists() || !subor.isFile())
        {
            // Problem so suborom
            return false;
        }

        // Informacie o velkosti a hlbke
        if (!nacitajParametreZoSuboru(strom, subor, typ))
        {
            // Pri nacitavani nastal problem
            return false;
        }

        // Ak som sa dostal sem, tak strom je uspesne vytvoreny,
        // teraz ho naplnim datami zo suborov
        if (!nacitajDataZoSuboru(strom, subor, typ))
        {
            // Pri nacitavani samotnych dat nastal problem
            return false;
        }

        return true;
    }

    private static <T extends IPolygon> boolean nacitajParametreZoSuboru(QuadStrom<T> strom, File subor, Class<T> typ)
    {
        try
        {
            FileReader fCitac = new FileReader(subor);
            BufferedReader bCitac = new BufferedReader(fCitac);

            double vlavoDoleX = Double.parseDouble(bCitac.readLine());
            double vlavoDoleY = Double.parseDouble(bCitac.readLine());
            double vpravoHoreX = Double.parseDouble(bCitac.readLine());
            double vpravoHoreY = Double.parseDouble(bCitac.readLine());

            int maxUroven = Integer.parseInt(bCitac.readLine());

            // Vytvorim strom
            strom.setSuradnice(new Suradnica(vlavoDoleX, vlavoDoleY), new Suradnica(vpravoHoreX, vpravoHoreY));
            strom.setMaxUroven(maxUroven);

            bCitac.close();
            fCitac.close();

            // Vytvorenie stromu bolo uspesne
            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
    }

    private static <T extends IPolygon> boolean nacitajDataZoSuboru(QuadStrom<T> strom, File subor, Class<T> typ)
    {
        try
        {
            FileReader fCitac = new FileReader(subor);
            BufferedReader bCitac = new BufferedReader(fCitac);

            for (int i = 0; i < 5; i++)
            {
                String dummyRiadok = bCitac.readLine();
            }

            String riadok;
            while (true)
            {
                riadok = bCitac.readLine();
                if (riadok == null)
                {
                    // Ukonci citanie, ked dosiahnes koniec suboru
                    break;
                }

                String[] casti = riadok.split(";", 6);

                int cislo = Integer.parseInt(casti[0]);

                double vlavoDoleX =  Double.parseDouble(casti[1]);
                double vlavoDoleY =  Double.parseDouble(casti[2]);
                double vpravoHoreX = Double.parseDouble(casti[3]);
                double vpravoHoreY = Double.parseDouble(casti[4]);

                Suradnica surVlavoDole = new Suradnica(vlavoDoleX, vlavoDoleY);
                Suradnica surVpravoHore = new Suradnica(vpravoHoreX, vpravoHoreY);

                if (typ.equals(DummyNehnutelnost.class))
                {
                    DummyNehnutelnost dummyNehnutelnost = new DummyNehnutelnost(cislo, surVlavoDole, surVpravoHore);
                    strom.vloz((T)dummyNehnutelnost);
                }
                else if (typ.equals(DummyParcela.class))
                {
                    DummyParcela dummyParcela = new DummyParcela(cislo, surVlavoDole, surVpravoHore);
                    strom.vloz((T)dummyParcela);
                }
            }

            bCitac.close();
            fCitac.close();

            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
    }
}
