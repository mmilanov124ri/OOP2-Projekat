import GUI.GlavniProzor;
import Modeli.KontrolaLeta;
import java.awt.*;

public class Main {

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                //kreiranje kontrole leta za pokrenuti program
                KontrolaLeta kontrola = new KontrolaLeta();

                //otvaranje glavnog prozora
                new GlavniProzor(kontrola);

            } catch (Exception e) {
                System.err.println(
                        "Greska pri pokretanju aplikacije: " + e.getMessage()
                );
            }
        });
    }
}