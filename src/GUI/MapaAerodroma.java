package GUI;

import Modeli.Aerodrom;
import Modeli.KontrolaLeta;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.TimerTask;
import java.util.Timer;
import java.util.HashSet;
import java.util.Set;


public class MapaAerodroma extends Canvas {
    private static final int margina = 20;
    private static final int velicina = 12;

    private final KontrolaLeta kontrola;

    private final MenadzerNeaktivnosti neaktivnosti;

    private Aerodrom selektovaniAerodrom;
    private boolean prikazCrveno = true;

    private final Set<String> skriveniAerodromi = new HashSet<>();

    private int trenutniPocetakX;
    private int trenutniPocetakY;
    private int trenutnaSirinaMape;
    private int trenutnaVisinaMape;

    private final Timer tajmerTreperenja;

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
    }

    private void nacrtajOkvirMape(Graphics g, int pocetakX, int pocetakY, int sirinaMape, int visinaMape) {
        g.setColor(Color.LIGHT_GRAY);

        g.drawRect(margina, margina, sirinaMape, visinaMape);


        int ekranX0 = pretvoriX(0, pocetakX, sirinaMape);

        g.drawLine(ekranX0,pocetakY,ekranX0,pocetakY + visinaMape);

        int ekranY0 = pretvoriY(0, pocetakY, visinaMape);

        g.drawLine(pocetakX,ekranY0,pocetakX + sirinaMape,ekranY0);
    }

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

    private int pretvoriX(int x,int pocetakX, int sirinaMape) {
        double odnos = (x+180.0) / 360.0;
        return pocetakX + (int) Math.round(odnos * sirinaMape);
    }


    private int pretvoriY(int y, int pocetakY, int visinaMape) {
        double odnos = (90.0 - y) / 180.0;

        return pocetakY + (int) Math.round(odnos * visinaMape);
    }

    public void osveziMapu(){
        repaint();
    }

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

    public void ponistiSelekciju(){

        selektovaniAerodrom = null;
        prikazCrveno = true;

        neaktivnosti.nastavi();
        repaint();
    }

    public boolean jeAerodromVidljiv(String kod){
        return !skriveniAerodromi.contains(kod);
    }

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

    public void prikaziSveAerodrome(){
        skriveniAerodromi.clear();
        repaint();
    }

    public void sakrijSveAerodrome(){
        for(Aerodrom aerodrom : kontrola.getAerodromi()){
            skriveniAerodromi.add(aerodrom.getKod());
        }

        ponistiSelekciju();
        repaint();
    }

    private boolean klikJeNaAerodromu(Aerodrom aerodrom, int misX, int misY){
        if(aerodrom == null || !jeAerodromVidljiv(aerodrom.getKod())){
            return false;
        }

        int ekranX = pretvoriX(aerodrom.getX(),trenutniPocetakX,trenutnaSirinaMape);
        int ekranY = pretvoriY(aerodrom.getY(),trenutniPocetakY,trenutnaVisinaMape);

        int tolerancija = velicina/2 + 4;

        return Math.abs(misX - ekranX) <= tolerancija && Math.abs(misY - ekranY) <= tolerancija;

    }
}
