import Aplikacia.Databaza;
import Testovanie.Tester;

public class Main
{
    public static final char REZIM = 'G';

    public static void main(String[] args)
    {
        if (REZIM == 'G')
        {
            Databaza databaza = new Databaza();
            //databaza.obnovAplikaciu();

            databaza.resetuj(1, 10,
                             0, 0, 100, 100, 10);

            boolean okn1 = databaza.vlozNehnutelnost(1, "neh1", 0, 0, 50, 50, -1, false);
            boolean okn2 = databaza.vlozNehnutelnost(2, "neh2", 60, 60, 100, 100, -1, false);
            boolean okn3 = databaza.vlozNehnutelnost(3, "neh3", 0, 60, 40, 100, -1, false);

            boolean okp1 = databaza.vlozParcelu("par1", 45, 45, 100, 100, -1, false);
            boolean okp2 = databaza.vlozParcelu("par2", 0, 0, 20, 20, -1, false);
            boolean okp3 = databaza.vlozParcelu("par3", 0, 0, 20, 20, -1, false);

            boolean oka = databaza.ukonciAplikaciu();
            int x = 100;
        }
        else if (REZIM == 'T')
        {
            Tester tester = new Tester();
            tester.replikacie(100);
        }
    }
}
