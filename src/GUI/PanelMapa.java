package GUI;

import Modeli.Aerodrom;
import Modeli.KontrolaLeta;

import javax.swing.*;
import java.awt.*;

public class PanelMapa extends Panel {
    private final MapaAerodroma mapa;
    private final KontrolaLeta kontrola;
    private final Panel panelCheckBoxa;

    public PanelMapa(KontrolaLeta kontrola, MenadzerNeaktivnosti neaktivnosti) {
        if(kontrola == null){
            throw new NullPointerException(
                    "Nevalidna kontrola leta");
        }

        this.kontrola = kontrola;

        setLayout(new BorderLayout(10, 10));

        Label naslov = new Label("Mapa aerodroma", Label.CENTER);

        naslov.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));

        mapa = new MapaAerodroma(kontrola,neaktivnosti);

        panelCheckBoxa = new Panel(new GridLayout(0,1,3,3));

        ScrollPane skrolLista = new ScrollPane(ScrollPane.SCROLLBARS_AS_NEEDED);
        skrolLista.setPreferredSize(new Dimension(230,0));
        skrolLista.add(panelCheckBoxa);

        Panel desniPanel = new Panel(new BorderLayout(5,5));

        Label naslovListe = new Label("Prikaz aerodroma", Label.CENTER);

        naslovListe.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));

        Panel panelButton = new Panel(new GridLayout(1,2,5,5));

        Button buttonPrikaziSve = new Button("Prikazi sve");
        Button buttonSakrijSve = new Button("Sakrij sve");

        panelButton.add(buttonPrikaziSve);
        panelButton.add(buttonSakrijSve);

        desniPanel.add(naslovListe,BorderLayout.NORTH);
        desniPanel.add(skrolLista,BorderLayout.CENTER);
        desniPanel.add(panelButton,BorderLayout.SOUTH);

        add(naslov,BorderLayout.NORTH);
        add(mapa,BorderLayout.CENTER);
        add(desniPanel,BorderLayout.EAST);

        buttonPrikaziSve.addActionListener(e->{
            mapa.prikaziSveAerodrome();
            osveziListuAerodroma();
        });

        buttonSakrijSve.addActionListener(e->{
            mapa.sakrijSveAerodrome();
            osveziListuAerodroma();
        });

        osveziListuAerodroma();

    }

    public void osveziMapu(){
        osveziListuAerodroma();
        mapa.osveziMapu();
    }

    public void ponistiSelekciju(){
        mapa.ponistiSelekciju();
    }

    public void osveziListuAerodroma(){
        panelCheckBoxa.removeAll();

        panelCheckBoxa.setLayout(new GridLayout(0,1,3,3));

        for(Aerodrom aerodrom : kontrola.getAerodromi()){
            String tekst = String.format("%s - %s (%d, %d)",aerodrom.getKod(),aerodrom.getIme(),aerodrom.getX(),aerodrom.getY());

            Checkbox checkbox = new Checkbox(tekst,mapa.jeAerodromVidljiv(aerodrom.getKod()));

            checkbox.addItemListener(e -> {
                mapa.postaviVidljivostAerodroma(aerodrom.getKod(),checkbox.getState());
            });

            panelCheckBoxa.add(checkbox);

        }

        panelCheckBoxa.validate();

        if(panelCheckBoxa.getParent() != null){
            panelCheckBoxa.getParent().validate();
        }

        panelCheckBoxa.repaint();

    }

}
