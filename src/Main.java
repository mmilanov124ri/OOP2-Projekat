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

            System.out.println("Pokretanje testa neaktivnosti...");
            System.out.println(
                    "Ne diraj program 5 sekundi: "
                            + "treba da se pojavi upozorenje."
            );

            new GlavniProzor(kontrola);

        } catch (Exception e) {
            System.err.println(
                    "Greska pri pokretanju aplikacije: "
                            + e.getMessage()
            );

            e.printStackTrace();
        }

    }
}