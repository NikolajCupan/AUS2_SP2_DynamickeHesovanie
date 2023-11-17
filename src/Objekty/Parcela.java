package Objekty;

import Hesovanie.IRecord;
import Ostatne.Helper;
import Ostatne.Konstanty;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.BitSet;

public class Parcela extends Polygon
{
    private static final int MAX_POCET_REFERENCII = 5;

    // Unikatny kluc
    private int parcelaID;

    // Ostatne atributy
    private String popis;

    // Zoznam ID nehnutelnosti, ktore lezia na danej parcele
    private ArrayList<Integer> nehnutelnostiID;

    public Parcela(int parcelaID, String popis, Suradnica suradnica1, Suradnica suradnica2)
    {
        this.nastavSuradnice(suradnica1, suradnica2);

        this.parcelaID = parcelaID;
        this.setPopis(popis);
        this.nehnutelnostiID = new ArrayList<>();
    }

    // Pre ucely inicializacie z pola bajtov
    public Parcela() {}

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
    public boolean jeRovnaky(IRecord zaznam)
    {
        if (!(zaznam instanceof Parcela parcela))
        {
            return false;
        }

        return this.parcelaID == parcela.getParcelaID();
    }

    @Override
    public BitSet getHash()
    {
        return null;
    }

    @Override
    public int getVelkost()
    {
        int velkost = 0;

        // ID parcely - int
        velkost += Konstanty.VELKOST_INT;

        // Bajt pre dlzku popisu
        velkost += Konstanty.VELKOST_BAJT;

        // Samotny String - 1 bajt pre kazdy znak v Stringu
        velkost += Konstanty.VELKOST_BAJT * Konstanty.MAX_DLZKA_POPIS_PARCELA;

        // 2 suradnice, pricom kazda ma 2 double hodnoty
        velkost += 4 * Konstanty.VELKOST_DOUBLE;

        // Bajt pre dlzku zoznamu referencii na nehnutelnosti
        velkost += Konstanty.VELKOST_BAJT;

        // Samotne ID nehnutelnosti - int
        velkost += MAX_POCET_REFERENCII * Konstanty.VELKOST_INT;

        return velkost;
    }

    @Override
    public byte[] prevedNaPoleBajtov()
    {
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteOutputStream);

        try
        {
            dataOutputStream.writeInt(this.parcelaID);

            dataOutputStream.writeByte(this.popis.length());
            String rozsirenyPopis = Helper.rozsirString(this.popis, Konstanty.MAX_DLZKA_POPIS_PARCELA);
            dataOutputStream.writeBytes(rozsirenyPopis);

            dataOutputStream.write(this.surVlavoDole.prevedNaPoleBajtov());
            dataOutputStream.write(this.surVpravoHore.prevedNaPoleBajtov());

            dataOutputStream.writeByte(this.nehnutelnostiID.size());
            for (Integer nehnutelnostID : this.nehnutelnostiID)
            {
                dataOutputStream.writeInt(nehnutelnostID);
            }

            for (int i = 0; i < MAX_POCET_REFERENCII - this.nehnutelnostiID.size(); i++)
            {
                dataOutputStream.writeInt(0);
            }

            return byteOutputStream.toByteArray();
        }
        catch (Exception ex)
        {
            throw new IllegalStateException("Konverzia Parcely na pole bajtov sa nepodarila!");
        }
    }

    @Override
    public void prevedZPolaBajtov(byte[] poleBajtov)
    {
        if (this.getVelkost() != poleBajtov.length)
        {
            throw new RuntimeException("Dlzka pola bajtov sa nezhoduje s velkostou Parcely!");
        }

        ByteArrayInputStream byteInputStream = new ByteArrayInputStream(poleBajtov);
        DataInputStream dataInputStream = new DataInputStream(byteInputStream);

        try
        {
            this.parcelaID = dataInputStream.readInt();

            byte dlzkaPopisu = dataInputStream.readByte();
            this.popis = Helper.nacitajString(dataInputStream, dlzkaPopisu, Konstanty.MAX_DLZKA_POPIS_PARCELA);

            Suradnica suradnica1 = new Suradnica(dataInputStream.readDouble(), dataInputStream.readDouble());
            Suradnica suradnica2 = new Suradnica(dataInputStream.readDouble(), dataInputStream.readDouble());
            this.nastavSuradnice(suradnica1, suradnica2);

            this.nehnutelnostiID = new ArrayList<>();
            byte pocetReferencii = dataInputStream.readByte();

            for (int i = 0; i < pocetReferencii; i++)
            {
                this.nehnutelnostiID.add(dataInputStream.readInt());
            }
        }
        catch (Exception ex)
        {
            throw new IllegalStateException("Konverzia pola bajtov na Parcelu sa nepodarila!");
        }
    }

    @Override
    public boolean equals(Object object)
    {
        if (!(object instanceof Parcela parcela))
        {
            return false;
        }

        final double epsilon = 0.00001;
        if (Math.abs(this.getVlavoDoleX() - parcela.getVlavoDoleX()) < epsilon &&
            Math.abs(this.getVlavoDoleY() - parcela.getVlavoDoleY()) < epsilon &&
            Math.abs(this.getVpravoHoreX() - parcela.getVpravoHoreX()) < epsilon &&
            Math.abs(this.getVpravoHoreY() - parcela.getVpravoHoreY()) < epsilon &&
            this.parcelaID == parcela.getParcelaID() &&
            this.popis.equals(parcela.getPopis()))
        {
            if (this.nehnutelnostiID.size() != parcela.getNehnutelnostiID().size())
            {
                return false;
            }

            for (int i = 0; i < this.nehnutelnostiID.size(); i++)
            {
                if (!this.nehnutelnostiID.get(i).equals(parcela.getNehnutelnostiID().get(i)))
                {
                    return false;
                }
            }

            return true;
        }

        return false;
    }
}
