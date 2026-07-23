package Modeli;

import java.time.LocalTime;

public class Let {
    private Aerodrom polazniAerodrom;
    private Aerodrom odredisniAerodrom;
    private LocalTime vremePoletanja;
    private int trajanje;

    public Let(Aerodrom polazniAerodrom, Aerodrom odredisniAerodrom, LocalTime vremePoletanja, int trajanje) {
        this.polazniAerodrom = polazniAerodrom;
        this.odredisniAerodrom = odredisniAerodrom;
        this.vremePoletanja = vremePoletanja;
        this.trajanje = trajanje;
    }

    public String toString() {
        return "Let: " + polazniAerodrom.getKod() + " " + odredisniAerodrom.getKod() + " " + vremePoletanja + " " + trajanje;
    }


    public Aerodrom getPoletanje() {
        return polazniAerodrom;
    }

    public void setPoletanje(Aerodrom poletanje) {
        this.polazniAerodrom = poletanje;
    }

    public Aerodrom getSletanje() {
        return odredisniAerodrom;
    }

    public void setSletanje(Aerodrom sletanje) {
        this.odredisniAerodrom = sletanje;
    }

    public LocalTime getVreme() {
        return vremePoletanja;
    }

    public void setVreme(int vreme) {
        this.vremePoletanja = vremePoletanja;
    }

    public int getTrajanje() {
        return trajanje;
    }

    public void setTrajanje(int trajanje) {
        this.trajanje = trajanje;
    }
}
