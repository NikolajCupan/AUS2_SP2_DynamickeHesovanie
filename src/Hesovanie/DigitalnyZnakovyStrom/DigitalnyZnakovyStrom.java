package Hesovanie.DigitalnyZnakovyStrom;

import Hesovanie.Block;
import Hesovanie.SpravcaSuborov;
import Ostatne.Status;
import Rozhrania.IData;

import java.util.BitSet;
import java.util.Stack;

public class DigitalnyZnakovyStrom
{
    private Vrchol root;

    public DigitalnyZnakovyStrom()
    {
        this.root = new ExternyVrchol();
    }

    public int getPocetElementov()
    {
        int pocet = 0;

        Stack<Vrchol> zasobnik = new Stack<>();
        zasobnik.push(this.root);

        while (!zasobnik.isEmpty())
        {
            Vrchol curVrchol = zasobnik.pop();

            if (curVrchol instanceof InternyVrchol internyVrchol)
            {
                zasobnik.push(internyVrchol.getLavySyn());
                zasobnik.push(internyVrchol.getPravySyn());
            }
            else if (curVrchol instanceof ExternyVrchol externyVrchol)
            {
                // Externy vrchol nema ziadnych synov
                pocet += externyVrchol.getPocetZaznamovBlock();
            }
        }

        return pocet;
    }

    public<T extends IData> void vloz(T pridavany, Class<T> typ, SpravcaSuborov spravcaSuborov)
    {
        // Traverzuj stromom, pokym sa nedostanes na Externy vrchol
        BitSet pridavanyZaznamHash = pridavany.getHash();
        Vrchol zaciatocnyVrchol = this.root;

        // Vytvorene pomocou new, aby bolo mozne pouzit ako output parameter
        int[] curBitHash = new int[]{ 0 };

        ExternyVrchol externyVrchol = this.traverzujNaExternyVrchol(zaciatocnyVrchol, pridavanyZaznamHash, curBitHash);
        this.vlozZaznamDoVrcholu(pridavany, typ, externyVrchol, spravcaSuborov, pridavanyZaznamHash, curBitHash[0]);
    }

    private ExternyVrchol traverzujNaExternyVrchol(Vrchol zaciatocnyVrchol, BitSet zaznamHash, int[] curBitHash)
    {
        Vrchol curVrchol = zaciatocnyVrchol;

        while (curVrchol instanceof InternyVrchol internyVrchol)
        {
            int hodnotaBitu = zaznamHash.get(curBitHash[0]) ? 1 : 0;
            curBitHash[0]++;

            if (hodnotaBitu == 0)
            {
                // Vlavo
                curVrchol = internyVrchol.getLavySyn();
            }
            else
            {
                // Vpravo
                curVrchol = internyVrchol.getPravySyn();
            }
        }

        // Dostal som sa na Externy vrchol
        return (ExternyVrchol)curVrchol;
    }

