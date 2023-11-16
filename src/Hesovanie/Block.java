package Hesovanie;

public class Block<T extends IData> implements IRecord
{
    @Override
    public int getVelkost()
    {
        return 0;
    }

    @Override
    public byte[] prevedNaPoleBajtov()
    {
        return null;
    }

    @Override
    public void prevedZPolaBajtov(byte[] poleBajtov)
    {
    }
}
