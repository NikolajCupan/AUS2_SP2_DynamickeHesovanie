package QuadStrom;

import Objekty.Polygon;
import Objekty.Suradnica;
import Rozhrania.IPolygon;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;

public class QuadStrom<T extends IPolygon> implements Iterable<Quad<T>>
{
    // Po kazdych OPTIMALIZUJ_NA operacii (vloz a vymaz),
    // sa vykona pokus o optimalizaciu struktury
    private static final int OPTIMALIZUJ_NA = 10000;
    private int pocitadloOperacii = 0;

    // V pripade ak quady na urovni maxUroven obsahuju menej ako PRILIS_PRAZDNE
    // percent dat, tak su zrusene vsetky urovne az pokym sa nedosiahne aspon
    // PRILIS_PRAZDNE percent dat na danej urovni
    // Naopak v pripade ak quady na urovni maxUroven obsahuju viac ako PRILIS_PLNE
    // percent dat, tak je pridanych dalsich ZVYS_MAX_UROVEN_O urovni
    private static final double PRILIS_PLNE = 0.20;
    private static final double PRILIS_PRAZDNE = 0.05;
    private static final int ZVYS_MAX_UROVEN_O = 5;

    // Najhlbsia mozna uroven dosiahnutelna v strome, elementy nie je mozne vlozit hlbsie
    private int maxUroven;
    private Quad<T> rootQuad;

    public QuadStrom(double vlavoDoleX, double vlavoDoleY, double vpravoHoreX, double vpravoHoreY, int maxUroven)
    {
        this.maxUroven = maxUroven;
        Suradnica suradnica1 = new Suradnica(vlavoDoleX, vlavoDoleY);
        Suradnica suradnica2 = new Suradnica(vpravoHoreX, vpravoHoreY);
        this.rootQuad = new Quad<T>(suradnica1, suradnica2, 0);
    }

    public int getPocetElementov()
    {
        int pocet = 0;

        for (Quad<T> quad : this)
        {
            pocet += quad.getData().size();
        }

        return pocet;
    }

    public void vloz(T pridavany)
    {
        this.pocitadloOperacii++;
        this.skusOptimalizovat();

        Quad<T> curQuad = this.rootQuad;

        while (true)
        {
            // Dosiahnuta maximalna uroveb => nejde sa hlbsie
            if (curQuad.getUrovenQuadu() >= this.maxUroven)
            {
                curQuad.getData().add(pridavany);
                break;
            }

            if (!curQuad.jeRozdeleny() && curQuad.getData().isEmpty())
            {
                // Dostal som sa na list, ktory je prazdny
                // Nie je nutne ist hlbsie
                curQuad.getData().add(pridavany);
                break;
            }

            // Ak je quad uz teraz rozdeleny, tak nebudem moct zrusit jeho podQuady
            boolean podQuadyPrazdne = !curQuad.jeRozdeleny();

            if (!curQuad.jeRozdeleny())
            {
                // Dostal som sa na list, ktory nie je prazdny
                curQuad.rozdel();

                // Ak sa v liste nachadza iba 1 element, tak je mozne,
                // ze tento bude mozne vlozit hlbsie
                if (curQuad.getData().size() == 1)
                {
                    // Vytlaceny element hned vlozim
                    podQuadyPrazdne = this.vlozVytlaceny(curQuad, curQuad.getData().remove(0));
                }
            }

            // Zistim, kde bude vlozeny novy element (parameter metody)
            boolean novyVPodQuade = false;
            for (Quad<T> podQuad : curQuad.getPodQuady())
            {
                // Polygon sa moze nachadzat maximalne v 1 podQuade
                if (podQuad.leziVnutri(pridavany))
                {
                    curQuad = podQuad;
                    novyVPodQuade = true;
                    break;
                }
            }

            if (!novyVPodQuade)
            {
                // Ziadny podquad nevyhovuje
                if (curQuad.leziVnutri(pridavany))
                {
                    // Ak vytlaceny element nebol vlozeny do podQuadu, tak nie je nutne, aby tieto existovali
                    if (podQuadyPrazdne)
                    {
                        curQuad.vymazPodQuady();
                    }

                    curQuad.getData().add(pridavany);
                    break;
                }
                else
                {
                    throw new RuntimeException("Neplatny vkladany element!");
                }
            }
        }
    }

