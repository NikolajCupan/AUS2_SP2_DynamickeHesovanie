package Rozhrania;

public interface IRecord
{
    int getVelkost();
    byte[] prevedNaPoleBajtov();
    void prevedZPolaBajtov(byte[] poleBajtov);
}
