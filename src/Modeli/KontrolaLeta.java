package Modeli;

import java.util.ArrayList;
import java.util.List;

public class KontrolaLeta {
    private List<Aerodrom> aerodromi;
    private List<Let> letovi;

    public KontrolaLeta() {
        aerodromi = new ArrayList<>();
        letovi = new ArrayList<>();
    }

    public void dodajAerodrom(Aerodrom aerodrom) {
        if(aerodrom == null) {
            throw new IllegalArgumentException("Aerodrom nije unet");
        }
        if(pronadjiAerodrom(aerodrom.getKod()) != null) {
            throw new IllegalArgumentException("Aerodrom vec postoji");
        }
        aerodromi.add(aerodrom);
    }


    public Aerodrom pronadjiAerodrom(String kod) {
        if(kod == null) {
            return null;
        }
        for(Aerodrom aerodrom : aerodromi) {
            if(aerodrom.getKod().equals(kod)) {
                return aerodrom;
            }
        }
        return null;
    }

    public List<Aerodrom> getAerodromi() {
        return aerodromi;
    }

    public void setAerodromi(List<Aerodrom> aerodromi) {
        this.aerodromi = aerodromi;
    }

    public List<Let> getLetovi() {
        return letovi;
    }

    public void setLetovi(List<Let> letovi) {
        this.letovi = letovi;
    }
}

