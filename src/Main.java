import Modeli.KontrolaLeta;
import Modeli.Aerodrom;
import Modeli.Let;

import Fajlovi.CSVCitac;

import java.io.IOException;
import java.time.LocalTime;

public class Main {

    public static void main(String[] args) {
        KontrolaLeta kontrola = new KontrolaLeta();

        try {

            CSVCitac.ucitaj("C:\\Users\\Mihajlo\\IdeaProjects\\OOP2-Projekat\\src\\af.csv", kontrola);

            System.out.println("Aerodromi: ");

            kontrola.getAerodromi().forEach(System.out::println);

            System.out.println("Letovi: ");

            kontrola.getLetovi().forEach(System.out::println);

        }
        catch(IOException e){
            System.out.println("Fajl nije moguce otvoriti ili procitati " + e.getMessage());
            }

         catch (IllegalArgumentException e) {
            System.out.println("Greska: " + e.getMessage());
        }
    }
}