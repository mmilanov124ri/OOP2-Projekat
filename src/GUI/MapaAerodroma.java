package GUI;

import Modeli.Aerodrom;
import Modeli.KontrolaLeta;
import Modeli.Let;

import Simulacija.SimulacijaLeta;
import Simulacija.SimulatorLetova;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.TimerTask;
import java.util.Timer;
import java.util.HashSet;
import java.util.Set;


public class MapaAerodroma extends Canvas {

    //margina mape i velicine aerodroma
    private static final int margina = 20;
    private static final int velicina = 12;

    private final KontrolaLeta kontrola;

    private SimulatorLetova simulator;

    private final MenadzerNeaktivnosti neaktivnosti;

    private Aerodrom selektovaniAerodrom;
    private boolean prikazCrveno = true;

    private final Set<String> skriveniAerodromi = new HashSet<>();

    private int trenutniPocetakX;
    private int trenutniPocetakY;
    private int trenutnaSirinaMape;
    private int trenutnaVisinaMape;

    private final Timer tajmerTreperenja;

    //kreiranje nove mape aerodroma
    public MapaAerodroma(KontrolaLeta kontrola, MenadzerNeaktivnosti neaktivnosti) {
        if (kontrola == null) {
            throw new IllegalArgumentException(
                    "Nevalidna kontrola leta"
            );
        }

        if(neaktivnosti == null){
            throw new IllegalArgumentException(
                    "Nevalidna neaktivnost"
            );
        }

        this.kontrola = kontrola;
        this.neaktivnosti = neaktivnosti;

        setBackground(Color.WHITE);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                obradiKlik(e.getX(), e.getY());
            }
        });

        tajmerTreperenja = new Timer("Treperenje aerodroma", true);

        tajmerTreperenja.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(selektovaniAerodrom != null){
                    prikazCrveno = !prikazCrveno;

                    EventQueue.invokeLater(MapaAerodroma.this::repaint);
                }
            }
            },500,500
        );
    }

    //crtanje mape sa avionima i aerodromima
    @Override
    public void paint(Graphics g) {
        super.paint(g);

        int sirinaMape = getWidth() - 2 * margina;
        int visinaMape = getHeight() - 2 * margina;

        if (sirinaMape <= 0 || visinaMape <= 0) {
            return;
        }

        int pocetakX = (getWidth() - sirinaMape) / 2;
        int pocetakY = (getHeight() - visinaMape) / 2;

        trenutniPocetakX = pocetakX;
        trenutniPocetakY = pocetakY;
        trenutnaSirinaMape = sirinaMape;
        trenutnaVisinaMape = visinaMape;

        nacrtajOkvirMape(g, pocetakX, pocetakY,sirinaMape, visinaMape);

        for (Aerodrom aerodrom : kontrola.getAerodromi()) {
            if(!jeAerodromVidljiv(aerodrom.getKod())){
                continue;
            }
            nacrtajAerodrom(g, aerodrom, pocetakX, pocetakY, sirinaMape, visinaMape);
        }

        nacrtajAvione(g);
    }

    //crtanje okvira mape kao i X i Y osi
    private void nacrtajOkvirMape(Graphics g, int pocetakX, int pocetakY, int sirinaMape, int visinaMape) {
        g.setColor(Color.LIGHT_GRAY);

        g.drawRect(margina, margina, sirinaMape, visinaMape);


        int ekranX0 = pretvoriX(0, pocetakX, sirinaMape);

        g.drawLine(ekranX0,pocetakY,ekranX0,pocetakY + visinaMape);

        int ekranY0 = pretvoriY(0, pocetakY, visinaMape);

        g.drawLine(pocetakX,ekranY0,pocetakX + sirinaMape,ekranY0);
    }

    //pojedinacno crtanje aerodroma na mapi
    private void nacrtajAerodrom(Graphics g,Aerodrom aerodrom, int pocetakX, int pocetakY, int sirinaMape, int visinaMape) {
        int ekranX = pretvoriX(aerodrom.getX(), pocetakX, sirinaMape);
        int ekranY = pretvoriY(aerodrom.getY(), pocetakY, visinaMape);

        int polaVelicine = velicina / 2;

        if(aerodrom == selektovaniAerodrom && prikazCrveno){
            g.setColor(Color.RED);
        }else{
            g.setColor(Color.LIGHT_GRAY);
        }


        g.fillRect(ekranX - polaVelicine, ekranY - polaVelicine, velicina, velicina);

        g.setColor(Color.BLACK);

        g.drawString(aerodrom.getKod(), ekranX + 7, ekranY - 7);
    }

    //konverzija realne koordinate X u koordinate na mapi
    private int pretvoriX(int x,int pocetakX, int sirinaMape) {
        double odnos = (x+180.0) / 360.0;
        return pocetakX + (int) Math.round(odnos * sirinaMape);
    }

    //konverzija realne koordinate Y u koordinate na mapi
    private int pretvoriY(int y, int pocetakY, int visinaMape) {
        double odnos = (90.0 - y) / 180.0;

        return pocetakY + (int) Math.round(odnos * visinaMape);
    }

    //refresh mape
    public void osveziMapu(){
        repaint();
    }

    //obrada klika na najblizi aerodrom i promena stanja selekcije
    private void obradiKlik(int misX, int misY){
        Aerodrom kliknutAerodrom = pronadjiAerodromNaPoziciji(misX, misY);

        if(trenutnaSirinaMape <= 0 || trenutnaVisinaMape <= 0){
            return;
        }

        if(kliknutAerodrom != null && klikJeNaAerodromu(selektovaniAerodrom,misX,misY)){
            ponistiSelekciju();
            return;
        }

        if(kliknutAerodrom == null){
            ponistiSelekciju();
            return;
        }

        selektovaniAerodrom = kliknutAerodrom;
        prikazCrveno = true;

        neaktivnosti.pauziraj();
        repaint();

    }

    //pomocna funkcija trazenja najblizeg aerodroma od kursora misa
    private Aerodrom pronadjiAerodromNaPoziciji(int misX, int misY){
        Aerodrom najblizi = null;
        double najmanjeRastojanje = Double.MAX_VALUE;

        int dozvoljenoRastojanje = 10;

        for(Aerodrom aerodrom : kontrola.getAerodromi()){
            if(!jeAerodromVidljiv(aerodrom.getKod())){
                continue;
            }
            int ekranX = pretvoriX(aerodrom.getX(),trenutniPocetakX,trenutnaSirinaMape);
            int ekranY = pretvoriY(aerodrom.getY(),trenutniPocetakY,trenutnaVisinaMape);

            int razlikaX = misX - ekranX;
            int razlikaY = misY - ekranY;

            double rastojanje = Math.sqrt(Math.pow(razlikaX, 2) + Math.pow(razlikaY, 2));

            if(rastojanje <= dozvoljenoRastojanje && rastojanje < najmanjeRastojanje){
                najmanjeRastojanje = rastojanje;
                najblizi = aerodrom;
            }
        }
        return najblizi;
    }

    //resetovanje selektovanog aerodroma
    public void ponistiSelekciju() {

        selektovaniAerodrom = null;
        prikazCrveno = true;

        if(simulator == null || !simulator.jePokrenut()){
        neaktivnosti.nastavi();
        }
        repaint();
    }

    //da li je aerodrom vidljiv u checkbox-u
    public boolean jeAerodromVidljiv(String kod){
        return !skriveniAerodromi.contains(kod);
    }

    //cekiranje aerodroma u checkbox-u i ponovno crtanje na mapi
    public void postaviVidljivostAerodroma(String kod, boolean vidljiv){
        if(kod == null){
            return;
        }

        if (vidljiv) {
            skriveniAerodromi.remove(kod);

        }else{
            skriveniAerodromi.add(kod);
        }

        if(!vidljiv && selektovaniAerodrom != null && selektovaniAerodrom.getKod().equals(kod)){
            ponistiSelekciju();
        }

        repaint();

    }

    //prikaz svih aerodroma na mapi
    public void prikaziSveAerodrome(){
        skriveniAerodromi.clear();
        repaint();
    }

    //brisanje svih aerodroma sa mape
    public void sakrijSveAerodrome(){
        for(Aerodrom aerodrom : kontrola.getAerodromi()){
            skriveniAerodromi.add(aerodrom.getKod());
        }

        ponistiSelekciju();
        repaint();
    }

    //da li je validan klik misa na aerodrom (vidljiv aerodrom, unutar granica klika)
    private boolean klikJeNaAerodromu(Aerodrom aerodrom, int misX, int misY){
        if(aerodrom == null || !jeAerodromVidljiv(aerodrom.getKod())){
            return false;
        }

        int ekranX = pretvoriX(aerodrom.getX(),trenutniPocetakX,trenutnaSirinaMape);
        int ekranY = pretvoriY(aerodrom.getY(),trenutniPocetakY,trenutnaVisinaMape);

        int tolerancija = velicina/2 + 4;

        return Math.abs(misX - ekranX) <= tolerancija && Math.abs(misY - ekranY) <= tolerancija;

    }

    //dodavanje simulatora mapi
    public void postaviSimulator(SimulatorLetova simulator){
        this.simulator = simulator;
        repaint();
    }

    //zaustavljanje treperenja aerodroma
    public void zatvori(){
        tajmerTreperenja.cancel();
    }

    //crtanje pojedinacnih aviona na mapi
    private void nacrtajAvione(Graphics g){
        if(simulator == null){
            return;
        }

        //trenutno vreme simulacije
        double trenutnoVreme = simulator.getTrenutnoVremeUMinutima();

        for(SimulacijaLeta simulacija : simulator.getAktivniLetovi()){
            Let let = simulacija.getLet();

            Aerodrom polazni = let.getPoletanje();
            Aerodrom odredisni = let.getSletanje();

            //proteklo vreme
            double predjeno = simulacija.getNapredak(trenutnoVreme);

            int pocetniX = pretvoriX(polazni.getX(),trenutniPocetakX,trenutnaSirinaMape);
            int pocetniY = pretvoriY(polazni.getY(),trenutniPocetakY,trenutnaVisinaMape);

            int krajnjiX = pretvoriX(odredisni.getX(),trenutniPocetakX,trenutnaSirinaMape);
            int krajnjiY = pretvoriY(odredisni.getY(),trenutniPocetakY,trenutnaVisinaMape);

            //pozicija aviona u trenutku na mapi
            int trenutniX = (int) Math.round(pocetniX + predjeno * (krajnjiX - pocetniX));
            int trenutniY = (int) Math.round(pocetniY + predjeno * (krajnjiY - pocetniY));


            int velicinaAviona = 10;

            g.setColor(Color.BLUE);

            //crtanje kruga na XY poziciji
            g.fillOval(trenutniX - velicinaAviona/2,trenutniY-velicinaAviona/2,velicinaAviona,velicinaAviona);

        }

    }

    public boolean imaSelektovanAerodrom(){
        return selektovaniAerodrom != null;
    }

}
