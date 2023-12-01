package Rozhrania;

public interface IPolygon
{
    boolean leziVnutri(double x, double y);
    boolean leziVnutri(IPolygon vnutorny);
    boolean prekryva(IPolygon polygon);
    double getVlavoDoleX();
    double getVlavoDoleY();
    double getVpravoHoreX();
    double getVpravoHoreY();
    boolean jeRovnakyPolygon(IPolygon polygon);
}
