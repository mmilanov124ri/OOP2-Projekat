package GUI;

import Modeli.Aerodrom;
import Modeli.KontrolaLeta;
import Simulacija.SchedulerLetova;
import Simulacija.SimulacijaLeta;
import Simulacija.SimulatorLetova;

import java.awt.*;
import java.util.List;

public class PanelMapa extends Panel {
    private final MapaAerodroma mapa;
    private final KontrolaLeta kontrola;
    private final Panel panelCheckBoxa;

    private SimulatorLetova simulator;

    private final Label labelaVreme;
    private final Button buttonStart;
    private final Button buttonPauza;
    private final Button buttonReset;

    private final MenadzerNeaktivnosti neaktivnosti;

    public PanelMapa(KontrolaLeta kontrola, MenadzerNeaktivnosti neaktivnosti) {
        if(kontrola == null){
            throw new NullPointerException(
                    "Nevalidna kontrola leta");
        }

        this.kontrola = kontrola;

        this.neaktivnosti = neaktivnosti;

        setLayout(new BorderLayout(10, 10));

        Label naslov = new Label("Mapa aerodroma", Label.CENTER);

        naslov.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));

        labelaVreme = new Label("Vreme: 00:00", Label.CENTER);
        labelaVreme.setFont(new Font(Font.MONOSPACED,Font.BOLD,16));

        buttonStart = new Button("Start");
        buttonPauza = new Button("Pauza");
        buttonReset = new Button("Reset");

        Panel panelKontrole = new Panel(new FlowLayout(FlowLayout.LEFT,10 ,5));
        panelKontrole.add(labelaVreme);
        panelKontrole.add(buttonStart);
        panelKontrole.add(buttonPauza);
        panelKontrole.add(buttonReset);

        Panel gornjiPanel = new Panel(new BorderLayout());
        gornjiPanel.add(naslov, BorderLayout.NORTH);
        gornjiPanel.add(panelKontrole, BorderLayout.CENTER);

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

        add(gornjiPanel,BorderLayout.NORTH);
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

        buttonStart.addActionListener(e->{
            if(simulator != null){
                simulator.start();
                azurirajNeaktivnost();
            }
        });

        buttonPauza.addActionListener(e->{
            if(simulator != null){
                simulator.pauza();
                azurirajNeaktivnost();
            }
        });

        buttonReset.addActionListener(e->{
            if(simulator != null){
                simulator.reset();
                azurirajNeaktivnost();
            }
        });

        napraviNoviSimulator();

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

    private void napraviNoviSimulator(){
        if(simulator != null){
            simulator.zatvori();
        }

        List<SimulacijaLeta> raspored = SchedulerLetova.napraviRaspored(kontrola.getLetovi());

        simulator = new SimulatorLetova(raspored,this::osveziPrikazSimualcije);

        mapa.postaviSimulator(simulator);

        osveziPrikazSimualcije();

    }

    private void osveziPrikazSimualcije(){
        if(simulator == null){
            labelaVreme.setText("Vreme: 00:00");
            return;
        }

        labelaVreme.setText("Vreme: " + simulator.getFormatiranoVreme());

        mapa.repaint();

    }

    public void osveziPodatkeSimulacije(){
        napraviNoviSimulator();
    }
    public void zatvori(){
        if(simulator != null){
            simulator.zatvori();
        }
    }

    private void azurirajNeaktivnost(){
        boolean simulacijaUToku = simulator != null && simulator.jePokrenut();

        boolean aerodromJeSelektovan = mapa.imaSelektovanAerodrom();

        if(simulacijaUToku || aerodromJeSelektovan){
            neaktivnosti.pauziraj();
        }else{
            neaktivnosti.nastavi();
        }

    }


}
