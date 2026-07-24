import Fajlovi.JSONCitac;
import Fajlovi.JSONPisac;
import Fajlovi.CSVPisac;
import Fajlovi.CSVCitac;


import GUI.GlavniProzor;
import Modeli.KontrolaLeta;
import Modeli.Aerodrom;
import Modeli.Let;

import javax.swing.SwingUtilities;
import java.awt.*;
import java.io.IOException;
import java.time.LocalTime;

public class Main {

    public static void main(String[] args) {
        KontrolaLeta kontrola = new KontrolaLeta();

        try {
            CSVCitac.ucitaj(
                    "src/af.csv",
                    kontrola
            );

            System.out.println(
                    "CSV uspesno ucitan."
            );

            System.out.println(
                    "Broj aerodroma posle ucitavanja: "
                            + kontrola.getAerodromi().size()
            );

            System.out.println(
                    "Broj letova posle ucitavanja: "
                            + kontrola.getLetovi().size()
            );

        } catch (IOException e) {
            System.out.println(
                    "CSV fajl nije moguce otvoriti: "
                            + e.getMessage()
            );

        } catch (IllegalArgumentException e) {
            System.out.println(
                    "CSV fajl sadrzi neispravne podatke: "
                            + e.getMessage()
            );
        }

        /*
         * 2. Programsko testiranje dodavanja aerodroma i leta.
         */
        try {
            Aerodrom testAerodrom = new Aerodrom(
                    "TST",
                    "Testni aerodrom",
                    20,
                    40
            );

            kontrola.dodajAerodrom(testAerodrom);

            kontrola.dodajLet(
                    "TST",
                    "BEG",
                    LocalTime.of(12, 30),
                    60
            );

            System.out.println(
                    "Testni aerodrom i let su uspesno dodati."
            );

        } catch (IllegalArgumentException e) {
            System.out.println(
                    "Greska prilikom testnog dodavanja: "
                            + e.getMessage()
            );
        }

        System.out.println(
                "Konacan broj aerodroma: "
                        + kontrola.getAerodromi().size()
        );

        System.out.println(
                "Konacan broj letova: "
                        + kontrola.getLetovi().size()
        );

        /*
         * 3. Pokretanje GUI-ja.
         */
        EventQueue.invokeLater(() -> {
            try {
                new GlavniProzor(kontrola);

            } catch (Exception e) {
                System.out.println(
                        "Greska prilikom pokretanja GUI-ja: "
                                + e.getMessage()
                );

                e.printStackTrace();
            }
        });


    }
}