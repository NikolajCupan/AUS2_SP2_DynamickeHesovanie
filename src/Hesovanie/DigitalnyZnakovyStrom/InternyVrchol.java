package Hesovanie.DigitalnyZnakovyStrom;

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
}
