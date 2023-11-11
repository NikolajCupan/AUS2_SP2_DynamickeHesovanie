package Objekty;

import Ostatne.Konstanty;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Nehnutelnost extends Polygon
{
    private static final int MAX_POCET_REFERENCII = 6;

    // Unikatny kluc
    private int nehnutelnostID;

    // Ostatne atributy
    private int supisneCislo;
    private String popis;

    // Zoznam ID parciel, na ktorych lezi dana nehnutelnost
    private final ArrayList<Integer> parcelyID;

    public Nehnutelnost(int nehnutelnostID, int supisneCislo, String popis, Suradnica suradnica1, Suradnica suradnica2)
    {
        this.nastavSuradnice(suradnica1, suradnica2);

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
        final double epsilon = 0.00001;

        if (!(object instanceof Nehnutelnost nehnutelnost))
        {
            return false;
        }

        if (nehnutelnost.getNehnutelnostID() == this.nehnutelnostID &&
            nehnutelnost.getSupisneCislo() == this.supisneCislo &&
            Math.abs(this.getVlavoDoleX() - nehnutelnost.getVlavoDoleX()) < epsilon &&
            Math.abs(this.getVlavoDoleY() - nehnutelnost.getVlavoDoleY()) < epsilon &&
            Math.abs(this.getVpravoHoreX() - nehnutelnost.getVpravoHoreX()) < epsilon &&
            Math.abs(this.getVpravoHoreY() - nehnutelnost.getVpravoHoreY()) < epsilon)
        {
            return true;
        }

        return false;
    }
}
