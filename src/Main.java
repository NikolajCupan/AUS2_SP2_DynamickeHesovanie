import Hesovanie.DynamickeHesovanie;
import Objekty.Parcela;
import Objekty.Suradnica;
import Testovanie.Tester;

import java.io.File;

public class Main
{
    private static final String NAZOV_HS = "hlavny";
    private static final String NAZOV_PS = "preplnujuci";

    public static void main(String[] args)
    {
        vymazSubory();

        Suradnica suradnica = new Suradnica();
        DynamickeHesovanie<Parcela> dh = new DynamickeHesovanie<>(1, 2, NAZOV_HS, NAZOV_PS);

        Parcela p1 = new Parcela(1, "1", suradnica, suradnica);
        Parcela p2 = new Parcela(2, "2", suradnica, suradnica);
        Parcela p3 = new Parcela(3, "3", suradnica, suradnica);
        Parcela p4 = new Parcela(4, "4", suradnica, suradnica);
        Parcela p5 = new Parcela(5, "5", suradnica, suradnica);
        Parcela p6 = new Parcela(6, "6", suradnica, suradnica);
        Parcela p7 = new Parcela(7, "7", suradnica, suradnica);

        dh.vloz(p1, Parcela.class);
        dh.vloz(p2, Parcela.class);
        dh.vloz(p3, Parcela.class);
        dh.vloz(p4, Parcela.class);
        dh.vloz(p5, Parcela.class);
        dh.vloz(p6, Parcela.class);
        dh.vloz(p7, Parcela.class);

        dh.vymaz(p2, Parcela.class);
        dh.vymaz(p6, Parcela.class);

        System.out.println("Hlavny");
        dh.vypisHlavnySubor(Parcela.class);
        System.out.println("Preplnujuci");
        dh.vypisPreplnujuciSubor(Parcela.class);
    }

    private static void vymazSubory()
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
