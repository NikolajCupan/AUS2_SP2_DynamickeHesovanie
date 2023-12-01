import Hesovanie.DynamickeHesovanie;
import Objekty.Parcela;
import Objekty.Suradnica;

public class Main
{
    public static final String NAZOV_HS = "hlavny";
    public static final String NAZOV_PS = "preplnujuci";

    public static void main(String[] args)
    {
        Suradnica suradnica = new Suradnica();
        DynamickeHesovanie<Parcela> dh = new DynamickeHesovanie<>(1, 2, NAZOV_HS, NAZOV_PS, Parcela.class);
        dh.vymazSubory();

        Parcela p1 = new Parcela(1, "1", suradnica, suradnica);
        Parcela p2 = new Parcela(2, "2", suradnica, suradnica);

        dh.vloz(p1);
        dh.vloz(p2);

        dh.vymaz(p1);
        dh.vymaz(p2);

        dh.vymazSubory();
    }
}
