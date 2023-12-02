package Hesovanie.DigitalnyZnakovyStrom;

import Ostatne.Status;

import java.io.Serializable;

public abstract class Vrchol implements Serializable
{
    protected InternyVrchol otec;

    public Vrchol getSurodenec()
    {
        Status status = this.getStatus();

        if (status == Status.KOREN)
        {
            return null;
        }
        else if (status == Status.LAVY_SYN)
        {
            return this.otec.getPravySyn();
        }
        else
        {
            return this.otec.getLavySyn();
        }
    }

    public InternyVrchol getOtec()
    {
        return this.otec;
    }

    public void setOtec(InternyVrchol otec)
    {
        this.otec = otec;
    }

    public Status getStatus()
    {
        if (this.otec == null)
        {
            return Status.KOREN;
        }
        else if (this.otec.getLavySyn().equals(this))
        {
            return Status.LAVY_SYN;
        }
        else
        {
            return Status.PRAVY_SYN;
        }
    }
}
