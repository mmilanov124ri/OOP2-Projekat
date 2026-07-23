package Modeli;

public class Aerodrom {
    private String kod;
    private String ime;
    private int x;
    private int y;

    public Aerodrom(String kod, String ime, int x, int y) {
        this.kod = kod;
        this.ime = ime;
        this.x = x;
        this.y = y;
    }








    public String getKod() {
        return kod;
    }

    public void setKod(String kod) {
        this.kod = kod;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
