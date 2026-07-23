package Fajlovi;

import Modeli.Aerodrom;
import Modeli.KontrolaLeta;
import Modeli.Let;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class JSONPisac {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

    private static class AerodromDTO {
        @SerializedName("code")
        String kod;
        @SerializedName("name")
        String naziv;
        Integer x;
        Integer y;

        public AerodromDTO(String kod, String naziv, Integer x, Integer y) {
            this.kod = kod;
            this.naziv = naziv;
            this.x = x;
            this.y = y;
        }
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

        public LetDTO(String od, String doo, String vreme,Integer trajanje) {
            this.od = od;
            this.doo = doo;
            this.vreme = vreme;
            this.trajanje = trajanje;
        }
    }

    private static class PodaciDTO {
        @SerializedName("airports")
        List<AerodromDTO> aerodromi;
        @SerializedName("flights")
        List<LetDTO> letovi;

        public PodaciDTO(List<AerodromDTO> aerodromi, List<LetDTO> letovi) {
            this.aerodromi = aerodromi;
            this.letovi = letovi;
        }
    }

    public static void sacuvaj(String path, KontrolaLeta kontrolaLeta) throws IOException {
        if(path == null || path.isEmpty()){
            throw new IllegalArgumentException(
                    "Nevalidna putanja fajla"
            );
        }

        if(kontrolaLeta == null){
            throw new IllegalArgumentException(
                    "Nevalidna kontrola leta"
            );
        }

        List<AerodromDTO> aerodromi = new ArrayList<>();
        List<LetDTO> letovi = new ArrayList<>();

        for(Aerodrom aerodrom : kontrolaLeta.getAerodromi()){
            aerodromi.add(new AerodromDTO(aerodrom.getKod(),aerodrom.getIme(),aerodrom.getX(),aerodrom.getY()));
        }

        for (Let let : kontrolaLeta.getLetovi()){
            letovi.add(new LetDTO(let.getPoletanje().getKod(),let.getSletanje().getKod(),let.getVreme().format(formatter),let.getTrajanje()));
        }

        PodaciDTO podaci = new PodaciDTO(
                aerodromi,letovi
        );

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try(
            BufferedWriter bw = Files.newBufferedWriter(Paths.get(path), StandardCharsets.UTF_8);
        ){
            gson.toJson(podaci, bw);
        }
    }




}
