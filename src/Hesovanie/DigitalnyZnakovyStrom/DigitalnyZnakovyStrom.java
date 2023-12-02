package Hesovanie.DigitalnyZnakovyStrom;

import Hesovanie.Block;
import Hesovanie.SpravcaSuborov;
import Ostatne.Status;
import Rozhrania.IData;

import java.io.Serializable;
import java.util.BitSet;
import java.util.Stack;

public class DigitalnyZnakovyStrom implements Serializable
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
                pocet += externyVrchol.getPocetZaznamovBlocky();
            }
        }

        return pocet;
    }

    public<T extends IData> boolean aktualizuj(T aktualizovany, Class<T> typ, SpravcaSuborov spravcaSuborov)
    {
        // Traverzuj stromom, pokym sa nedostanes na Externy vrchol
        BitSet pridavanyZaznamHash = aktualizovany.getHash();
        Vrchol zaciatocnyVrchol = this.root;

        // Vytvorene pomocou new, aby bolo mozne pouzit ako output parameter
        int[] curBitHash = new int[]{ 0 };

        ExternyVrchol externyVrchol = this.traverzujNaExternyVrchol(zaciatocnyVrchol, pridavanyZaznamHash, curBitHash);
        Block<T> najdenyBlock = externyVrchol.getBlock(typ, spravcaSuborov);

        if (najdenyBlock != null)
        {
            return najdenyBlock.aktualizuj(aktualizovany, spravcaSuborov, externyVrchol.getOffset());
        }

        // Ak Block neexistuje, tak k aktualizacii urcite nedoslo
        return false;
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

            if (externyVrchol.getPocetZaznamovBlocky() < spravcaSuborov.getBlokovaciFaktorHlavnySubor() &&
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

        if (najdenyExternyVrchol.getOffset() == -1)
        {
            // Externy vrchol nema prideleny offset, je prazdny
            return null;
        }

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

        // Moze dojst k uvolneniu 1 alebo 0 Preplnujucich blockov
        this.skusStriastVrchol(typ, najdenyExternyVrchol, spravcaSuborov);

        if (najdenyExternyVrchol.getPocetZaznamovBlocky() == 0 &&
            najdenyExternyVrchol.getPocetPreplnujucichBlockov() == 0)
        {
            spravcaSuborov.uvolniBlockHlavnySubor(najdenyExternyVrchol.getOffset(), typ);
            najdenyExternyVrchol.setOffset(-1);
        }

        if (najdenyExternyVrchol.equals(this.root) &&
            najdenyExternyVrchol.getOffset() != -1)
        {
            this.skusDealokovatKoren(typ, spravcaSuborov);
        }
        else
        {
            this.skusSpojitVrcholy(typ, najdenyExternyVrchol, spravcaSuborov);
        }

        return realneVymazany;
    }

    private<T extends IData> void skusDealokovatKoren(Class<T> typ, SpravcaSuborov spravcaSuborov)
    {
        ExternyVrchol koren = (ExternyVrchol)this.root;

        if (koren.getPocetZaznamovBlocky() == 0)
        {
            // Koren bol vyprazdneny, tym padom moze byt dealokovany
            spravcaSuborov.uvolniBlockHlavnySubor(koren.getOffset(), typ);
            this.root = new ExternyVrchol();
        }
    }

    private<T extends IData> void skusSpojitVrcholy(Class<T> typ, ExternyVrchol externyVrchol, SpravcaSuborov spravcaSuborov)
    {
        // Na zaciatku si skontrolujem, ci je vobec
        // nejake spajanie mozne vykonat
        if (!this.moznoSpojitVrcholy(externyVrchol, spravcaSuborov))
        {
            return;
        }

        // Oba Blocky si nacitam do operacnej pamati
        Block<T> prvy = new Block<>(spravcaSuborov.getBlokovaciFaktorHlavnySubor(), typ);
        if (externyVrchol.getOffset() != -1)
        {
            prvy.prevedZPolaBajtov(spravcaSuborov.citajHlavnySubor(externyVrchol.getOffset(), prvy.getVelkost()));
        }

        ExternyVrchol surodenec = (ExternyVrchol)externyVrchol.getSurodenec();
        Block<T> druhy = new Block<>(spravcaSuborov.getBlokovaciFaktorHlavnySubor(), typ);
        if (surodenec.getOffset() != -1)
        {
            druhy.prevedZPolaBajtov(spravcaSuborov.citajHlavnySubor(surodenec.getOffset(), prvy.getVelkost()));
        }

        // Zaznamy z oboch Blockov presuniem do noveho Blocku
        Block<T> spojeny = new Block<>(spravcaSuborov.getBlokovaciFaktorHlavnySubor(), typ);
        for (T zaznam : prvy.getZaznamy())
        {
            spojeny.forceVloz(zaznam);
        }

        for (T zaznam : druhy.getZaznamy())
        {
            spojeny.forceVloz(zaznam);
        }

        // Uvolnim povodne Blocky
        if (externyVrchol.getOffset() != -1)
        {
            spravcaSuborov.uvolniBlockHlavnySubor(externyVrchol.getOffset(), typ);
        }

        if (surodenec.getOffset() != -1)
        {
            spravcaSuborov.uvolniBlockHlavnySubor(surodenec.getOffset(), typ);
        }

        // Za urcitych podmienok je mozne rusit aj dalsie Vrcholy
        ExternyVrchol novyExternyVrchol = externyVrchol;
        while (true)
        {
            novyExternyVrchol = this.odpojInternyVrchol(novyExternyVrchol.getOtec());

            if (novyExternyVrchol.getStatus() == Status.KOREN)
            {
                // Dostal som sa az na koren, ako offset pouzijem
                // offset Externeho vrcholu, nakolko tento som nedealokoval
                novyExternyVrchol.vlozBlock(spojeny, typ, spravcaSuborov, -1);

                // Nie je mozne dalej pokracovat, Block, ktory vznikol
                // spajanim bol ulozeny do korena
                break;
            }

            Vrchol surodenecNoveho = novyExternyVrchol.getSurodenec();
            if (surodenecNoveho instanceof ExternyVrchol surodenecNovehoExterny &&
                surodenecNovehoExterny.getPocetZaznamovBlocky() == 0)
            {
                // V tomto pripade je mozne spajanie vykonavat aj dalej
                continue;
            }
            else
            {
                // Nie je mozne vykonat dalsie spajanie Vrcholov, ako offset pouzijem
                // offset Externeho vrcholu, nakolko tento som nedealokoval
                novyExternyVrchol.vlozBlock(spojeny, typ, spravcaSuborov, -1);
                break;
            }
        }
    }

    private ExternyVrchol odpojInternyVrchol(InternyVrchol odpajanyVrchol)
    {
        ExternyVrchol novyExternyVrchol = new ExternyVrchol();

        InternyVrchol otecOdpajanehoVrcholu = odpajanyVrchol.getOtec();
        if (otecOdpajanehoVrcholu == null)
        {
            // Odpajany Vrchol je korenom stromu
            this.root = novyExternyVrchol;
        }
        else
        {
            novyExternyVrchol.setOtec(otecOdpajanehoVrcholu);

            if (odpajanyVrchol.getStatus() == Status.LAVY_SYN)
            {
                otecOdpajanehoVrcholu.setLavySyn(novyExternyVrchol);
            }
            else
            {
                otecOdpajanehoVrcholu.setPravySyn(novyExternyVrchol);
            }
        }

        return novyExternyVrchol;
    }

    // Metoda rozhodne, ci je mozne spojit 2 Externe vrcholy
    // v strome do 1 Externeho vrcholu
    private boolean moznoSpojitVrcholy(ExternyVrchol externyVrchol, SpravcaSuborov spravcaSuborov)
    {
        Vrchol surodenec = externyVrchol.getSurodenec();
        if (surodenec == null || surodenec instanceof InternyVrchol)
        {
            // Ak surodenec je koren alebo Interny vrchol,
            // tak spajanie urcite nie je mozne vykonat
            return false;
        }

        ExternyVrchol externyVrcholSurodenec = (ExternyVrchol)surodenec;
        if (externyVrchol.getPocetPreplnujucichBlockov() != 0 ||
            externyVrcholSurodenec.getPocetPreplnujucichBlockov() != 0)
        {
            // Ak niektory z Vrcholov ma prideleny Preplnujuci block,
            // tak spajanie urcite nie je mozne vykonat
            return false;
        }

        int pocetSpolu = externyVrchol.getPocetZaznamovBlocky() + externyVrcholSurodenec.getPocetZaznamovBlocky();
        if (pocetSpolu > spravcaSuborov.getBlokovaciFaktorHlavnySubor())
        {
            // Zaznamy oboch Vrcholov sa nezmestia do jedineho Vrcholu
            return false;
        }
        else if (pocetSpolu == spravcaSuborov.getBlokovaciFaktorHlavnySubor())
        {
            // Spojenie mozno vykonat
            return true;
        }
        else
        {
            // Spajanie malo byt vykonane uz v minulosti
            throw new RuntimeException("Spajanie nebolo vykonane!");
        }
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

        int pocetZaznamovVrchol = externyVrchol.getPocetZaznamovBlocky();
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
            // Striasanie malo byt vykonane uz v minulosti
            throw new RuntimeException("Striasane nebolo vykonane!");
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
