import Aplikacia.Prezenter;
import GUI.*;
import Testovanie.Tester;

public class Main
{
    public static final char REZIM = 'G';

    public static void main(String[] args)
    {
        if (REZIM == 'G')
        {
            new GUI(new Prezenter());
        }
        else if (REZIM == 'T')
        {
            Tester tester = new Tester();
            tester.replikacie(100);
        }
    }
}
