package Hesovanie.DynamickyZnakovyStrom;

import Hesovanie.Block;
import Hesovanie.Subor;
import Ostatne.Konstanty;
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

    public<T extends IData> void vloz(T pridavany, Class<T> typ,
                                      int blokovaciFaktorHlavnySubor, int blokovaciFaktorPreplnujuciSubor,
                                      Subor hlavnySubor, Subor preplnujuciSubor)
    {
        // Traverzuj stromom, pokym sa nedostanes na Externy vrchol
        int curBitHash = 0;
        BitSet pridavanyZaznamHash = pridavany.getHash();

        InternyVrchol predchadzajuciVrchol = null;
        boolean odpojLavehoSyna = false;
        Vrchol curVrchol = this.root;

        while (curVrchol instanceof InternyVrchol internyVrchol)
        {
            int hodnotaBitu = pridavanyZaznamHash.get(curBitHash) ? 1 : 0;
            curBitHash++;

            predchadzajuciVrchol = internyVrchol;
            if (hodnotaBitu == 0)
            {
                // Vlavo
                odpojLavehoSyna = true;
                curVrchol = internyVrchol.getLavySyn();
            }
            else
            {
                // Vpravo
                odpojLavehoSyna = false;
                curVrchol = internyVrchol.getPravySyn();
            }
        }

        // Dostal som sa na Externy vrchol
        ExternyVrchol externyVrchol = (ExternyVrchol)curVrchol;
        if (externyVrchol.getPocetZaznamovBlock() < blokovaciFaktorHlavnySubor)
        {
            // V Blocku je miesto pre pridavany Zaznam
            this.vlozDoExistujuceho(externyVrchol, pridavany, typ, blokovaciFaktorHlavnySubor, hlavnySubor);
            return;
        }

        // V Blocku nie je miesto pre pridavany Zaznam
        while (true)
        {
            if (curBitHash >= Konstanty.POCET_BITOV_HASH)
            {
                // Doslo k vyuzitiu vsetkych bitov,
                // nutnost vlozit do preplnujuceho suboru
                throw new RuntimeException("Minuli sa vsetky bity!");
            }

            // Metoda odpoji najdeny najdeny Externy vrchol od zvysku stromu
            // a nahradi ho novym Internym vrcholom
            InternyVrchol novyInternyVrchol = this.odpojExternyVrchol(odpojLavehoSyna, predchadzajuciVrchol);

            // Nacitaj zaznamy z odpojeneho Externoho vrcholu
            // a presuvaj ich do novo vytvorenych Externych vrcholov
            Block<T> odpojenyVrcholBlock = externyVrchol.getBlock(typ, blokovaciFaktorHlavnySubor, hlavnySubor);
            Block<T> lavyBlock = new Block<>(blokovaciFaktorHlavnySubor, typ);
            Block<T> pravyBlock = new Block<>(blokovaciFaktorHlavnySubor, typ);

            for (T zaznam : odpojenyVrcholBlock.getZaznamy())
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
                ((ExternyVrchol)novyInternyVrchol.getLavySyn()).vlozBlock(lavyBlock, hlavnySubor, externyVrchol.getOffset());
                // Nutne poziadat o novy offset
                ((ExternyVrchol)novyInternyVrchol.getPravySyn()).vlozBlock(pravyBlock, hlavnySubor, hlavnySubor.getNovyOffset());

                break;
            }

            // Novy Zaznam nemohol byt vlozeny, cely proces je nutne opakovat
            if (lavyBlock.jeBlockPlny())
            {
                odpojLavehoSyna = true;
                ((ExternyVrchol)novyInternyVrchol.getLavySyn()).vlozBlock(lavyBlock, hlavnySubor, externyVrchol.getOffset());
            }
            else
            {
                odpojLavehoSyna = false;
                ((ExternyVrchol)novyInternyVrchol.getPravySyn()).vlozBlock(lavyBlock, hlavnySubor, externyVrchol.getOffset());
            }

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

    private<T extends IData> void vlozDoExistujuceho(ExternyVrchol externyVrchol, T pridavany, Class<T> typ, int blokovaciFaktorHlavnySubor, Subor hlavnySubor)
    {
        if (externyVrchol.getOffset() == -1)
        {
            // Externy nema pridelene miesto v subore
            long pridelenyOffset = hlavnySubor.getNovyOffset();
            externyVrchol.vloz(pridavany, typ, blokovaciFaktorHlavnySubor, hlavnySubor, pridelenyOffset);
        }
        else
        {
            externyVrchol.vloz(pridavany, typ, blokovaciFaktorHlavnySubor, hlavnySubor, -1);
        }
    }
}
