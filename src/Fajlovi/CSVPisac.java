package Fajlovi;

import Modeli.Aerodrom;
import Modeli.KontrolaLeta;
import Modeli.Let;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class CSVPisac {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

    public static void sacuvaj(String path, KontrolaLeta kontrola) throws IOException {
        if(path == null || path.isEmpty()) {
            throw new IllegalArgumentException(
                    "Nevalidna putanja do fajla"
            );
        }

        if(kontrola == null) {
            throw new IllegalArgumentException(
                    "Nevalidna kontrola leta"
            );
        }

        try (BufferedWriter pisac = Files.newBufferedWriter(Paths.get(path), StandardCharsets.UTF_8)) {
            pisac.write("# AIRPORTS");
            pisac.newLine();

            pisac.write("CODE,NAME,X,Y");
            pisac.newLine();

            for(Aerodrom aerodrom : kontrola.getAerodromi()) {
                pisac.write(aerodrom.getKod() + ", " + aerodrom.getIme() + ", " + aerodrom.getX() + ", " + aerodrom.getY());
                pisac.newLine();
            }

            pisac.write("# FLIGHTS");
            pisac.newLine();

            pisac.write("FROM,TO,DEPARTURE,DURATION");
            pisac.newLine();

            for(Let let : kontrola.getLetovi()) {
                pisac.write(let.getPoletanje().getKod() + ", " + let.getSletanje().getKod() + ", " + let.getVreme().format(formatter) + ", " + let.getTrajanje());
                pisac.newLine();
            }
        }
    }

}
