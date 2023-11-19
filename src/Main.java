import Hesovanie.Block;
import Hesovanie.DynamickeHesovanie;
import Hesovanie.DynamickyZnakovyStrom.ExternyVrchol;
import Hesovanie.Subor;
import Objekty.Nehnutelnost;
import Objekty.Parcela;
import Objekty.Suradnica;
import Ostatne.Generator;
import com.sun.source.tree.TryTree;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;

public class Main
{
    public static void main(String[] args) throws IOException
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

        Collections.shuffle(indexy);
        for (int i = 0; i < pocetElementov; i++)
        {
            int index = indexy.get(i);
            Parcela hladana = al.get(index);
            Parcela najdenaDH = h.vyhladaj(hladana, Parcela.class);

            System.out.println("AL: " + hladana.toString() + ", DH: " + najdenaDH.toString());
        }
    }
}
