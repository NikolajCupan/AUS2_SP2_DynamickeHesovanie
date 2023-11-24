import Hesovanie.DynamickeHesovanie;
import Objekty.Parcela;
import Ostatne.Generator;

import java.util.ArrayList;
import java.util.Collections;

public class Main
{
    public static void main(String[] args)
    {
        final int pocetElementov = 100000;

        Generator generator = new Generator(1, 1, 1, 0, 0, 100, 100, 1);
        DynamickeHesovanie<Parcela> h = new DynamickeHesovanie<>(2, 2, "hlavny", "preplnujuci");
        ArrayList<Parcela> al = new ArrayList<>();
        ArrayList<Integer> indexy = new ArrayList<>();

        for (int i = 0; i < pocetElementov; i++)
        {
            indexy.add(i);

            Parcela parcela = generator.getParcela();
            h.vloz(parcela, Parcela.class);
            al.add(parcela);
        }

        int pocet = h.getPocetElemtov();
        int spracovane = 0;
        int ok = 0;
        int bad = 0;

        Collections.shuffle(indexy);
        for (int i = 0; i < pocetElementov; i++)
        {
            int index = indexy.get(i);
            Parcela hladana = al.get(index);
            Parcela najdenaDH = h.vyhladaj(hladana, Parcela.class);

            if (hladana.jeRovnaky(najdenaDH))
            {
                ok++;
            }
            else
            {
                bad++;
            }

            spracovane++;
        }

        System.out.println("Ok: " + ok);
        System.out.println("Bad: " + bad);
        System.out.println("Spracovane: " + spracovane);
        System.out.println("Pocet: " + pocet);
    }
}
