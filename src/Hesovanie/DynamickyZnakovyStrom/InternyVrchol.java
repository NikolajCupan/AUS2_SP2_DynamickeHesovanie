package Hesovanie.DynamickyZnakovyStrom;

public class InternyVrchol extends Vrchol
{
    private Vrchol lavySyn;
    private Vrchol pravySyn;

    public InternyVrchol()
    {
        this.lavySyn = null;
        this.pravySyn = null;
        this.otec = null;
    }

    public void odstranSynov()
    {
        this.lavySyn = null;
        this.pravySyn = null;
    }

    public Vrchol getLavySyn()
    {
        return this.lavySyn;
    }

    public Vrchol getPravySyn()
    {
        return this.pravySyn;
    }

    public void setLavySyn(Vrchol lavySyn)
    {
        this.lavySyn = lavySyn;
    }

    public void setPravySyn(Vrchol pravySyn)
    {
        this.pravySyn = pravySyn;
    }

    @Override
    public String toString()
    {
        String string = "Interny vrchol (";

        if (this.lavySyn != null)
        {
            string += "Lavy syn: existuje,";
        }
        else
        {
            string +=  "\tLavy syn: neexistuje";
        }

        if (this.pravySyn != null)
        {
            string +=  "\tPravy syn: existuje";
        }
        else
        {
            string +=  "\tPravy syn: neexistuje";
        }

        string += ")";

        return string;
    }
}