    // False -> element bol vlozeny do podquadu
    // True  -> element bol vlozeny do quadu
    private boolean vlozVytlaceny(Quad<T> quad, T vytlaceny)
    {
        // Quad bol rozdeleny pred zavolanim tejto metody
        for (Quad<T> podQuad : quad.getPodQuady())
        {
            if (podQuad.leziVnutri(vytlaceny))
            {
                podQuad.getData().add(vytlaceny);
                return false;
            }
        }

        // Vytlaceny element sa nezmesti do ziadneho podquadu
        quad.getData().add(vytlaceny);
        return true;
    }

    // Vyhladavanie podla suradnice
    public ArrayList<T> vyhladaj(double x, double y)
    {
        ArrayList<T> najdene = new ArrayList<>();
        Quad<T> curQuad = this.rootQuad;

        if (!curQuad.leziVnutri(x, y))
        {
            // Specialna situacia, kedy zvolena suradnica lezi mimo najvacsi quad,
            // v takomto pripade vyhladavanie moze skoncit okamzite
            return najdene;
        }

        while (true)
        {
            for (T element : curQuad.getData())
            {
                if (element.leziVnutri(x, y))
                {
                    najdene.add(element);
                }
            }

            // Quad nemusi byt rozdeleny
            if (curQuad.jeRozdeleny())
            {
                // Suradnica moze lezat maximalne v 1 podquade
                for (Quad<T> podQuad : curQuad.getPodQuady())
                {
                    if (podQuad.leziVnutri(x, y))
                    {
                        curQuad = podQuad;
                        break;
                    }
                }
            }
            else
            {
                break;
            }
        }

        return najdene;
    }

    // Vyhladavenie podla polygonu
    public ArrayList<T> vyhladaj(double vlavoDoleX, double vlavoDoleY, double vpravoHoreX, double vpravoHoreY)
    {
        Polygon prehladavanaOblast = new Polygon();
        prehladavanaOblast.nastavSuradnice(new Suradnica(vlavoDoleX, vlavoDoleY),
                new Suradnica(vpravoHoreX, vpravoHoreY));

        ArrayList<T> najdene = new ArrayList<>();
        Stack<Quad<T>> zasobnik = new Stack<>();
        zasobnik.push(this.rootQuad);

        while (!zasobnik.isEmpty())
        {
            Quad<T> curQuad = zasobnik.pop();

            for (T element : curQuad.getData())
            {
                if (element.prekryva(prehladavanaOblast))
                {
                    najdene.add(element);
                }
            }

            // Quad nemusi byt rozdeleny
            if (curQuad.jeRozdeleny())
            {
                for (Quad<T> podQuad : curQuad.getPodQuady())
                {
                    // Do zasobnika vlozim vsetky quady, ktore sa prekryvaju s prehladavanou oblastou
                    if (podQuad.prekryva(prehladavanaOblast))
                    {
                        zasobnik.push(podQuad);
                    }
                }
            }
        }

        return najdene;
    }

    // Metoda vrati zmazany element,
    // v pripade ak ziadny zmazany nebol, tak vrati null
    public T vymaz(double x, double y, T vymazavany)
    {
        this.pocitadloOperacii++;
        this.skusOptimalizovat();

        Quad<T> curQuad = this.rootQuad;
        Stack<Quad<T>> cesta = new Stack<>();

        while (true)
        {
            cesta.push(curQuad);

            for (T element : curQuad.getData())
            {
                if (element.leziVnutri(x, y) && element.equals(vymazavany))
                {
                    curQuad.getData().remove(element);
                    this.vymazPrazdneQuady(cesta);
                    return element;
                }
            }

            if (!curQuad.jeRozdeleny())
            {
                // Nie je mozne dalej hladat, dany element neexistuje
                return null;
            }

            for (Quad<T> podQuad : curQuad.getPodQuady())
            {
                if (podQuad.leziVnutri(x, y))
                {
                    curQuad = podQuad;
                    break;
                }
            }
        }
    }

