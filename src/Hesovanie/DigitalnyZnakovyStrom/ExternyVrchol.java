package Hesovanie.DigitalnyZnakovyStrom;

import Hesovanie.Block;
import Hesovanie.SpravcaSuborov;
import Rozhrania.IData;

import java.util.ArrayList;

public class ExternyVrchol extends Vrchol
{
    // Udava, na ktorom bajte zacina dany Block
    private long offset;

    // Vratane Zaznamov v Preplnujucich blockoch
    private int pocetZaznamovBlock;
    private int pocetPreplnujucichBlockov;

    public ExternyVrchol()
    {
        this.offset = -1;
        this.pocetZaznamovBlock = 0;
        this.pocetPreplnujucichBlockov = 0;

        this.otec = null;
    }

    // Vloz na dany offset cely Block
    public<T extends IData> void vlozBlock(Block<T> pridavanyBlock, Class<T> typ, SpravcaSuborov spravcaSuborov, long forcedOffset)
    {
        if (forcedOffset != -1)
        {
            this.offset = forcedOffset;
        }
        else
        {
            this.offset = (this.offset == -1) ? spravcaSuborov.dajVolnyBlockHlavnySubor(typ) : this.offset;
        }

        this.pocetZaznamovBlock = pridavanyBlock.getPocetPlatnychZaznamov();

        try
        {
            spravcaSuborov.ulozHlavnySubor(this.offset, pridavanyBlock.prevedNaPoleBajtov());
        }
        catch (Exception ex)
        {
            throw new RuntimeException("Chyba pri vkladani Blocku do Externeho vrcholu!");
        }
    }

    public<T extends IData> void vloz(T pridavany, Class<T> typ, SpravcaSuborov spravcaSuborov)
    {
        boolean blockMalOffset = (this.offset != -1);
        this.offset = (this.offset == -1) ? spravcaSuborov.dajVolnyBlockHlavnySubor(typ) : this.offset;

        try
        {
            // Nacitanie Blocku do operacnej pamati
            Block<T> block = new Block<>(spravcaSuborov.getBlokovaciFaktorHlavnySubor(), typ);

            if (blockMalOffset)
            {
                // Citaj data z daneho offsetu iba ak sa tam nachadzaju realne data daneho Blocku,
                // ak bol prideleny novy offset, tak data necitaj
                byte[] poleBajtovSubor = spravcaSuborov.citajHlavnySubor(this.offset, block.getVelkost());
                block.prevedZPolaBajtov(poleBajtovSubor);
            }

            // Aktualizovanie Blocku a ulozenie do Suboru
            boolean vytvorenyNovyPreplnujuciBlock = block.vloz(pridavany, spravcaSuborov);
            if (vytvorenyNovyPreplnujuciBlock)
            {
                this.pocetPreplnujucichBlockov++;
            }

            spravcaSuborov.ulozHlavnySubor(this.offset, block.prevedNaPoleBajtov());

            this.pocetZaznamovBlock++;
        }
        catch (Exception ex)
        {
            throw new RuntimeException("Chyba pri vkladani do Externeho vrcholu!");
        }
    }

    public<T extends IData> T vymaz(T vymazavany, Class<T> typ, SpravcaSuborov spravcaSuborov)
    {
        if (this.offset == -1)
        {
            // Externy vrchol nema prideleny ziadny Block,
            // urcite nebude mozne vykonat mazanie
            return null;
        }

        Block<T> block = new Block<>(spravcaSuborov.getBlokovaciFaktorHlavnySubor(), typ);
        block.prevedZPolaBajtov(spravcaSuborov.citajHlavnySubor(this.offset, block.getVelkost()));

        // Alokovane pomocou new, aby sa dalo pouzit ako out parameter
        boolean[] dealokovanyPreplnujuciBlock = new boolean[]{ false };
        T realneVymazany = block.vymaz(vymazavany, spravcaSuborov, dealokovanyPreplnujuciBlock);

        if (realneVymazany != null)
        {
            // Vymazanie bolo uspesne
            this.pocetZaznamovBlock--;
            spravcaSuborov.ulozHlavnySubor(this.offset, block.prevedNaPoleBajtov());

            if (dealokovanyPreplnujuciBlock[0])
            {
                // Doslo k dealokovaniu Preplnujuceho blocku
                this.pocetPreplnujucichBlockov--;
            }

            return realneVymazany;
        }

        return null;
    }

