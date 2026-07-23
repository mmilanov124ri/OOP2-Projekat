package Modeli;

public class Aerodrom {
    private final String kod;
    private String ime;
    private int x;
    private int y;

    public Aerodrom(String kod, String ime, int x, int y) {
        if(kod == null || !kod.matches("[A-Z]{3}")) {
            throw new IllegalArgumentException("Kod aerodroma nije validan");
        }

        if(ime == null || ime.isEmpty()) {
            throw new IllegalArgumentException("Ime aerodroma je prazno");
        }

        if(x < -180 || y < -90 || x > 180 || y > 90) {
            throw new IllegalArgumentException("Koordinate aerodroma su izvan opsega");
        }

        this.kod = kod;
        this.ime = ime;
        this.x = x;
        this.y = y;
    }

    public String toString() {
        return "Aerodrom: " + kod + " " + ime + " " + x + " " + y;
    }

    public String getKod() {
        return kod;
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
