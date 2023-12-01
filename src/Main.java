import Aplikacia.Aplikacia;
import Objekty.Nehnutelnost;
import Objekty.Parcela;
import Testovanie.Tester;

public class Main
{
    public static final char REZIM = 'G';

    public static void main(String[] args)
    {
        if (REZIM == 'G')
        {
            Aplikacia aplikacia = new Aplikacia();
            aplikacia.inicializuj(10, 10, "hlavny", "preplnujuci",
                                  0, 0, 100, 100, 10);
            aplikacia.resetuj();

            aplikacia.vlozNehnutelnost(1, "neh1", 0, 0, 50, 50);
            aplikacia.vlozParcelu("par1", 0, 0, 10, 10);
            aplikacia.vlozParcelu("par2", 0, 0, 20, 20);
            aplikacia.vlozParcelu("par3", 0, 0, 30, 30);
            aplikacia.vlozParcelu("par4", 0, 0, 35, 35);
            aplikacia.vlozParcelu("par5", 0, 0, 40, 40);
            aplikacia.vlozParcelu("par6", 0, 0, 50, 50);

            Nehnutelnost pred = aplikacia.vyhladajNehnutelnost(1);
            Parcela v4 = aplikacia.vymazParcelu(4);
            Nehnutelnost po = aplikacia.vyhladajNehnutelnost(1);

            aplikacia.resetuj();
        }
        else if (REZIM == 'T')
        {
            Tester tester = new Tester();
            tester.replikacie(100);
        }
    }
}
