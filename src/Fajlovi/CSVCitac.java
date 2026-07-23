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
    private enum Modeli {Aerodrom, Let, Nista}

    public static void ucitaj(String path, KontrolaLeta kontrolaLeta) throws IOException {
        if(path == null || path.isEmpty()) {
            throw new IllegalArgumentException(
                    "Putanja do fajla nije valdina"
            );
        }

        KontrolaLeta temp = new KontrolaLeta();
        Modeli model = Modeli.Nista;

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
                    continue;
                }

                if(linija.equals("# FLIGHTS")) {
                    model = Modeli.Let;
                    continue;
                }

                if(linija.equals("CODE,NAME,X,Y") || linija.equals("FROM,TO,DEPARTURE,DURATION")) {
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

            kontrolaLeta.obrisiSvePodatke();

            for(Aerodrom aerodrom : temp.getAerodromi()){
                kontrolaLeta.dodajAerodrom(aerodrom);
            }

            temp.getLetovi().forEach(let->kontrolaLeta.dodajLet(let.getPoletanje().getKod(),let.getSletanje().getKod(),let.getVreme(),let.getTrajanje()));

        }

    }

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
