package GUI;

import Fajlovi.CSVCitac;
import Fajlovi.CSVPisac;
import Fajlovi.JSONCitac;
import Fajlovi.JSONPisac;

import Modeli.KontrolaLeta;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.file.Path;


public class GlavniProzor extends Frame {
    private final KontrolaLeta kontrola;

    private final MenadzerNeaktivnosti neaktivnosti;

    private final PanelMapa panelMapa;
    private final PanelAerodromi panelAerodroma;
    private final PanelLetovi panelLetovi;

    //stvaranje glavnog prozora programa
    public GlavniProzor(KontrolaLeta kontrola) throws IllegalAccessException {
        super("Konntrola leta");

        if (kontrola == null) {
            throw new IllegalAccessException(
                    "Nevalidna kontrola leta"
            );
        }

        this.kontrola = kontrola;

        neaktivnosti = new MenadzerNeaktivnosti(this,60000,5);

        //dimenzije glavnog prozora
        setSize(1600, 900);
        setMinimumSize(new Dimension(1000, 650));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        //kreiranje panela i dodavanje funkcionalnosti
        Panel navigacija = new Panel(new FlowLayout(FlowLayout.LEFT));

        Button buttonMapa = new Button("Mapa");
        Button buttonAerodromi = new Button("Aerodromi");
        Button buttonLetovi = new Button("Letovi");

        navigacija.add(buttonAerodromi);
        navigacija.add(buttonLetovi);
        navigacija.add(buttonMapa);

        add(navigacija, BorderLayout.NORTH);

        CardLayout rasporedKartica = new CardLayout();
        Panel panelKartica = new Panel(rasporedKartica);

        panelAerodroma = new PanelAerodromi(kontrola,this::osveziSvePrikaze);
        panelLetovi = new PanelLetovi(kontrola, this::osveziSvePrikaze);
        panelMapa = new PanelMapa(kontrola, neaktivnosti);

        panelKartica.add(panelAerodroma, "Aerodromi");
        panelKartica.add(panelLetovi, "Letovi");
        panelKartica.add(panelMapa, "Mapa");

        add(panelKartica, BorderLayout.CENTER);

        //dodavanje dugmadi za prolaz kroz program
        buttonAerodromi.addActionListener(e -> {
            panelMapa.ponistiSelekciju();
            panelAerodroma.osveziTabelu();
            rasporedKartica.show(panelKartica, "Aerodromi");
        });

        buttonLetovi.addActionListener(e -> {
            panelMapa.ponistiSelekciju();
            panelLetovi.osveziAerodrome();
            panelLetovi.osveziTabelu();
            rasporedKartica.show(panelKartica, "Letovi");
        });

        buttonMapa.addActionListener(e -> {
            panelMapa.osveziMapu();

            rasporedKartica.show(panelKartica, "Mapa");
        });

        //zatvaranje aplikacije
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                zatvoriAplikaciju();
            }
        });

        napraviMeni();

        setVisible(true);
        neaktivnosti.pokreni();


    }

    //meni bar za manipulaciju sa fajlovima
    private void napraviMeni() {
        MenuBar menuBar = new MenuBar();

        Menu meniFajl = new Menu("Fajl");

        MenuItem ucitajCSV = new MenuItem("Ucitaj CSV");
        MenuItem ucitajJSON = new MenuItem("Ucitaj JSON");

        MenuItem sacuvajCSV = new MenuItem("Sacuvaj CSV");
        MenuItem sacuvajJSON = new MenuItem("Sacuvaj JSON");

        MenuItem izlaz = new MenuItem("Izlaz");

        meniFajl.add(ucitajCSV);
        meniFajl.add(ucitajJSON);
        meniFajl.addSeparator();
        meniFajl.add(sacuvajCSV);
        meniFajl.add(sacuvajJSON);
        meniFajl.addSeparator();
        meniFajl.add(izlaz);

        menuBar.add(meniFajl);
        setMenuBar(menuBar);

        ucitajCSV.addActionListener(e -> ucitajCSV());
        ucitajJSON.addActionListener(e -> ucitajJSON());

        sacuvajCSV.addActionListener(e -> sacuvajCSV());
        sacuvajJSON.addActionListener(e -> sacuvajJSON());

        izlaz.addActionListener(e -> {
            zatvoriAplikaciju();
        });
    }

    //biranje csv fajla za prikaz
    private void ucitajCSV() {
        String putanja = izaberiFajl("Izaberite CSV fajl", FileDialog.LOAD);

        if (putanja == null) {
            return;
        }

        try {
            CSVCitac.ucitaj(putanja, kontrola);
            osveziSvePrikaze();

            prikaziPoruku("Uspeh", "Uspesno ucitan CSV fajl");

        } catch (IOException e) {
            prikaziPoruku("Greska", "Nedozvoljen pristup CSV fajlu" + e.getMessage());
        } catch (IllegalArgumentException e) {
            prikaziPoruku("Greska", "Neispravan CSV fajl: " + e.getMessage());
        }
    }

    //biranje json fajla za prikaz
    private void ucitajJSON() {
        String putanja = izaberiFajl("Izaberite JSON fajl", FileDialog.LOAD);

        if (putanja == null) {
            return;
        }

        try {
            JSONCitac.ucitaj(putanja, kontrola);

            osveziSvePrikaze();

            prikaziPoruku("Uspeh", "JSON fajl je uspesno ucitan");

        } catch (IOException e) {
            prikaziPoruku("Greska", "Nedozvoljen pristup JSON fajlu" + e.getMessage());


        } catch (IllegalArgumentException e) {
            prikaziPoruku("Greska", "Neispravan JSON fajl: " + e.getMessage());
        }
    }

    //biranje csv fajla za upis
    private void sacuvajCSV() {
        String putanja = izaberiFajl("Sacuvajte CSV fajl", FileDialog.SAVE);

        if (putanja == null) {
            return;
        }

        try {
            CSVPisac.sacuvaj(putanja, kontrola);

            prikaziPoruku("Uspeh", "Uspesno sacuvan CSV fajl");
        } catch (IOException | IllegalArgumentException e) {
            prikaziPoruku("Greska", "Neuspesno cuvanje" + e.getMessage());
        }
    }

    //biranje json fajla za upis
    private void sacuvajJSON() {
        String putanja = izaberiFajl("Sacuvajte JSON fajl", FileDialog.SAVE);

        if (putanja == null) {
            return;
        }

        putanja = dodajEkstenziju(putanja, ".json");

        try {
            JSONPisac.sacuvaj(putanja, kontrola);

            prikaziPoruku("Uspeh", "Uspesno sacuvan JSON fajl");

        } catch (IOException | IllegalArgumentException e) {
            prikaziPoruku("Greska", "Neuspesno cuvanje fajla" + e.getMessage());
        }

    }

    //pomocna funkcija za odabir fajla iz direktorijuma
    private String izaberiFajl(String naslov, int rezim) {
        FileDialog izbor = new FileDialog(this, naslov, rezim);

        izbor.setVisible(true);

        String imeFajla = izbor.getFile();
        String direktorijum = izbor.getDirectory();

        if (imeFajla == null || direktorijum == null) {
            return null;
        }

        return Path.of(direktorijum, imeFajla).toString();

    }

    //dodavanje ekstenzije (.csv,.json)
    private String dodajEkstenziju(String putanja, String ekstenzija) {
        if (!putanja.toLowerCase().endsWith(ekstenzija)) {
            return putanja + ekstenzija;
        }

        return putanja;
    }

    //refresh celog menija i ostalih elemenata
    private void osveziSvePrikaze() {
        panelMapa.ponistiSelekciju();
        panelAerodroma.osveziTabelu();
        panelLetovi.osveziAerodrome();
        panelLetovi.osveziTabelu();
        panelMapa.osveziMapu();
        panelMapa.osveziPodatkeSimulacije();

        validate();
        repaint();
    }

    //zatvaranje aplikacije
    private void zatvoriAplikaciju(){
        panelMapa.zatvori();
        neaktivnosti.zaustavi();

        dispose();
        System.exit(0);
    }

    //prikaz informacione poruke o statusu procesa
    private void prikaziPoruku(String naslov, String poruka) {
        Dialog dijalog = new Dialog(this, naslov, true);

        dijalog.setLayout(new BorderLayout(10, 10));

        Label tekst = new Label(poruka, Label.CENTER);
        Button buttonOK = new Button("OK");

        Panel panelButton = new Panel(new FlowLayout(FlowLayout.CENTER));

        panelButton.add(buttonOK);

        dijalog.add(tekst, BorderLayout.CENTER);
        dijalog.add(panelButton, BorderLayout.SOUTH);

        buttonOK.addActionListener(e -> {
            dijalog.dispose();
        });

        dijalog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dijalog.dispose();
            }
        });

        dijalog.setSize(550, 150);
        dijalog.setResizable(false);
        dijalog.setLocationRelativeTo(this);
        dijalog.setVisible(true);
    }
}