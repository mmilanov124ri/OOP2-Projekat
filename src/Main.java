import Fajlovi.JSONCitac;
import Fajlovi.JSONPisac;
import Fajlovi.CSVPisac;
import Fajlovi.CSVCitac;


import Modeli.KontrolaLeta;
import Modeli.Aerodrom;
import Modeli.Let;


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

            CSVPisac.sacuvaj(
                    "C:\\Users\\Mihajlo\\Desktop\\sacuvaniPodaci.csv",
                    kontrola
            );

            KontrolaLeta provera = new KontrolaLeta();

            CSVCitac.ucitaj(
                    "src/af.csv",
                    provera
            );

            System.out.println(
                    "Sacuvano aerodroma: "
                            + provera.getAerodromi().size()
            );

            System.out.println(
                    "Sacuvano letova: "
                            + provera.getLetovi().size()
            );

        } catch (IOException e) {
            System.out.println(
                    "Greska pri radu sa fajlom: "
                            + e.getMessage()
            );

        } catch (IllegalArgumentException e) {
            System.out.println(
                    "Neispravni podaci: "
                            + e.getMessage()
            );





    }}}