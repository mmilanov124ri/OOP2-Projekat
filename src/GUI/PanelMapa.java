package GUI;

import Modeli.KontrolaLeta;

import javax.swing.*;
import java.awt.*;

public class PanelMapa extends Panel {
    private final MapaAerodroma mapa;

    public PanelMapa(KontrolaLeta kontrola) {
        setLayout(new BorderLayout(10, 10));

        Label naslov = new Label("Mapa aerodroma", Label.CENTER);

        naslov.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));

        mapa = new MapaAerodroma(kontrola);

        Panel omotac = new Panel(new BorderLayout());
        omotac.add(mapa, BorderLayout.CENTER);

        add(naslov, BorderLayout.NORTH);
        add(omotac, BorderLayout.CENTER);
    }

    public void osveziMapu(){
        mapa.osveziMapu();
    }
}