    // Po zmazani elementu sa zmazu vsetky quady, ktore uz nemusia existovat
    private void vymazPrazdneQuady(Stack<Quad<T>> cesta)
    {
        Quad<T> dno = cesta.pop();
        if (dno.getData().size() > 1 || dno.getUrovenQuadu() == 0)
        {
            return;
        }

        if (dno.jeRozdeleny() && dno.getData().isEmpty())
        {
            // V takejto situacii existuje moznost, ze sa v celom podstrome
            // daneho quadu nachadza prave jeden prvok, v takom pripade
            // mozem tento presunut plytsie

            // Ak je ktorykolvek podQuad rozdeleny, tak nie je mozne mazat
            int pocetPodQuady = 0;
            boolean existujeRozdelenyPodQuad = false;
            for (Quad<T> podQuad : dno.getPodQuady())
            {
                pocetPodQuady += podQuad.getData().size();

                if (podQuad.jeRozdeleny())
                {
                    existujeRozdelenyPodQuad = true;
                }
            }

            if (!existujeRozdelenyPodQuad)
            {
                if (pocetPodQuady == 0)
                {
                    throw new RuntimeException("Existuje prazdny podstrom!");
                }
                else if (pocetPodQuady == 1)
                {
                    T vytlacenyElement = null;
                    for (Quad<T> podQuad : dno.getPodQuady())
                    {
                        if (!podQuad.getData().isEmpty())
                        {
                            vytlacenyElement = podQuad.getData().remove(0);
                        }
                    }

                    dno.getData().add(vytlacenyElement);
                    dno.forceVymazPodQuady();
                }
            }
        }

        while (!cesta.isEmpty())
        {
            Quad<T> vyssi = cesta.pop();

            // Ak je ktorykolvek podQuad rozdeleny, tak nie je mozne mazat
            for (Quad<T> podQuad : vyssi.getPodQuady())
            {
                if (podQuad.jeRozdeleny())
                {
                    return;
                }
            }

            // Pocet elementov, ktore sa nachadzaju v podquadoch
            int pocetElementovPodQuady = 0;
            for (Quad<T> podQuad : vyssi.getPodQuady())
            {
                pocetElementovPodQuady += podQuad.getData().size();
            }

            // Ak je tento pocet vacsi ako 1, tak nie je mozne mazat
            if (pocetElementovPodQuady > 1)
            {
                return;
            }

            // Rovnako nie je mozne mazat ak vyssi quad obsahuje data a zaroven existuje element aj v podquadoch
            if (!vyssi.getData().isEmpty() && pocetElementovPodQuady == 1)
            {
                return;
            }

            // Ak som sa dostal az sem, tak mozem vykonat mazanie
            // V podquadoch sa moze nachadzat prave 1 alebo 0 elementov
            T vytlacenyElement = null;
            for (Quad<T> podQuad : vyssi.getPodQuady())
            {
                if (!podQuad.getData().isEmpty())
                {
                    vytlacenyElement = podQuad.getData().remove(0);
                    break;
                }
            }

            vyssi.vymazPodQuady();
            if (vytlacenyElement != null)
            {
                vyssi.getData().add(vytlacenyElement);
            }
        }
    }

    // Metoda presunie data z quadov, ktore maju uroven hlbsiu
    // ako uroven dana parametrom, do quadov o hlbke parametra
    public void presunPlytsie(int uroven)
    {
        Stack<Quad<T>> zasobnik = new Stack<>();
        zasobnik.push(this.getRootQuad());

        while (!zasobnik.isEmpty())
        {
            Quad<T> curQuad = zasobnik.pop();

            if (!curQuad.jeRozdeleny())
            {
                // Nie je potrebne nic riesit nakolko quad nie je rozdeleny
                continue;
            }

            if (curQuad.getUrovenQuadu() < uroven)
            {
                // V tomto pripade nie je nutne nic robit,
                // iba si vlozim podquady do zasobnika
                for (Quad<T> podQuad : curQuad.getPodQuady())
                {
                    zasobnik.push(podQuad);
                }

                continue;
            }

            // Nasiel som quad, do ktoreho budem presuvat data z nizsich quadov
            Stack<Quad<T>> podQuadyZasobnik = new Stack<>();
            podQuadyZasobnik.push(curQuad);
            ArrayList<Quad<T>> podQuadyZoznam = new ArrayList<>();

            while (!podQuadyZasobnik.isEmpty())
            {
                Quad<T> curPodQuad = podQuadyZasobnik.pop();

                if (curPodQuad.jeRozdeleny())
                {
                    for (Quad<T> podQuad : curPodQuad.getPodQuady())
                    {
                        podQuadyZasobnik.push(podQuad);
                        podQuadyZoznam.add(podQuad);
                    }
                }
            }

            // Presuniem data z podquadov
            for (Quad<T> podQuad : podQuadyZoznam)
            {
                curQuad.getData().addAll(podQuad.getData());
            }

            // V tomto pripade mozem podquady zrusit hoci obsahuju data,
            // nakolko tieto som si uz presunul vyssie
            curQuad.forceVymazPodQuady();
        }
    }

