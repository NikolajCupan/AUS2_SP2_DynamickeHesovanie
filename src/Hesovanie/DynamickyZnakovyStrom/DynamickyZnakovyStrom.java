package Hesovanie.DynamickyZnakovyStrom;

import Hesovanie.Block;
import Hesovanie.Subor;
import Ostatne.Konstanty;
import Rozhrania.IData;

import java.util.BitSet;

public class DynamickyZnakovyStrom
{
    private Vrchol root;

    public DynamickyZnakovyStrom()
    {
        this.root = new ExternyVrchol();
    }

    public<T extends IData> void vloz(T pridavany,
                                      int blokovaciFaktorHlavnySubor, int blokovaciFaktorPreplnujuciSubor,
                                      Subor hlavnySubor, Subor preplnujuciSubor)
    {
        // Traverzuj stromom, pokym sa nedostanes na Externy vrchol
        BitSet pridavanyZaznamHash = pridavany.getHash();
        int curBit = 0;

        InternyVrchol predchadzajuciVrchol = null;
        boolean jeNajdenyExternyVrcholLavySyn = false;
        Vrchol curVrchol = this.root;

        while (curVrchol instanceof InternyVrchol internyVrchol)
        {
            int hodnotaBitu = pridavanyZaznamHash.get(curBit) ? 1 : 0;
            curBit++;

            predchadzajuciVrchol = internyVrchol;
            if (hodnotaBitu == 0)
            {
                // Vlavo
                jeNajdenyExternyVrcholLavySyn = true;
                curVrchol = internyVrchol.getLavySyn();
            }
            else
            {
                // Vpravo
                jeNajdenyExternyVrcholLavySyn = false;
                curVrchol = internyVrchol.getPravySyn();
            }
        }

        if (curBit == (Konstanty.POCET_BITOV_HASH - 1))
        {
            // Doslo k vyuzitiu vsetkych bitov,
            // nutnost vlozit do preplnujuceho suboru
            throw new RuntimeException("Minuli sa vsetky bity!");
        }

        // Dostal som sa na Externy vrchol
        ExternyVrchol externyVrchol = (ExternyVrchol)curVrchol;
        if (externyVrchol.getPocetZaznamovBlock() < blokovaciFaktorHlavnySubor)
        {
            // V Blocku je miesto pre pridavany Zaznam
            externyVrchol.vloz(pridavany, blokovaciFaktorHlavnySubor, hlavnySubor);
            return;
        }

        // V Blocku nie je miesto pre pridavany Zaznam,
        // metoda odpojila Rxterny vrchol od zvysku stromu
        InternyVrchol novyInternyVrchol = this.rozdelExternyVrchol(jeNajdenyExternyVrcholLavySyn, predchadzajuciVrchol);

        // Nacitaj zaznamy v odpojenom Externom vrchole
        // a presuvaj ich do novo vytvorenych Externych vrcholov
        Block<T> externyVrcholBlock = externyVrchol.getBlock(pridavany, blokovaciFaktorHlavnySubor, hlavnySubor);
        Block<T> lavyBlock = new Block<>(blokovaciFaktorHlavnySubor, pridavany);
        Block<T> pravyBlock = new Block<>(blokovaciFaktorHlavnySubor, pridavany);

        for (T zaznam : externyVrcholBlock.getZaznamy())
        {
            BitSet presuvanyZaznamHash = zaznam.getHash();
            int hodnotaBitu = presuvanyZaznamHash.get(curBit) ? 1 : 0;

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

        if (!lavyBlock.jeBlockPlny() && !pravyBlock.jeBlockPlny())
        {
            // V novo vytvorenych Blockov sa nachadza
            // miesto pre pridavany Zaznam
            int hodnotaBitu = pridavanyZaznamHash.get(curBit) ? 1 : 0;

            if (hodnotaBitu == 0)
            {
                // Vlavo
                lavyBlock.vloz(pridavany);
            }
            else
            {
                // Vpravo
                pravyBlock.vloz(pridavany);
            }

            ((ExternyVrchol)novyInternyVrchol.getLavySyn()).vlozBlock(lavyBlock, hlavnySubor, externyVrchol.getOffset());
            ((ExternyVrchol)novyInternyVrchol.getPravySyn()).vlozBlock(pravyBlock, hlavnySubor, hlavnySubor.getVelkostSuboru());
        }
    }

    private InternyVrchol rozdelExternyVrchol(boolean jeNajdenyExternyVrcholLavySyn, InternyVrchol predchadzajuciVrchol)
    {
        // Vytvor na mieste rozdelovaneho vrcholu interny vrchol
        // a nastav referenciu v predchadzajucom vrchole
        InternyVrchol novyInternyVrchol = new InternyVrchol();

        if (predchadzajuciVrchol == null)
        {
            // Rozdelujem koren
            this.root = novyInternyVrchol;
        }
        else
        {
            if (jeNajdenyExternyVrcholLavySyn)
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
}