    public<T extends IData> void strasVrchol(Class<T> typ, SpravcaSuborov spravcaSuborov)
    {
        Block<T> hlavnyBlock = new Block<>(spravcaSuborov.getBlokovaciFaktorHlavnySubor(), typ);
        hlavnyBlock.prevedZPolaBajtov(spravcaSuborov.citajHlavnySubor(this.offset, hlavnyBlock.getVelkost()));

        Block<T> curBlock = hlavnyBlock;
        ArrayList<Block<T>> preplnujuceBlocky = new ArrayList<>();

        while (curBlock.getOffsetPreplnujuciSubor() != -1)
        {
            Block<T> preplnujuciBlock = new Block<>(spravcaSuborov.getBlokovaciFaktorPreplnujuciSubor(), typ);
            preplnujuciBlock.prevedZPolaBajtov(spravcaSuborov.citajPreplnujuciSubor(curBlock.getOffsetPreplnujuciSubor(), preplnujuciBlock.getVelkost()));

            preplnujuceBlocky.add(preplnujuciBlock);
            curBlock = preplnujuciBlock;
        }

        if (preplnujuceBlocky.size() == 1)
        {
            // Existovat bude iba Hlavny block
            Block<T> preplnujuciBlock = preplnujuceBlocky.get(0);
            for (int i = 0; i < preplnujuciBlock.getPocetPlatnychZaznamov(); i++)
            {
                hlavnyBlock.forceVloz(preplnujuciBlock.getZaznamy().get(i));
            }

            spravcaSuborov.uvolniBlockPreplnujuciSubor(hlavnyBlock.getOffsetPreplnujuciSubor(), typ);
            hlavnyBlock.setOffsetPreplnujuciSubor(-1);
            spravcaSuborov.ulozHlavnySubor(this.offset, hlavnyBlock.prevedNaPoleBajtov());
        }
        else
        {
            // Presuvat budem zaznamy z posledneho Preplnujuceho blocku
            Block<T> poslednyBlock = preplnujuceBlocky.get(preplnujuceBlocky.size() - 1);
            ArrayList<T> presuvaneZaznamy = poslednyBlock.getZaznamy();

            // Predposledny block sa stane poslednym blockom
            Block<T> predposlednyBlock = preplnujuceBlocky.get(preplnujuceBlocky.size() - 2);

            // Uvolnim posledny block
            spravcaSuborov.uvolniBlockPreplnujuciSubor(predposlednyBlock.getOffsetPreplnujuciSubor(), typ);

            predposlednyBlock.setOffsetPreplnujuciSubor(-1);
            preplnujuceBlocky.remove(preplnujuceBlocky.size() - 1);

            // Postupne budem presuvat Zaznamy z arraylistu do jednotlivych Blockov
            ArrayList<Block<T>> vsetkyBlocky = preplnujuceBlocky;
            vsetkyBlocky.add(0, hlavnyBlock);

            int zaznamIndex = 0;
            for (int i = 0; i < vsetkyBlocky.size(); i++)
            {
                boolean spracuvanyModifikovany = false;
                Block<T> spracuvanyBlock = vsetkyBlocky.get(i);

                while (true)
                {
                    if (zaznamIndex >= presuvaneZaznamy.size())
                    {
                        // Vsetky zaznamy boli spracovane
                        break;
                    }

                    T zaznam = presuvaneZaznamy.get(zaznamIndex);
                    boolean uspesneVlozene = spracuvanyBlock.skusVlozit(zaznam);

                    if (uspesneVlozene)
                    {
                        spracuvanyModifikovany = true;
                        zaznamIndex++;
                    }
                    else
                    {
                        // Block je plny
                        break;
                    }
                }

                if (spracuvanyModifikovany || i == vsetkyBlocky.size() - 1)
                {
                    // Nutne dany Block ulozit, predposledny Block je nutne ulozit vzdy
                    if (i == 0)
                    {
                        // Doslo k modifikacii Hlavneho blocku
                        spravcaSuborov.ulozHlavnySubor(this.offset, spracuvanyBlock.prevedNaPoleBajtov());
                    }
                    else
                    {
                        // Doslo k modifikacii Preplnujuceho blocku
                        long offsetUkladanehoBlocku = vsetkyBlocky.get(i - 1).getOffsetPreplnujuciSubor();
                        spravcaSuborov.ulozPreplnujuciSubor(offsetUkladanehoBlocku, spracuvanyBlock.prevedNaPoleBajtov());
                    }
                }
            }

            if (zaznamIndex < presuvaneZaznamy.size())
            {
                throw new RuntimeException("Presuvanie Zaznamov pri striasani zlyhalo!");
            }
        }

        // Pri striasani vzdy dojde k uvolneniu prave 1 Preplnujuceho blocku
        this.pocetPreplnujucichBlockov--;
    }

    // Vrati cely Block
    public<T extends IData> Block<T> getBlock(Class<T> typ, SpravcaSuborov spravcaSuborov)
    {
        if (this.offset == -1)
        {
            return null;
        }

        try
        {
            // Nacitaj Block zo Suboru a vrat ho
            Block<T> block = new Block<>(spravcaSuborov.getBlokovaciFaktorHlavnySubor(), typ);

            byte[] poleBajtovSubor = spravcaSuborov.citajHlavnySubor(this.offset, block.getVelkost());
            block.prevedZPolaBajtov(poleBajtovSubor);

            return block;
        }
        catch (Exception ex)
        {
            throw new RuntimeException("Chyba pri nacitani Blocku z Externeho vrcholu!");
        }
    }

    public long getOffset()
    {
        return this.offset;
    }

    public int getPocetZaznamovBlock()
    {
        return this.pocetZaznamovBlock;
    }

    public int getPocetPreplnujucichBlockov()
    {
        return this.pocetPreplnujucichBlockov;
    }

    public void setOffset(long offset)
    {
        this.offset = offset;
    }

    public void setPocetZaznamovBlock(int pocetZaznamovBlock)
    {
        this.pocetZaznamovBlock = pocetZaznamovBlock;
    }

    public<T extends IData> String naString(SpravcaSuborov spravcaSuborov, Class<T> typ)
    {
        String string = "Externy vrchol:\n";
        string += "- offset: " + this.offset + "\n";
        string += "- pocet zaznamov block: " + this.pocetZaznamovBlock + "\n";
        string += "- pocet preplnujucich blockov: " + this.pocetPreplnujucichBlockov + "\n";

        if (this.offset != -1)
        {
            Block<T> block = new Block<>(spravcaSuborov.getBlokovaciFaktorHlavnySubor(), typ);
            block.prevedZPolaBajtov(spravcaSuborov.citajHlavnySubor(this.offset, block.getVelkost()));
            for (T zaznam : block.getZaznamy())
            {
                string += zaznam;
            }
        }

        return string;
    }
}
