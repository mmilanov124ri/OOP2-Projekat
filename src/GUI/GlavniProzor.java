package GUI;

import Modeli.KontrolaLeta;

import javax.smartcardio.Card;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GlavniProzor extends Frame {
    private final KontrolaLeta kontrola;

    public GlavniProzor(KontrolaLeta kontrola) throws IllegalAccessException {
        super("Konntrola leta");

        if(kontrola == null){
            throw new IllegalAccessException(
                    "Nevalidna kontrola leta"
            );
        }

        this.kontrola = kontrola;

        setSize(1000,650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        Panel navigacija = new Panel(new FlowLayout(FlowLayout.LEFT));

        Button buttonAerodromi = new Button("Aerodromi");
        Button buttonLetovi = new Button("Letovi");

        navigacija.add(buttonAerodromi);
        navigacija.add(buttonLetovi);

        add(navigacija, BorderLayout.NORTH);

        CardLayout rasporedKartica = new CardLayout();
        Panel panelKartica = new Panel(rasporedKartica);

        PanelAerodromi panelAerodroma = new PanelAerodromi(kontrola);


        PanelLetovi panelLetovi = new PanelLetovi(kontrola);

        panelKartica.add(panelAerodroma, "Aerodromi");
        panelKartica.add(panelLetovi, "Letovi");

        add(panelKartica, BorderLayout.CENTER);

        buttonAerodromi.addActionListener(e->{
            panelAerodroma.osveziTabelu();
            rasporedKartica.show(panelKartica,"Aerodromi");
        });

        buttonLetovi.addActionListener(e->{
            panelLetovi.osveziAerodrome();
            panelLetovi.osveziTabelu();
            rasporedKartica.show(panelKartica,"Letovi");
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
                System.exit(0);
            }
        });

        setVisible(true);


    }

}