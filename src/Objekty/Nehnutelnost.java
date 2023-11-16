package Objekty;

import Hesovanie.IRecord;
import Ostatne.Konstanty;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.BitSet;

public class Nehnutelnost extends Polygon
{
    private static final int MAX_POCET_REFERENCII = 6;

    // Unikatny kluc
    private final int nehnutelnostID;

    // Ostatne atributy
    private int supisneCislo;
    private String popis;

    // Zoznam ID parciel, na ktorych lezi dana nehnutelnost
    private final ArrayList<Integer> parcelyID;

    public Nehnutelnost(int nehnutelnostID, int supisneCislo, String popis, Suradnica suradnica1, Suradnica suradnica2)
    {
        this.nastavSuradnice(suradnica1, suradnica2);

        this.nehnutelnostID = nehnutelnostID;
        this.supisneCislo = supisneCislo;
        this.popis = popis;
        this.parcelyID = new ArrayList<>();
    }

    // Metoda sa pokusi pridat ID parcely do zoznamu ID parciel, na ktorych lezi nehnutelnost,
    // ak pridanie zlyha (nehnutelnost nelezi na danej parcele), vyhodi sa vynimka, v pripade
    // ak bol dosiahnuty maximalny pocet referencii, nova referencia nie je pridana
    public void skusPridatParcelu(Parcela parcela)
    {
        if (!this.prekryva(parcela))
        {
            throw new RuntimeException("Nehnutelnost nelezi na danej parcele!");
        }

        if (this.parcelyID.size() < MAX_POCET_REFERENCII)
        {
            // Pridaj iba v pripade ak nie je presiahnuty limit
            this.parcelyID.add(parcela.getParcelaID());
        }
    }

    // Metoda sa pokusi odobrat ID parcely zo zoznamu ID parciel, na ktorych lezi nehnutelnost,
    // ak odobratie zlyha (nehnutelnost nelezi na danej parcele), vyhodi sa vynimka
    public void skusOdobratParcelu(Parcela parcela)
    {
        if (!this.prekryva(parcela))
        {
            throw new RuntimeException("Nehnutelnost nelezi na danej parcele!");
        }

        this.parcelyID.remove(parcela.getParcelaID());
    }

    public int getNehnutelnostID()
    {
        return this.nehnutelnostID;
    }

    public int getSupisneCislo()
    {
        return this.supisneCislo;
    }

    public String getPopis()
    {
        return popis;
    }

    public ArrayList<Integer> getParcelyID()
    {
        return this.parcelyID;
    }

    public void setSupisneCislo(int supisneCislo)
    {
        this.supisneCislo = supisneCislo;
    }

    public void setPopis(String popis)
    {
        this.popis = popis.substring(0, Math.min(popis.length(), Konstanty.MAX_DLZKA_POPIS_NEHNUTELNOST));
    }

    @Override
    public String toString()
    {
        DecimalFormat formatovac = new DecimalFormat("#.##");
        return "Nehnuteľnost (ID " + this.nehnutelnostID + "): " + this.supisneCislo +
                " {" + formatovac.format(this.getVlavoDoleX()) + ", " + formatovac.format(this.getVlavoDoleY()) +
                "}, {" + formatovac.format(this.getVpravoHoreX()) + ", " + formatovac.format(this.getVpravoHoreY()) + "}";
    }

    @Override
    public boolean equals(Object object)
    {
        return false;
    }

    @Override
    public boolean jeRovnaky(IRecord zaznam)
    {
        return false;
    }

    @Override
    public BitSet getHash()
    {
        return null;
    }

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
