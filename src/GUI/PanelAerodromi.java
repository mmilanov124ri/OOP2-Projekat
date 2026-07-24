package GUI;

import Modeli.Aerodrom;
import Modeli.KontrolaLeta;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class PanelAerodromi extends Panel{
    private final KontrolaLeta kontrola;

    private final TextField poljeKod;
    private final TextField poljeNaziv;
    private final TextField poljeX;
    private final TextField poljeY;

    private final Panel panelTabele;

    public PanelAerodromi(KontrolaLeta kontrola) {
        if(kontrola == null){
            throw new IllegalArgumentException(
                    "Nevalidna kontrola leta");
        }

        this.kontrola = kontrola;

        setLayout(new BorderLayout(10, 10));

        Panel panelUnosa = new Panel(new GridLayout(2, 5, 8, 8));

        panelUnosa.add(new Label("Kod aerodroma:"));
        panelUnosa.add(new Label("Naziv:"));
        panelUnosa.add(new Label("Koordinata X:"));
        panelUnosa.add(new Label("Koordinata Y:"));
        panelUnosa.add(new Label(""));

        poljeKod = new TextField();
        poljeNaziv = new TextField();
        poljeX = new TextField();
        poljeY = new TextField();

        Button buttonDodaj = new Button("Dodaj aerodrom");

        panelUnosa.add(poljeKod);
        panelUnosa.add(poljeNaziv);
        panelUnosa.add(poljeX);
        panelUnosa.add(poljeY);
        panelUnosa.add(buttonDodaj);

        add(panelUnosa, BorderLayout.NORTH);

        panelTabele = new Panel();

        Panel omotacTabele = new Panel(new BorderLayout());
        omotacTabele.add(panelTabele, BorderLayout.NORTH);

        ScrollPane skrol = new ScrollPane(ScrollPane.SCROLLBARS_AS_NEEDED);

        skrol.add(omotacTabele);

        add(skrol, BorderLayout.CENTER);

        buttonDodaj.addActionListener(e->dodajAerodrom());

        osveziTabelu();

    }

    private void dodajAerodrom() {
        try{
            String kod = poljeKod.getText().trim();
            String naziv = poljeNaziv.getText().trim();

            int x = Integer.parseInt(poljeX.getText().trim());
            int y = Integer.parseInt(poljeY.getText().trim());


            Aerodrom aerodrom = new Aerodrom(kod,naziv,x,y);
            kontrola.dodajAerodrom(aerodrom);

            osveziTabelu();
            ocistiPolja();

            prikaziPoruku("Uspeh", "Aerodrom je uspesno dodat");
        }catch (NumberFormatException e){
            prikaziPoruku("Greska", "Koordinate nisu u valdinom formatu");
        }catch (IllegalArgumentException e){
            prikaziPoruku("Greska", e.getMessage());
        }

    }

    public void osveziTabelu(){
        panelTabele.removeAll();

        panelTabele.setLayout(new GridLayout(0, 4, 5, 5));

        Label naslovKod = new Label("Kod", Label.CENTER);
        Label naslovNaziv = new Label("Naziv", Label.CENTER);
        Label naslovX = new Label("X", Label.CENTER);
        Label naslovY = new Label("Y", Label.CENTER);

        Font font = new Font(Font.SANS_SERIF, Font.BOLD, 13);

        naslovKod.setFont(font);
        naslovNaziv.setFont(font);
        naslovX.setFont(font);
        naslovY.setFont(font);

        panelTabele.add(naslovKod);
        panelTabele.add(naslovNaziv);
        panelTabele.add(naslovX);
        panelTabele.add(naslovY);

        for(Aerodrom aerodrom : kontrola.getAerodromi()){
            panelTabele.add(new Label(aerodrom.getKod(), Label.CENTER));
            panelTabele.add(new Label(aerodrom.getIme(), Label.CENTER));

            panelTabele.add(new Label(String.valueOf(aerodrom.getX()),Label.CENTER));
            panelTabele.add(new Label(String.valueOf(aerodrom.getY()),Label.CENTER));

        }

        panelTabele.validate();
        panelTabele.repaint();

    }

    private void ocistiPolja(){
        poljeKod.setText("");
        poljeNaziv.setText("");
        poljeX.setText("");
        poljeY.setText("");

        poljeKod.requestFocus();
    }

    private void prikaziPoruku(String naslov, String poruka){
        Frame roditelj = pronadjiRoditeljskiFrame();

        Dialog dijalog = new Dialog(roditelj, naslov,true);

        dijalog.setLayout(new BorderLayout(10 ,10));

        Label tekst = new Label(poruka, Label.CENTER);

        Button buttonOk = new Button("OK");

        Panel panelDugmeta = new Panel(new FlowLayout(FlowLayout.CENTER));
        panelDugmeta.add(buttonOk);

        dijalog.add(tekst, BorderLayout.CENTER);
        dijalog.add(buttonOk, BorderLayout.SOUTH);

        buttonOk.addActionListener(e->dijalog.dispose());

        dijalog.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e) {
                dijalog.dispose();
            }
        });

        dijalog.setSize(450, 140);
        dijalog.setResizable(false);
        dijalog.setLocationRelativeTo(roditelj);
        dijalog.setVisible(true);
    }

    private Frame pronadjiRoditeljskiFrame(){
        Container roditelj = getParent();

        while(roditelj != null){
            if(roditelj instanceof Frame){
                return (Frame)roditelj;
            }

            roditelj = roditelj.getParent();
        }
        return null;

    }




}
