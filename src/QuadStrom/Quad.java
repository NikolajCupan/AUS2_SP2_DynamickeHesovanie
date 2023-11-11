package QuadStrom;

import Objekty.Polygon;
import Objekty.Suradnica;
import Ostatne.IPolygon;

import java.util.ArrayList;

public class Quad<T extends IPolygon> extends Polygon
{
    // SZ | SV    0 | 1
    // -- . --    - . -
    // JZ | JV    3 | 2
    private static final int SZ = 0;
    private static final int SV = 1;
    private static final int JV = 2;
    private static final int JZ = 3;

    private static final int POCET_PODQUADOV = 4;

    private final ArrayList<T> data;

    // Uroven je cislovana od 0, korenovy quad ma uroven 0
    private final int urovenQuadu;

    // Quady idu za sebou v nasledujucom poradi:
    //                                   x  y  i
    // -> Severo-zapad  (vlavo hore)  -> -  +  0
    // -> Severo-vychod (vpravo hore) -> +  +  1
    // -> Juho-vychod   (vpravo dole) -> +  -  2
    // -> Juho-zapad    (vlavo dole)  -> -  -  3
    private Quad<T>[] podQuady;

    public Quad(Suradnica suradnica1, Suradnica suradnica2, int urovenQuadu)
    {
        this.nastavSuradnice(suradnica1, suradnica2);

        this.data = new ArrayList<>();
        this.urovenQuadu = urovenQuadu;
        this.podQuady = new Quad[POCET_PODQUADOV];
    }

    // Metoda rozdeli dany quad na 4 rovnako velke podQuady
    // Priklad:
    // -> zaklad: {  0;   0}, {100; 100}
    //     -> SZ: {  0;  50}, { 50; 100}
    //     -> SV: { 50;  50}, {100; 100}
    //     -> JV: { 50;   0}, {100;  50}
    //     -> JZ: {  0;   0}, { 50;  50}
    public void rozdel()
    {
        if (this.jeRozdeleny())
        {
            throw new RuntimeException("Quad uz je rozdeleny!");
        }

        double stredX = (this.surVlavoDole.getX() + this.surVpravoHore.getX()) / 2;
        double stredY = (this.surVlavoDole.getY() + this.surVpravoHore.getY()) / 2;

        Suradnica SZvlavoDole = new Suradnica(this.surVlavoDole.getX(), stredY);
        Suradnica SZvpravoHore = new Suradnica(stredX, this.surVpravoHore.getY());
        this.podQuady[SZ] = new Quad<T>(SZvlavoDole, SZvpravoHore, this.urovenQuadu + 1);

        Suradnica SVvlavoDole = new Suradnica(stredX, stredY);
        Suradnica SVvpravoHore = new Suradnica(this.surVpravoHore.getX(), this.surVpravoHore.getY());
        this.podQuady[SV] = new Quad<T>(SVvlavoDole, SVvpravoHore, this.urovenQuadu + 1);

        Suradnica JVvlavoDole = new Suradnica(stredX, this.surVlavoDole.getY());
        Suradnica JVvpravoHore = new Suradnica(this.surVpravoHore.getX(), stredY);
        this.podQuady[JV] = new Quad<T>(JVvlavoDole, JVvpravoHore, this.urovenQuadu + 1);

        Suradnica JZvlavoDole = new Suradnica(this.surVlavoDole.getX(), this.surVlavoDole.getY());
        Suradnica JZvpravoHore = new Suradnica(stredX, stredY);
        this.podQuady[JZ] = new Quad<T>(JZvlavoDole, JZvpravoHore, this.urovenQuadu + 1);
    }

    public void vymazPodQuady()
    {
        for (Quad<T> podQuady : this.podQuady)
        {
            if (!podQuady.getData().isEmpty() || podQuady.jeRozdeleny())
            {
                throw new RuntimeException("Nie je mozne zmazat quad, ktory obsahuje data!");
            }
        }

        this.podQuady = new Quad[POCET_PODQUADOV];
    }

    // Zmaze podquady hoci obsahuju data
    public void forceVymazPodQuady()
    {
        this.podQuady = new Quad[POCET_PODQUADOV];
    }

    public boolean jeRozdeleny()
    {
        return this.podQuady[SZ] != null ||
               this.podQuady[SV] != null ||
               this.podQuady[JV] != null ||
               this.podQuady[JZ] != null;
    }

    public int getUrovenQuadu()
    {
        return this.urovenQuadu;
    }

    public ArrayList<T> getData()
    {
        return this.data;
    }

    public Quad<T>[] getPodQuady()
    {
        return this.podQuady;
    }
}
