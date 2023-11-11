package Objekty;

import Ostatne.Konstanty;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Parcela extends Polygon
{
    private static final int MAX_POCET_REFERENCII = 5;

    // Unikatny kluc
    private int parcelaID;

    // Ostatne atributy
    private String popis;

    // Zoznam ID nehnutelnosti, ktore lezia na danej parcele
    private final ArrayList<Integer> nehnutelnostiID;

    public Parcela(int parcelaID, String popis, Suradnica suradnica1, Suradnica suradnica2)
    {
        this.nastavSuradnice(suradnica1, suradnica2);

        this.parcelaID = parcelaID;
        this.popis = popis;
        this.nehnutelnostiID = new ArrayList<>();
    }

    // Metoda sa pokusi pridat ID nehnutelnosti do zoznamu ID nehnutelnosti, ktore lezia na parcele,
    // ak pridanie zlyha (na parcele nelezi dana nehnutelnost), vyhodi sa vynimka, v pripade
    // ak bol dosiahnuty maximalny pocet referencii, nova referencia nie je pridana
    public void skusPridatNehnutelnost(Nehnutelnost nehnutelnost)
    {
        if (!this.prekryva(nehnutelnost))
        {
            throw new RuntimeException("Na parcele nelezi dana nehnutelnost!");
        }

        if (this.nehnutelnostiID.size() < MAX_POCET_REFERENCII)
        {
            // Pridaj iba v pripade ak nie je presiahnuty limit
            this.nehnutelnostiID.add(nehnutelnost.getNehnutelnostID());
        }
    }

    // Metoda sa pokusi odobrat ID nehnutelnosti zo zoznamu nehnutelnosti, ktore lezia na parcele
    // ak odobratie zlyha (na parcele nelezi dana nehnutelnost), vyhodi sa vynimka
    public void skusOdobratNehnutelnost(Nehnutelnost nehnutelnost)
    {
        if (!this.prekryva(nehnutelnost))
        {
            throw new RuntimeException("Na parcele nelezi dana nehnutelnost!");
        }

        this.nehnutelnostiID.remove(nehnutelnost.getNehnutelnostID());
    }

    public int getParcelaID()
    {
        return this.parcelaID;
    }

    public String getPopis()
    {
        return popis;
    }

    public ArrayList<Integer> getNehnutelnostiID()
    {
        return this.nehnutelnostiID;
    }

    public void setPopis(String popis)
    {
        this.popis = popis.substring(0, Math.min(popis.length(), Konstanty.MAX_DLZKA_POPIS_PARCELA));
    }

    @Override
    public String toString()
    {
        DecimalFormat formatovac = new DecimalFormat("#.##");
        return "Nehnuteľnost (ID " + this.parcelaID + "): " +
                " {" + formatovac.format(this.getVlavoDoleX()) + ", " + formatovac.format(this.getVlavoDoleY()) +
                "}, {" + formatovac.format(this.getVpravoHoreX()) + ", " + formatovac.format(this.getVpravoHoreY()) + "}";
    }

    @Override
    public boolean equals(Object object)
    {
        final double epsilon = 0.00001;

        if (!(object instanceof Parcela parcela))
        {
            return false;
        }

        if (parcela.getParcelaID() == this.parcelaID &&
            Math.abs(this.getVlavoDoleX() - parcela.getVlavoDoleX()) < epsilon &&
            Math.abs(this.getVlavoDoleY() - parcela.getVlavoDoleY()) < epsilon &&
            Math.abs(this.getVpravoHoreX() - parcela.getVpravoHoreX()) < epsilon &&
            Math.abs(this.getVpravoHoreY() - parcela.getVpravoHoreY()) < epsilon)
        {
            return true;
        }

        return false;
    }
}
