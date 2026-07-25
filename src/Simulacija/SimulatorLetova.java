package Simulacija;

import Modeli.Let;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class SimulatorLetova {
    private static final double minutPoKoraku = 2.0;
    private static final long periodMS = 200;

    private final List<SimulacijaLeta> raspored;
    private final Runnable promena;

    private final ScheduledExecutorService izvrsilac;
    private ScheduledFuture<?> zadatak;

    private double trenutnoVremeUMinutima;

    public SimulatorLetova(List<SimulacijaLeta> raspored, Runnable promena) {
        if(raspored == null){
            throw new IllegalArgumentException(
                    "Nevalidan raspored"
            );
        }

        this.raspored = new ArrayList<>(raspored);
        this.promena = promena;
        this.trenutnoVremeUMinutima = 0.0;

        ThreadFactory fabrikaNiti = posao ->{
            Thread t = new Thread(posao, "Simulator letova");

            t.setDaemon(true);
            return t;
        };

        izvrsilac = Executors.newSingleThreadScheduledExecutor(fabrikaNiti);

    }

    public synchronized void start() {
        if(zadatak != null && !zadatak.isDone()){
            return;
        }

        zadatak = izvrsilac.scheduleAtFixedRate(this::izvrsiKorak, periodMS,periodMS, TimeUnit.MILLISECONDS);
    }

    public synchronized void pauza() {
        if(zadatak == null){
            return;
        }
        zadatak.cancel(false);
        zadatak = null;
    }

    public void reset(){
        pauza();

        synchronized (this) {
            trenutnoVremeUMinutima = 0.0;
        }

        obavestiPromenu();
    }

    private void izvrsiKorak(){
        synchronized (this) {
            trenutnoVremeUMinutima += minutPoKoraku;
        }

        obavestiPromenu();
    }

    public synchronized double getTrenutnoVremeUMinutima(){
        return trenutnoVremeUMinutima;
    }

    public synchronized boolean jePokrenut(){
        return zadatak != null && !zadatak.isDone();
    }

    public synchronized List<SimulacijaLeta> getAktivniLetovi(){
        List<SimulacijaLeta> aktivniLetovi = new ArrayList<>();

        for(SimulacijaLeta l : raspored){
            if(l.jeULetu(trenutnoVremeUMinutima)){
                aktivniLetovi.add(l);
            }
        }

        return aktivniLetovi;

    }

    public synchronized String getFormatiranoVreme(){
        int ukupnoMinuta = (int) Math.floor(trenutnoVremeUMinutima);

        int sati = ((ukupnoMinuta) / 60) % 24;
        int minuti = (ukupnoMinuta % 60);

        return String.format("%02d:%02d",sati,minuti);

    }

    public void obavestiPromenu(){
        if(promena == null){
            return;
        }

        EventQueue.invokeLater(promena);
    }

    public void zatvori(){
        pauza();
        izvrsilac.shutdownNow();
    }


}