    // Uroven quadu s najhlbsou urovnou
    public int getHlbkaStromu()
    {
        int najhlbsiaUroven = 0;

        for (Quad<T> quad : this)
        {
            if (najhlbsiaUroven < quad.getUrovenQuadu())
            {
                najhlbsiaUroven = quad.getUrovenQuadu();
            }
        }

        return najhlbsiaUroven;
    }

    // Optimalizacia sa vykona za akychkolvek okolnosti,
    // v pripade ak zdravie patri do intervalu (0; 1> vykona sa
    // optimalizacia ako pri PRILIS_PLNE, v opacnom pripade sa vykona
    // optimalizacia ako pri PRILIS_PRAZDNE
    public void forceOptimalizuj()
    {
        double[] pomerUroven = this.getPomerUroven();
        double zdravie = this.getZdravie(pomerUroven[this.maxUroven]);
        this.pocitadloOperacii = 0;

        if (zdravie == 0.0)
        {
            // Vykonavat tento typ optimalizacie, ked zdravie nie je rovne 0.0
            // nema zmysel, pretoze v takom pripade ma najhlbsia uroven
            // viac ako PRILIS_PRAZDNE percent dat, tym padom by optimalizacia
            // nesposobila ziadnu zmenu
            this.optimalizuj(pomerUroven, zdravie);
        }
        else
        {
            // Druhy parameter explicitne nastavim na 1.0,
            // aby sa optimalizacia vykonala za akychkolvek okolnosti
            this.optimalizuj(pomerUroven, 1.0);
        }
    }

    private void skusOptimalizovat()
    {
        if (this.pocitadloOperacii >= OPTIMALIZUJ_NA)
        {
            this.pocitadloOperacii = 0;
            double[] pomerUroven = this.getPomerUroven();
            double zdravie = this.getZdravie(pomerUroven[this.maxUroven]);

            if (zdravie == 0.0 || zdravie == 1.0)
            {
                this.optimalizuj(pomerUroven, zdravie);
            }
        }
    }

    private void optimalizuj(double[] pomerUroven, double zdravie)
    {
        if (Double.isNaN(pomerUroven[0]))
        {
            return;
        }

        if (zdravie == 0.0)
        {
            // V tomto pripade su quady na urovni maxUroven prilis prazdne,
            // bude sa vykonavat rusenie quadov

            // Spocitavaj pomery dat na jednlitych urovniach (od najhlbsej),
            // kym sa nedosiahne aspon PRILIS_PRAZDNE percent dat
            double curPomer = 0.0;
            int novaMaxUroven = 0;
            for (int i = this.maxUroven; i >= 0; i--)
            {
                curPomer += pomerUroven[i];
                if (curPomer > PRILIS_PRAZDNE)
                {
                    novaMaxUroven = i;
                    break;
                }
            }

            // Presun data plytsie
            this.presunPlytsie(novaMaxUroven);
            this.setMaxUroven(novaMaxUroven);
        }
        else
        {
            // V tomto pripade su quady na urovni maxUroven prilis plne,
            // vytvorim si novy root quad s maxHlbka o ZVYS_MAX_UROVEN_O
            // vacsou a presuniem tam vsetky data

            Quad<T> oldRootQuad = this.rootQuad;
            Suradnica surVlavoDole = new Suradnica(oldRootQuad.getVlavoDoleX(), oldRootQuad.getVlavoDoleY());
            Suradnica surVpravoHore = new Suradnica(oldRootQuad.getVpravoHoreX(), oldRootQuad.getVpravoHoreY());
            this.rootQuad = new Quad<T>(surVlavoDole, surVpravoHore, 0);
            this.setMaxUroven(this.maxUroven + ZVYS_MAX_UROVEN_O);

            Stack<Quad<T>> zasobnik = new Stack<>();
            zasobnik.push(oldRootQuad);

            while (!zasobnik.isEmpty())
            {
                Quad<T> curQuad = zasobnik.pop();
                if (curQuad.jeRozdeleny())
                {
                    for (Quad<T> podQuad : curQuad.getPodQuady())
                    {
                        zasobnik.push(podQuad);
                    }
                }

                for (T element : curQuad.getData())
                {
                    this.vloz(element);
                    // Pocitadlo operacii vynulujem po kazdej operacii, aby nevznikol pokus
                    // o optimalizaciu pocas prebiehajucej optimalizacie
                    this.pocitadloOperacii = 0;
                }
            }
        }
    }

