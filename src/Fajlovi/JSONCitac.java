package Fajlovi;

import Modeli.Aerodrom;
import Modeli.KontrolaLeta;
import Modeli.Let;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

public class JSONCitac {

    private static class AerodromDTO {
        @SerializedName("code")
        String kod;
        @SerializedName("name")
        String naziv;
        Integer x;
        Integer y;
    }

    private static class LetDTO {
        @SerializedName("from")
        String od;
        @SerializedName("to")
        String doo;
        @SerializedName("departure")
        String vreme;
        @SerializedName("duration")
        Integer trajanje;
    }

    private static class PodaciDTO {
        @SerializedName("airports")
        List<AerodromDTO> aerodromi;
        @SerializedName("flights")
        List<LetDTO> letovi;
    }

    public static void ucitaj(
            String path,
            KontrolaLeta kontrola
    ) throws IOException {

        if (path == null || path.isBlank()) {
            throw new IllegalArgumentException(
                    "Nevalidna putanja do fajla."
            );
        }

        if (kontrola == null) {
            throw new IllegalArgumentException(
                    "Kontrola leta je nevalidna."
            );
        }

        Gson gson = new Gson();
        PodaciDTO podaci;

        try (BufferedReader citac = Files.newBufferedReader(
                Path.of(path),
                StandardCharsets.UTF_8
        )) {
            try {
                podaci = gson.fromJson(citac, PodaciDTO.class);

            } catch (JsonParseException e) {
                throw new IllegalArgumentException(
                        "Neispravan format JSON fajla: "
                                + e.getMessage(),
                        e
                );
            }
        }

        if (podaci == null) {
            throw new IllegalArgumentException(
                    "JSON fajl je prazan."
            );
        }
        if (podaci.aerodromi == null) {
            throw new IllegalArgumentException(
                    "JSON fajl ne sadrzi polje 'airports'."
            );
        }

        if (podaci.letovi == null) {
            throw new IllegalArgumentException(
                    "JSON fajl ne sadrzi polje 'flights'."
            );
        }

        if (podaci.aerodromi.isEmpty()) {
            throw new IllegalArgumentException(
                    "JSON fajl ne sadrzi aerodrome."
            );
        }

        if (podaci.letovi.isEmpty()) {
            throw new IllegalArgumentException(
                    "JSON fajl ne sadrzi letove."
            );
        }

        KontrolaLeta temp = new KontrolaLeta();

        ucitajAerodrome(podaci.aerodromi, temp);
        ucitajLetove(podaci.letovi, temp);

        kontrola.obrisiSvePodatke();

        for (Aerodrom aerodrom : temp.getAerodromi()) {
            kontrola.dodajAerodrom(aerodrom);
        }

        for (Let let : temp.getLetovi()) {
            kontrola.dodajLet(
                    let.getPoletanje().getKod(),
                    let.getSletanje().getKod(),
                    let.getVreme(),
                    let.getTrajanje()
            );
        }
    }

    private static void ucitajAerodrome(
            List<AerodromDTO> aerodromi,
            KontrolaLeta kontrola
    ) {
        for (int i = 0; i < aerodromi.size(); i++) {
            AerodromDTO aerodrom = aerodromi.get(i);

            if (aerodrom == null) {
                throw new IllegalArgumentException(
                        "Aerodrom na poziciji "
                                + (i + 1)
                                + " ne postoji."
                );
            }

            if (aerodrom.x == null || aerodrom.y == null) {
                throw new IllegalArgumentException(
                        "Aerodrom na poziciji "
                                + (i + 1)
                                + " nema validne koordinate."
                );
            }

            try {
                Aerodrom noviAerodrom = new Aerodrom(
                        aerodrom.kod,
                        aerodrom.naziv,
                        aerodrom.x,
                        aerodrom.y
                );

                kontrola.dodajAerodrom(noviAerodrom);

            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(
                        "Nevalidan aerodrom na poziciji "
                                + (i + 1)
                                + ": "
                                + e.getMessage(),
                        e
                );
            }
        }
    }

    private static void ucitajLetove(
            List<LetDTO> letovi,
            KontrolaLeta kontrola
    ) {
        for (int i = 0; i < letovi.size(); i++) {
            LetDTO let = letovi.get(i);

            if (let == null) {
                throw new IllegalArgumentException(
                        "Let na poziciji "
                                + (i + 1)
                                + " ne postoji."
                );
            }

            if (let.vreme == null || let.vreme.isBlank()) {
                throw new IllegalArgumentException(
                        "Let na poziciji "
                                + (i + 1)
                                + " nema vreme poletanja."
                );
            }

            if (let.trajanje == null || let.trajanje <= 0) {
                throw new IllegalArgumentException(
                        "Let na poziciji "
                                + (i + 1)
                                + " nema validno trajanje."
                );
            }

            LocalTime vremePoletanja;

            try {
                vremePoletanja = LocalTime.parse(
                        let.vreme.trim()
                );

            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException(
                        "Let na poziciji "
                                + (i + 1)
                                + " ima nevalidan format vremena. "
                                + "Ocekivan format je HH:mm.",
                        e
                );
            }

            try {
                kontrola.dodajLet(
                        let.od,
                        let.doo,
                        vremePoletanja,
                        let.trajanje
                );

            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(
                        "Let na poziciji "
                                + (i + 1)
                                + " nije moguce dodati: "
                                + e.getMessage(),
                        e
                );
            }
        }
    }
}