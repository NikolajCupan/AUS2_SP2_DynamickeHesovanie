import Hesovanie.DynamickeHesovanie;
import Objekty.Parcela;
import Objekty.Suradnica;
import Ostatne.Generator;

import java.io.File;

public class Main
{
    private static final String NAZOV_HS = "hlavny";
    private static final String NAZOV_PS = "preplnujuci";

    public static void main(String[] args)
    {
        vymazSubory();
        Suradnica suradnica = new Suradnica();

        Generator generator = new Generator(1, 1, 1, 0, 0, 100, 100, 1);
        DynamickeHesovanie<Parcela> dh = new DynamickeHesovanie<>(2, 2, "hlavny", "preplnujuci");

        Parcela p1 = new Parcela(1, "1", suradnica, suradnica);
        Parcela p2 = new Parcela(2, "2", suradnica, suradnica);
        Parcela p3 = new Parcela(3, "3", suradnica, suradnica);
        Parcela p4 = new Parcela(4, "4", suradnica, suradnica);

        dh.vloz(p1, Parcela.class);
        dh.vloz(p2, Parcela.class);
        dh.vloz(p3, Parcela.class);
        dh.vloz(p4, Parcela.class);

        dh.vymaz(p3, Parcela.class);
        dh.vymaz(p4, Parcela.class);
        dh.vymaz(p1, Parcela.class);
        dh.vymaz(p2, Parcela.class);

        int x = 100;
    }

    public static void vymazSubory()
    {
        File hlavnySubor = new File(NAZOV_HS);
        File preplnujuciSubor = new File(NAZOV_PS);

        if (hlavnySubor.exists())
        {
            hlavnySubor.delete();
        }

        if (preplnujuciSubor.exists())
        {
            preplnujuciSubor.delete();
        }
    }
}