    // Metoda transformuje pomer dat na danej urovni na interval <0; 1>
    // 0      => quady na urovni maxUroven su prilis prazdne
    // (0; 1) => quady na urovni maxUroven nie su ani prilis plne, ani prilis prazdne
    // 1      => quady na urovni maxUroven su prilis plne
    public double getZdravie(double naplnenost)
    {
        if (naplnenost <= PRILIS_PRAZDNE)
        {
            return 0.0;
        }

        if (naplnenost >= PRILIS_PLNE)
        {
            return 1.0;
        }

        double rozsah = PRILIS_PLNE - PRILIS_PRAZDNE;
        double naplnenostShift = naplnenost - PRILIS_PRAZDNE;

        return naplnenostShift / rozsah;
    }

    // Metoda vrati kolko percent zo vsetkych dat
    // sa nachadza na jednotlivych urovniach
    public double[] getPomerUroven()
    {
        int[] pocetUroven = new int[this.maxUroven + 1];
        double[] pomerUroven = new double[this.maxUroven + 1];

        for (Quad<T> quad : this)
        {
            int urovenQuadu = quad.getUrovenQuadu();
            pocetUroven[urovenQuadu] += quad.getData().size();
        }

        int pocetCelkom = this.getPocetElementov();

        for (int i = 0; i < this.maxUroven + 1; i++)
        {
            pomerUroven[i] = (double)pocetUroven[i] / pocetCelkom;
        }

        return pomerUroven;
    }

    public void setMaxUroven(int novaMaxUroven)
    {
        int najhlbsiaUroven = this.getHlbkaStromu();

        if (novaMaxUroven < najhlbsiaUroven)
        {
            throw new RuntimeException("Existuju data na hlbsich urovniach!");
        }

        this.maxUroven = novaMaxUroven;
    }

    public int getMaxUroven()
    {
        return this.maxUroven;
    }

    public int getPocitadloOperacii()
    {
        return this.pocitadloOperacii;
    }

    public Quad<T> getRootQuad()
    {
        return this.rootQuad;
    }

    public int getOptimalizujNa()
    {
        return OPTIMALIZUJ_NA;
    }

    public double getPrilisPrazdne()
    {
        return PRILIS_PRAZDNE;
    }

    public double getPrilisPlne()
    {
        return PRILIS_PLNE;
    }

    public Iterator<Quad<T>> iterator()
    {
        return new QSIterator();
    }

    // Pre-order iterator
    private class QSIterator implements Iterator<Quad<T>>
    {
        private final Stack<Quad<T>> quady;

        public QSIterator()
        {
            this.quady = new Stack<>();
            this.quady.push(QuadStrom.this.rootQuad);
        }

        @Override
        public boolean hasNext()
        {
            return !this.quady.isEmpty();
        }

        @Override
        public Quad<T> next()
        {
            if (!this.hasNext())
            {
                throw new NoSuchElementException();
            }

            Quad<T> curQuad = this.quady.pop();

            if (curQuad.jeRozdeleny())
            {
                for (Quad<T> podQuad : curQuad.getPodQuady())
                {
                    this.quady.push(podQuad);
                }
            }

            return curQuad;
        }
    }
}
