package Fajlovi;

import Modeli.KontrolaLeta;
import Modeli.Let;
import Modeli.Aerodrom;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.channels.ScatteringByteChannel;
import java.time.DateTimeException;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public class CSVCitac {

    //Modeli definise da li linija koda pripada aerodromu, letu ili nije nijedno od ta dva
    private enum Modeli {Aerodrom, Let, Nista}

    //Ucitava putanju do fajla za datu kontrolu leta
    public static void ucitaj(String path, KontrolaLeta kontrolaLeta) throws IOException {
        if(kontrolaLeta == null){
            throw new IllegalArgumentException(
                    "Nevalidna kontrolaLeta");
        }

        boolean sekcijaAerodromi = false;
        boolean sekcijaLetovi = false;
        boolean zaglavljeAerodromi = false;
        boolean zaglavljeLetovi = false;

        if(path == null || path.isEmpty()) {
            throw new IllegalArgumentException(
                    "Putanja do fajla nije valdina"
            );
        }

        KontrolaLeta temp = new KontrolaLeta();
        Modeli model = Modeli.Nista;

        //prolazi liniju po liniju i deklarise je po vrsti
        try (BufferedReader citac = new BufferedReader(new FileReader(path))) {

            String linija;
            int brojLinije = 0;

            while((linija = citac.readLine()) != null) {
                brojLinije++;
                linija = linija.trim();

                if(linija.isEmpty()) {
                    continue;
                }

                if(linija.equals("# AIRPORTS")) {
                    model = Modeli.Aerodrom;
                    sekcijaAerodromi = true;
                    continue;
                }

                if(linija.equals("# FLIGHTS")) {
                    model = Modeli.Let;
                    sekcijaLetovi = true;
                    continue;
                }

                if(linija.equals("CODE,NAME,X,Y")) {
                    if(model != Modeli.Aerodrom){
                        throw new IllegalArgumentException(
                                "Zaglavlje aerodroma je u pogresnoj sekciji"
                        );
                    }

                    zaglavljeAerodromi = true;
                    continue;
                }

                 if(linija.equals("FROM,TO,DEPARTURE,DURATION")){
                     if (model != Modeli.Let) {
                         throw new IllegalArgumentException(
                                 "Zaglavlje letova je u pogresnoj sekciji"
                         );
                     }

                     zaglavljeLetovi = true;
                     continue;
                 }

                try{
                  if(model == Modeli.Aerodrom) {
                      ucitajAerodrom(linija, temp);
                  }
                  else if(model == Modeli.Let) {
                      ucitajLet(linija, temp);
                  }
                  else{
                      throw new IllegalArgumentException(
                              "Podaci su izvan sekcije"
                      );
                  }
                }
                catch(IllegalArgumentException e){
                    throw new IllegalArgumentException(
                            "Greska tokom ucitavanja fajla na liniji: " + brojLinije + ": " + e.getMessage()
                    );
                };


            }

            if(!sekcijaAerodromi || !sekcijaLetovi || !zaglavljeAerodromi || !zaglavljeLetovi) {
                throw new IllegalArgumentException(
                        "CSV fajl ne sadrzi ocekivana zaglavlja i sekcije"
                );
            }

            kontrolaLeta.obrisiSvePodatke();

            for(Aerodrom aerodrom : temp.getAerodromi()){
                kontrolaLeta.dodajAerodrom(aerodrom);
            }

            temp.getLetovi().forEach(let->kontrolaLeta.dodajLet(let.getPoletanje().getKod(),let.getSletanje().getKod(),let.getVreme(),let.getTrajanje()));

        }

    }

    //kreira konkretan aerodrom iz ucitane linije
    private static void ucitajAerodrom(String linija, KontrolaLeta kontrolaLeta){
        String[] delovi = linija.split(",",-1);

        if(delovi.length != 4){
            throw new IllegalArgumentException(
                    "Aerodrom mora imati 4 polja : CODE, NAME,X i Y"
            );
        }

        String kod = delovi[0].trim();
        String ime = delovi[1].trim();

        int x;
        int y;

        try{
            x = Integer.parseInt(delovi[2].trim());
            y = Integer.parseInt(delovi[3].trim());
        }
        catch(NumberFormatException e){
            throw new IllegalArgumentException(
                    "Pogresan format broja"
            );
        };

        kontrolaLeta.dodajAerodrom(new Aerodrom(kod,ime,x,y));
    }

    //kreira konkretan let iz ucitane linije
    private static void ucitajLet(String linija, KontrolaLeta kontrolaLeta){
        String[] delovi = linija.split(",",-1);

        if(delovi.length != 4){
            throw new IllegalArgumentException(
                    "Let mora imati 4 polja : FROM, TO, DEPARTURE, DURATION"
            );
        }
        String polazni = delovi[0].trim();
        String odredisni = delovi[1].trim();

        LocalTime vremePoletanja;

        try{
            vremePoletanja = LocalTime.parse(delovi[2].trim());
        }catch(DateTimeParseException e){
            throw new IllegalArgumentException(
                    "Pogresan format vremena (HH:mm)"
            );
        }

        int trajanje;

        try{
            trajanje = Integer.parseInt(delovi[3].trim());
        }catch(NumberFormatException e){
            throw new IllegalArgumentException(
                    "Pogresan format broja"
            );
        }

        kontrolaLeta.dodajLet(polazni,odredisni,vremePoletanja,trajanje);
    }
}
