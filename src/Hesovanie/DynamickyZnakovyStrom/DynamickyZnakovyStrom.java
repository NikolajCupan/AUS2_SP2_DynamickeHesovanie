package Hesovanie.DynamickyZnakovyStrom;

import Hesovanie.Block;
import Hesovanie.SpravcaSuborov;
import Rozhrania.IData;

import java.util.BitSet;
import java.util.Stack;

public class DynamickyZnakovyStrom
{
    private Vrchol root;

    public DynamickyZnakovyStrom()
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
        InternyVrchol[] predchadzajuciVrchol = new InternyVrchol[]{ null };
        int[] curBitHash = new int[]{ 0 };
        boolean[] odpojLavehoSyna = new boolean[]{ false };

        ExternyVrchol externyVrchol = this.traverzujNaExternyVrchol(zaciatocnyVrchol, predchadzajuciVrchol, pridavanyZaznamHash, curBitHash, odpojLavehoSyna);
        this.vlozZaznamDoVrcholu(externyVrchol, predchadzajuciVrchol[0], pridavany, typ, spravcaSuborov, pridavanyZaznamHash, curBitHash[0], odpojLavehoSyna[0]);
    }

    private ExternyVrchol traverzujNaExternyVrchol(Vrchol zaciatocnyVrchol, InternyVrchol[] predchadzajuciVrchol, BitSet zaznamHash,
                                                   int[] curBitHash, boolean[] odpojLavehoSyna)
    {
        Vrchol curVrchol = zaciatocnyVrchol;

        while (curVrchol instanceof InternyVrchol internyVrchol)
        {
            int hodnotaBitu = zaznamHash.get(curBitHash[0]) ? 1 : 0;
            curBitHash[0]++;

            predchadzajuciVrchol[0] = internyVrchol;
            if (hodnotaBitu == 0)
            {
                // Vlavo
                odpojLavehoSyna[0] = true;
                curVrchol = internyVrchol.getLavySyn();
            }
            else
            {
                // Vpravo
                odpojLavehoSyna[0] = false;
                curVrchol = internyVrchol.getPravySyn();
            }
        }

        // Dostal som sa na Externy vrchol
        return (ExternyVrchol)curVrchol;
    }

    private<T extends IData> void vlozZaznamDoVrcholu(ExternyVrchol externyVrchol, InternyVrchol predchadzajuciVrchol, T pridavany, Class<T> typ,
                                                      SpravcaSuborov spravcaSuborov, BitSet pridavanyZaznamHash,
                                                      int curBitHash, boolean odpojLavehoSyna)
    {
        Block<T> presuvanyBlock = null;

        while (true)
        {
            if (externyVrchol.getPocetZaznamovBlock() < spravcaSuborov.getBlokovaciFaktorHlavnySubor() ||
                curBitHash >= pridavany.getPocetBitovHash())
            {
                // V Blocku je miesto pre pridavany Zaznam alebo boli pouzite vsetky bity hashu
                externyVrchol.vloz(pridavany, typ, spravcaSuborov);
                break;
            }

            // Metoda odpoji najdeny najdeny Externy vrchol od zvysku stromu
            // a nahradi ho novym Internym vrcholom
            InternyVrchol novyInternyVrchol = this.odpojExternyVrchol(odpojLavehoSyna, predchadzajuciVrchol);

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
                    lavyBlock.vloz(zaznam);
                }
                else
                {
                    // Vpravo
                    pravyBlock.vloz(zaznam);
                }
            }

            boolean novyPridavanyVlozeny = false;
            int hodnotaBituPridavany = pridavanyZaznamHash.get(curBitHash) ? 1 : 0;
            if (hodnotaBituPridavany == 0)
            {
                // Vlavo
                if (!lavyBlock.jeBlockPlny())
                {
                    lavyBlock.vloz(pridavany);
                    novyPridavanyVlozeny = true;
                }
            }
            else
            {
                // Vpravo
                if (!pravyBlock.jeBlockPlny())
                {
                    pravyBlock.vloz(pridavany);
                    novyPridavanyVlozeny = true;
                }
            }

            if (novyPridavanyVlozeny)
            {
                // Novy Zaznam bol uspesne vlozeny do stromu

                // Pouzi offset, ktory mal odpojeny Block
                ((ExternyVrchol)novyInternyVrchol.getLavySyn()).vlozBlock(lavyBlock, spravcaSuborov, externyVrchol.getOffset());
                // Nutne poziadat o novy offset
                ((ExternyVrchol)novyInternyVrchol.getPravySyn()).vlozBlock(pravyBlock, spravcaSuborov, -1);

                break;
            }

            // Novy Zaznam nemohol byt vlozeny, cely proces je nutne opakovat
            presuvanyBlock = lavyBlock.jeBlockPlny() ? lavyBlock : pravyBlock;
            odpojLavehoSyna = lavyBlock.jeBlockPlny();

            predchadzajuciVrchol = novyInternyVrchol;

            curBitHash++;
        }
    }

    private InternyVrchol odpojExternyVrchol(boolean odpojLavehoSyna, InternyVrchol predchadzajuciVrchol)
    {
        // Vytvor na mieste odpajaneho vrcholu novy Interny vrchol
        // a nastav referenciu v predchadzajucom vrchole
        InternyVrchol novyInternyVrchol = new InternyVrchol();

        if (predchadzajuciVrchol == null)
        {
            // Odpajam koren
            this.root = novyInternyVrchol;
        }
        else
        {
            if (odpojLavehoSyna)
            {
                predchadzajuciVrchol.setLavySyn(novyInternyVrchol);
            }
            else
            {
                predchadzajuciVrchol.setPravySyn(novyInternyVrchol);
            }
        }

        novyInternyVrchol.setLavySyn(new ExternyVrchol());
        novyInternyVrchol.setPravySyn(new ExternyVrchol());

        return novyInternyVrchol;
    }

    public<T extends IData> T vyhladaj(T vyhladavany, Class<T> typ, SpravcaSuborov spravcaSuborov)
    {
        // Prechadzaj stromom az pokym sa nedostanes na Externy vrchol
        BitSet vyhladavanyZaznamHash = vyhladavany.getHash();
        ExternyVrchol najdenyExternyVrchol = this.traverzujNaExternyVrchol(this.root, new InternyVrchol[]{ null }, vyhladavanyZaznamHash, new int[]{ 0 }, new boolean[]{ false });

        // Nacitaj Block a prehladaj ho
        Block<T> najdenyBlock = najdenyExternyVrchol.getBlock(typ, spravcaSuborov);
        return najdenyBlock.vyhladaj(vyhladavany);
    }
}
