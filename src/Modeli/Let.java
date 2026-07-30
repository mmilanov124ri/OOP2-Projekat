package Modeli;

import java.time.LocalTime;

public class Let {
    private Aerodrom polazniAerodrom;
    private Aerodrom odredisniAerodrom;
    private LocalTime vremePoletanja;
    private int trajanje;

    //kreiranje novog leta
    public Let(Aerodrom polazniAerodrom, Aerodrom odredisniAerodrom, LocalTime vremePoletanja, int trajanje) {
        if(polazniAerodrom == null || odredisniAerodrom == null) {
            throw new IllegalArgumentException(
                    "Nevalidno dodeljeni aerodromi"
            );
        }

        if(polazniAerodrom.getKod().equals(odredisniAerodrom.getKod())) {
            throw new IllegalArgumentException(
                    "Isti polazni i odredisni aerodrom"
            );
        }

        if (vremePoletanja == null) {
            throw new IllegalArgumentException(
                    "Nevalidno vreme poletanja"
            );
        }

        if(trajanje <= 0) {
            throw new IllegalArgumentException(
                    "Nevalidna vrednost trajanja leta"
            );
        }

        this.polazniAerodrom = polazniAerodrom;
        this.odredisniAerodrom = odredisniAerodrom;
        this.vremePoletanja = vremePoletanja;
        this.trajanje = trajanje;
    }

    public String toString() {
        return "Let: " + polazniAerodrom.getKod() + " -> " + odredisniAerodrom.getKod() + ", poletanje: " + vremePoletanja + ", trajanje: " + trajanje + " min";
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

    public void setVreme(LocalTime vremePoletanja) {
        if(vremePoletanja == null) {
            throw new IllegalArgumentException(
                    "Nevalidno vreme poletanja"
            );
        }

        this.vremePoletanja = vremePoletanja;
    }

    public int getTrajanje() {
        return trajanje;
    }

    public void setTrajanje(int trajanje) {
        if(trajanje <= 0){
            throw new IllegalArgumentException(
                    "Nevalidna vrednost trajanja leta"
            );
        }

        this.trajanje = trajanje;
    }
}