    private<T extends IData> void vlozZaznamDoVrcholu(T pridavany, Class<T> typ, ExternyVrchol externyVrchol, SpravcaSuborov spravcaSuborov,
                                                      BitSet pridavanyZaznamHash, int curBitHash)
    {
        Block<T> presuvanyBlock = null;
        long odlozenyOffset = externyVrchol.getOffset();

        while (true)
        {
            if (curBitHash >= pridavany.getPocetBitovHash())
            {
                // Pouzite vsetky bity z hesu
                if (presuvanyBlock != null)
                {
                    externyVrchol.vlozBlock(presuvanyBlock, typ, spravcaSuborov, odlozenyOffset);
                }

                externyVrchol.vloz(pridavany, typ, spravcaSuborov);
                break;
            }

            if (externyVrchol.getPocetZaznamovBlock() < spravcaSuborov.getBlokovaciFaktorHlavnySubor() &&
                presuvanyBlock == null)
            {
                // V Blocku je miesto pre pridavany Zaznam
                externyVrchol.vloz(pridavany, typ, spravcaSuborov);
                break;
            }

            // Metoda odpoji najdeny najdeny Externy vrchol od zvysku stromu
            // a nahradi ho novym Internym vrcholom
            InternyVrchol novyInternyVrchol = this.odpojExternyVrchol(externyVrchol);

            // Nacitaj Block z odpojeneho Externeho vrcholu
            // a presuvaj zaznamy do novo vytvorenych Externych vrcholov
            presuvanyBlock = (presuvanyBlock == null) ? externyVrchol.getBlock(typ, spravcaSuborov) : presuvanyBlock;
            Block<T> lavyBlock = new Block<>(spravcaSuborov.getBlokovaciFaktorHlavnySubor(), typ);
            Block<T> pravyBlock = new Block<>(spravcaSuborov.getBlokovaciFaktorHlavnySubor(), typ);

            for (T zaznam : presuvanyBlock.getZaznamy())
            {
                BitSet presuvanyZaznamHash = zaznam.getHash();
                int hodnotaBitu = presuvanyZaznamHash.get(curBitHash) ? 1 : 0;

                if (hodnotaBitu == 0)
                {
                    // Vlavo
                    lavyBlock.vloz(zaznam, spravcaSuborov);
                }
                else
                {
                    // Vpravo
                    pravyBlock.vloz(zaznam, spravcaSuborov);
                }
            }

            boolean novyPridavanyVlozeny = false;
            int hodnotaBituPridavany = pridavanyZaznamHash.get(curBitHash) ? 1 : 0;
            if (hodnotaBituPridavany == 0)
            {
                // Vlavo
                if (!lavyBlock.jeBlockPlny())
                {
                    lavyBlock.vloz(pridavany, spravcaSuborov);
                    novyPridavanyVlozeny = true;
                }
            }
            else
            {
                // Vpravo
                if (!pravyBlock.jeBlockPlny())
                {
                    pravyBlock.vloz(pridavany, spravcaSuborov);
                    novyPridavanyVlozeny = true;
                }
            }

            if (novyPridavanyVlozeny)
            {
                // Novy Zaznam bol uspesne vlozeny do stromu

                // Pouzi offset, ktory mal odpojeny Block
                ((ExternyVrchol)novyInternyVrchol.getLavySyn()).vlozBlock(lavyBlock, typ, spravcaSuborov, odlozenyOffset);
                // Nutne poziadat o novy offset
                ((ExternyVrchol)novyInternyVrchol.getPravySyn()).vlozBlock(pravyBlock, typ, spravcaSuborov, -1);

                break;
            }

            // Novy Zaznam nemohol byt vlozeny, cely proces je nutne opakovat
            presuvanyBlock = lavyBlock.jeBlockPlny() ? lavyBlock : pravyBlock;
            externyVrchol = lavyBlock.jeBlockPlny() ? (ExternyVrchol)novyInternyVrchol.getLavySyn() : (ExternyVrchol)novyInternyVrchol.getPravySyn();
            curBitHash++;
        }
    }

    private InternyVrchol odpojExternyVrchol(ExternyVrchol odpajany)
    {
        // Vytvor na mieste odpajaneho vrcholu novy Interny vrchol
        // a nastav referenciu v predchadzajucom vrchole
        InternyVrchol predchadzajuciVrchol = odpajany.getOtec();
        InternyVrchol novyInternyVrchol = new InternyVrchol();

        if (predchadzajuciVrchol == null)
        {
            // Odpajam koren
            this.root = novyInternyVrchol;
        }
        else
        {
            novyInternyVrchol.setOtec(predchadzajuciVrchol);
            if (odpajany.getStatus() == Status.LAVY_SYN)
            {
                predchadzajuciVrchol.setLavySyn(novyInternyVrchol);
            }
            else
            {
                predchadzajuciVrchol.setPravySyn(novyInternyVrchol);
            }
        }

        novyInternyVrchol.setLavySyn(new ExternyVrchol());
        novyInternyVrchol.getLavySyn().setOtec(novyInternyVrchol);

        novyInternyVrchol.setPravySyn(new ExternyVrchol());
        novyInternyVrchol.getPravySyn().setOtec(novyInternyVrchol);

        return novyInternyVrchol;
    }

    public<T extends IData> T vyhladaj(T vyhladavany, Class<T> typ, SpravcaSuborov spravcaSuborov)
    {
        // Prechadzaj stromom az pokym sa nedostanes na Externy vrchol
        BitSet vyhladavanyZaznamHash = vyhladavany.getHash();
        ExternyVrchol najdenyExternyVrchol = this.traverzujNaExternyVrchol(this.root, vyhladavanyZaznamHash, new int[]{ 0 });

        // Nacitaj Block a prehladaj ho
        Block<T> najdenyBlock = najdenyExternyVrchol.getBlock(typ, spravcaSuborov);
        return najdenyBlock.vyhladaj(vyhladavany, spravcaSuborov);
    }

