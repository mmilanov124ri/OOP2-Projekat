package GUI;

import Modeli.Let;
import Modeli.Aerodrom;
import Modeli.KontrolaLeta;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;


public class PanelLetovi extends Panel {
    private final KontrolaLeta kontrola;

    private final Choice izborPoletanje;
    private final Choice izborSletanje;

    private final TextField poljeVreme;
    private final TextField poljeTrajanje;

    private final Panel panelTabele;

    public PanelLetovi(KontrolaLeta kontrola) {
        if(kontrola == null){
            throw new IllegalArgumentException(
                    "Nevalidna kontrola leta");
        }

        this.kontrola = kontrola;

        setLayout(new BorderLayout(10 ,10));

        Panel panelUnosa = new Panel(new GridLayout(2, 5, 8, 8));

        panelUnosa.add(new Label("Polazni aerodrom:"));
        panelUnosa.add(new Label("Odredisni aerodrom: "));
        panelUnosa.add(new Label("Vreme poletanja: "));
        panelUnosa.add(new Label("Trajanje leta [min]: "));
        panelUnosa.add(new Label(""));

        izborPoletanje = new Choice();
        izborSletanje = new Choice();

        poljeVreme = new TextField();
        poljeTrajanje = new TextField();

        Button buttonDodaj = new Button("Dodaj let");

        panelUnosa.add(izborPoletanje);
        panelUnosa.add(izborSletanje);
        panelUnosa.add(poljeVreme);
        panelUnosa.add(poljeTrajanje);
        panelUnosa.add(buttonDodaj);

        add(panelUnosa, BorderLayout.NORTH);

        panelTabele = new Panel();

        Panel omotacTabele = new Panel(new BorderLayout());
        omotacTabele.add(panelTabele,BorderLayout.NORTH);


        ScrollPane skrol = new ScrollPane(ScrollPane.SCROLLBARS_AS_NEEDED);

        skrol.add(omotacTabele);

        add(skrol, BorderLayout.CENTER);

        buttonDodaj.addActionListener(e->dodajLet());

        osveziAerodrome();
        osveziTabelu();

    }

    public void osveziAerodrome(){
        String prethodnoPoletanje = null;
        String prethodnoSletanje = null;

        if(izborPoletanje.getItemCount() > 0){
            prethodnoPoletanje = izborPoletanje.getSelectedItem();
        }

        if(izborSletanje.getItemCount() > 0){
            prethodnoSletanje = izborSletanje.getSelectedItem();
        }

        izborPoletanje.removeAll();
        izborSletanje.removeAll();

        for(Aerodrom aerodrom : kontrola.getAerodromi()){
            izborPoletanje.add(aerodrom.getKod());
            izborSletanje.add(aerodrom.getKod());
        }

        izaberiAkoPostoji(izborPoletanje,prethodnoPoletanje);

        izaberiAkoPostoji(izborSletanje,prethodnoSletanje);

        if(izborSletanje.getItemCount() >= 2 && izborPoletanje.getSelectedIndex() == izborPoletanje.getSelectedIndex()){
            izborSletanje.select(1);
        }

    }

    private void izaberiAkoPostoji(Choice izbor, String vrednost){
        if(vrednost == null){
            return;
        }

        for(int i = 0; i<izbor.getItemCount(); i++){
            if(izbor.getItem(i).equals(vrednost)){
                izbor.select(i);
                return;
            }
        }
    }

    private void dodajLet(){
        try{
            if(izborPoletanje.getItemCount() < 2){
                throw new IllegalArgumentException(
                        "Za dodavanje leta moraju biti selektovana 2 aerodroma"
                );
            }

            String polazniKod = izborPoletanje.getSelectedItem().toString();

            String odredisniKod = izborSletanje.getSelectedItem().toString();

            if(polazniKod.equals(odredisniKod)){
                throw new IllegalArgumentException(
                        "Nije moguce izabrati isti aerodrom"
                );
            }

            LocalTime vremePoletanja;

            try{
                String tekstVremena = poljeVreme.getText().trim();

                if(tekstVremena.isEmpty()){
                    throw new IllegalArgumentException(
                            "Vreme poletanja je prazno"
                    );
                }

                vremePoletanja = LocalTime.parse(poljeVreme.getText().trim());
            }catch (DateTimeParseException e){
                throw new IllegalArgumentException(
                        "Nevalidan unos vremena [HH:mm]"
                );
            }

            int trajanje;

            try{
                trajanje = Integer.parseInt(poljeTrajanje.getText().trim());
            }catch(NumberFormatException e){
                throw new IllegalArgumentException(
                        "Nevalidan unos trajanja [min]"
                );
            }

            kontrola.dodajLet(polazniKod,odredisniKod,vremePoletanja,trajanje);

            osveziTabelu();
            ocistiPolja();

            prikaziPoruku("Uspeh", "Let je uspesno dodat");
        }catch (IllegalArgumentException e){
            prikaziPoruku("Greska", e.getMessage());
        }
    }

    public void osveziTabelu(){
        panelTabele.removeAll();

        panelTabele.setLayout(new GridLayout(0, 4, 5, 5));

        Label naslovOd = new Label("Od: ", Label.CENTER);
        Label naslovDo = new Label("Do: ", Label.CENTER);
        Label naslovVreme = new Label("Vreme: ", Label.CENTER);
        Label naslovTrajanje = new Label("Trajanje: ", Label.CENTER);

        Font font = new Font(Font.SANS_SERIF,Font.BOLD,13);

        naslovOd.setFont(font);
        naslovDo.setFont(font);
        naslovVreme.setFont(font);
        naslovTrajanje.setFont(font);

        panelTabele.add(naslovOd);
        panelTabele.add(naslovDo);
        panelTabele.add(naslovVreme);
        panelTabele.add(naslovTrajanje);

        for (Let let : kontrola.getLetovi()){
            panelTabele.add(new Label(let.getPoletanje().getKod(), Label.CENTER));
            panelTabele.add(new Label(let.getSletanje().getKod(), Label.CENTER));
            panelTabele.add(new Label(let.getVreme().toString(), Label.CENTER));
            panelTabele.add(new Label(let.getTrajanje() + " min", Label.CENTER));
        }

        panelTabele.validate();

        if(panelTabele.getParent() != null){
            panelTabele.getParent().validate();
        }

        panelTabele.repaint();
    }

    private void ocistiPolja(){
        poljeVreme.setText("");
        poljeTrajanje.setText("");

        poljeVreme.requestFocus();
    }

    private void prikaziPoruku(String naslov, String poruka){
        Frame roditelj = pronadjiRoditeljskiFrame();

        Dialog dijalog = new Dialog(roditelj,naslov,true);

        dijalog.setLayout(new BorderLayout(10 ,10));

        Label tekst = new Label(poruka, Label.CENTER);

        Button ok = new Button("OK");

        Panel panelDugmeta = new Panel(new FlowLayout(FlowLayout.CENTER));

        panelDugmeta.add(ok);

        dijalog.add(tekst, BorderLayout.CENTER);
        dijalog.add(panelDugmeta, BorderLayout.SOUTH);

        ok.addActionListener(e->dijalog.dispose());

        dijalog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e){
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
