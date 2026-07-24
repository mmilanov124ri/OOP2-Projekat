package GUI;

import Modeli.Aerodrom;
import Modeli.KontrolaLeta;

import javax.swing.*;
import java.awt.*;


public class MapaAerodroma extends Canvas {
    private static final int margina = 20;
    private static final int velicina = 12;

    private final KontrolaLeta kontrola;

    public MapaAerodroma(KontrolaLeta kontrola) {
        if (kontrola == null) {
            throw new IllegalArgumentException(
                    "Nevalidna kontrola leta"
            );
        }
        this.kontrola = kontrola;

        setBackground(Color.WHITE);
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

        nacrtajOkvirMape(g, pocetakX, pocetakY,sirinaMape, visinaMape);

        for (Aerodrom aerodrom : kontrola.getAerodromi()) {
            nacrtajAerodrom(g, aerodrom, pocetakX, pocetakY, sirinaMape, visinaMape);
        }
    }

    private void nacrtajOkvirMape(Graphics g, int pocetakX, int pocetakY, int sirinaMape, int visinaMape) {
        g.setColor(Color.LIGHT_GRAY);

        g.drawRect(margina, margina, sirinaMape, visinaMape);


        int ekranX0 = pretvoriX(0, pocetakX, sirinaMape);

        g.drawLine(ekranX0,margina,ekranX0,margina + visinaMape);

        int ekranY0 = pretvoriY(0, pocetakY, visinaMape);

        g.drawLine(ekranY0,margina,ekranY0,margina + visinaMape);
    }

    private void nacrtajAerodrom(Graphics g,Aerodrom aerodrom, int pocetakX, int pocetakY, int sirinaMape, int visinaMape) {
        int ekranX = pretvoriX(aerodrom.getX(), pocetakX, sirinaMape);
        int ekranY = pretvoriY(aerodrom.getY(), pocetakY, sirinaMape);

        int polaVelicine = velicina / 2;

        g.setColor(Color.GRAY);

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

}