    public<T extends IData> T vymaz(T vymazavany, Class<T> typ, SpravcaSuborov spravcaSuborov)
    {
        // Prechadzaj stromom az pokym sa nedostanes na Externy vrchol
        BitSet vymazavanyZaznamHash = vymazavany.getHash();
        ExternyVrchol najdenyExternyVrchol = this.traverzujNaExternyVrchol(this.root, vymazavanyZaznamHash, new int[]{ 0 });
        T realneVymazany = najdenyExternyVrchol.vymaz(vymazavany, typ, spravcaSuborov);

        if (realneVymazany == null)
        {
            // Zaznam sa v Subore nenachadza
            return null;
        }

        this.skusStriastVrchol(typ, najdenyExternyVrchol, spravcaSuborov);

        // TODO: Refactor mazania prazdnych vrcholov
        Block<T> block = najdenyExternyVrchol.getBlock(typ, spravcaSuborov);
        this.vymazPrazdneVrcholy(typ, najdenyExternyVrchol, block, spravcaSuborov);
        return realneVymazany;
    }

    private<T extends IData> void skusStriastVrchol(Class<T> typ, ExternyVrchol striasanyVrchol, SpravcaSuborov spravcaSuborov)
    {
        boolean moznoStriast = this.moznoStriast(striasanyVrchol, spravcaSuborov);
        if (!moznoStriast)
        {
            return;
        }

        // Pri striasani vzdy dojde k uvolneniu prave 1 Preplnujuceho blocku
        striasanyVrchol.strasVrchol(typ, spravcaSuborov);
    }

    // Metoda rozhodne, ci je mozne striasat dany Vrchol
    private boolean moznoStriast(ExternyVrchol externyVrchol, SpravcaSuborov spravcaSuborov)
    {
        if (externyVrchol.getPocetPreplnujucichBlockov() == 0)
        {
            // Striasaju sa Preplnujuce blocky, ak ziadny
            // neexistuje, tak nie je co striast
            return false;
        }

        int maxHlavnySubor = spravcaSuborov.getBlokovaciFaktorHlavnySubor();
        int maxPreplnujuciSubor = spravcaSuborov.getBlokovaciFaktorPreplnujuciSubor();

        int pocetZaznamovVrchol = externyVrchol.getPocetZaznamovBlock();
        int pocetPreplnujucichBlockovVrchol = externyVrchol.getPocetPreplnujucichBlockov();

        int maxKapacita = maxHlavnySubor + (maxPreplnujuciSubor * pocetPreplnujucichBlockovVrchol);
        int maxKapacitaBezPreplnujucehoBlocku = maxKapacita - maxPreplnujuciSubor;

        if (pocetZaznamovVrchol > maxKapacitaBezPreplnujucehoBlocku)
        {
            // Nie je mozne striast
            return false;
        }
        else if (pocetZaznamovVrchol == maxKapacitaBezPreplnujucehoBlocku)
        {
            // Mozno striast
            return true;
        }
        else
        {
            // Striasanie malo byt uz vykonane v minulosti
            throw new RuntimeException("Striasane nebolo vykonane!");
        }
    }

    private<T extends IData> void vymazPrazdneVrcholy(Class<T> typ, ExternyVrchol spracovanyVrchol, Block<T> kumulovanyBlock, SpravcaSuborov spravcaSuborov)
    {
        ExternyVrchol curSpracuvanyVrchol = spracovanyVrchol;

        if (curSpracuvanyVrchol.getStatus() == Status.KOREN)
        {
            this.mazaneZKorena(spravcaSuborov, kumulovanyBlock, typ);
            return;
        }

        while (true)
        {
            int pocetKumulovanyBlock = kumulovanyBlock.getPocetPlatnychZaznamov();

            if (curSpracuvanyVrchol.getStatus() == Status.KOREN)
            {
                // Dostali sme sa az na koren
                break;
            }

            if (curSpracuvanyVrchol.getSurodenec() instanceof InternyVrchol)
            {
                // Ak surodenec je Internym vrcholom, tak urcite nie je mozne vykonat mazanie Vrcholov
                break;
            }

            ExternyVrchol surodenec = (ExternyVrchol)curSpracuvanyVrchol.getSurodenec();
            int spoluElementov = surodenec.getPocetZaznamovBlock() + pocetKumulovanyBlock;

            if (spoluElementov > spravcaSuborov.getBlokovaciFaktorHlavnySubor())
            {
                // Vrcholy nemozno zlucit, nakolko sa Zaznamy nezmestia do jedineho Blocku
                break;
            }

            // Zluc zaznamy do kumulovaneho Blocku
            Block<T> surodenecBlock = surodenec.getBlock(typ, spravcaSuborov);
            if (surodenecBlock != null)
            {
                for (T zaznam : surodenecBlock.getZaznamy())
                {
                    kumulovanyBlock.vloz(zaznam, spravcaSuborov);
                }
            }

            curSpracuvanyVrchol = this.odpojInternyVrchol(curSpracuvanyVrchol.getOtec(), spravcaSuborov, typ);
        }

        if (kumulovanyBlock.getPocetPlatnychZaznamov() != 0)
        {
            curSpracuvanyVrchol.vlozBlock(kumulovanyBlock, typ, spravcaSuborov, -1);
        }
        else
        {
            spravcaSuborov.uvolniBlockHlavnySubor(curSpracuvanyVrchol.getOffset(), typ);
            curSpracuvanyVrchol.setOffset(-1);
            curSpracuvanyVrchol.setPocetZaznamovBlock(0);
        }
    }

    private<T extends IData> void mazaneZKorena(SpravcaSuborov spravcaSuborov, Block<T> aktualizovanyBlock, Class<T> typ)
    {
        ExternyVrchol korenExterny = (ExternyVrchol)this.root;

        if (aktualizovanyBlock.getPocetPlatnychZaznamov() == 0)
        {
            spravcaSuborov.uvolniBlockHlavnySubor(korenExterny.getOffset(), typ);
            this.root = new ExternyVrchol();
        }
        else
        {
            korenExterny.vlozBlock(aktualizovanyBlock, typ, spravcaSuborov, korenExterny.getOffset());
        }
    }

    private<T extends IData> ExternyVrchol odpojInternyVrchol(InternyVrchol odpajany, SpravcaSuborov spravcaSuborov, Class<T> typ)
    {
        this.uvolniBlocky(odpajany, spravcaSuborov, typ);

        InternyVrchol otecOdpajaneho = odpajany.getOtec();
        ExternyVrchol novyExternyVrchol = new ExternyVrchol();
        novyExternyVrchol.setOtec(otecOdpajaneho);

        if (odpajany.getStatus() == Status.LAVY_SYN)
        {
            otecOdpajaneho.setLavySyn(novyExternyVrchol);
        }
        else if (odpajany.getStatus() == Status.PRAVY_SYN)
        {
            otecOdpajaneho.setPravySyn(novyExternyVrchol);
        }
        else if (odpajany.getStatus() == Status.KOREN)
        {
            this.root = novyExternyVrchol;
        }

        return novyExternyVrchol;
    }

    private<T extends IData> void uvolniBlocky(InternyVrchol odpajany, SpravcaSuborov spravcaSuborov, Class<T> typ)
    {
        ExternyVrchol lavySyn = (ExternyVrchol)odpajany.getLavySyn();
        ExternyVrchol pravySyn = (ExternyVrchol)odpajany.getPravySyn();

        if (lavySyn.getOffset() != -1)
        {
            spravcaSuborov.uvolniBlockHlavnySubor(lavySyn.getOffset(), typ);
        }

        if (pravySyn.getOffset() != -1)
        {
            spravcaSuborov.uvolniBlockHlavnySubor(pravySyn.getOffset(), typ);
        }
    }

    public<T extends IData> void vypisStrom(SpravcaSuborov spravcaSuborov, Class<T> typ)
    {
        Stack<Vrchol> zasobnik = new Stack<>();
        zasobnik.push(this.root);

        while (!zasobnik.isEmpty())
        {
            Vrchol curVrchol = zasobnik.pop();

            if (curVrchol instanceof InternyVrchol internyVrchol)
            {
                zasobnik.push(internyVrchol.getLavySyn());
                zasobnik.push(internyVrchol.getPravySyn());
            }
            else
            {
                System.out.println(((ExternyVrchol)curVrchol).naString(spravcaSuborov, typ) + "\n");
            }
        }
    }
}
