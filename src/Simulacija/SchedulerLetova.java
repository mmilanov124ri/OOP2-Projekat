package Simulacija;

import Modeli.Let;

import java.time.LocalTime;
import java.util.*;

public class SchedulerLetova {

    //najmanji razmak izmedju poletanja dva aviona sa istog aerodroma
    private static final int termin = 10;

    private SchedulerLetova() {}

    //kreiranje rasporeda letova simulacije
    public static List<SimulacijaLeta> napraviRaspored(List<Let> letovi) {
        if (letovi == null){
            throw new IllegalArgumentException(
                    "Lista letova je prazna"
            );
        }

        if(letovi.isEmpty()){
            return new ArrayList<>();
        }

        List<IndeksiraniLet> sortiraniLetovi = new ArrayList<>();

        for (int i = 0; i < letovi.size(); i++) {
            Let let = letovi.get(i);

            if (let == null) {
                throw new IllegalArgumentException(
                        "Nevalidan let"
                );
            }

            sortiraniLetovi.add(new IndeksiraniLet(let, i));
        }

        //sortiranje letova po kriterijumu : 1. Vreme poletanja, 2. ID leta (ID se dodeljuje preko redosleda u listi)
        sortiraniLetovi.sort(Comparator.comparingInt(SchedulerLetova::planiraniMinut).thenComparingInt(IndeksiraniLet::getId));

        Map<String, Integer> sledeciSlobodanTermin = new HashMap<>();

        List<SimulacijaLeta> rezultat = new ArrayList<>();

        for (IndeksiraniLet indeksiraniLet : sortiraniLetovi){
            Let let  = indeksiraniLet.getLet();

            int planiranoVreme = uMinute(let.getVreme());

            String kodAerodroma = let.getPoletanje().getKod();

            int slobodnoOd = sledeciSlobodanTermin.getOrDefault(kodAerodroma, 0);

            int realniPolazak = Math.max(planiranoVreme,slobodnoOd);

            rezultat.add(new SimulacijaLeta(let, realniPolazak));

            sledeciSlobodanTermin.put(kodAerodroma,realniPolazak + termin);

        }

        //sortiranje letova po vremenu poletanja
        rezultat.sort(Comparator.comparingInt(SimulacijaLeta::getPolazakMinuti));

        return rezultat;

    }

    //vreme poletanja datog leta
    private static int planiraniMinut(IndeksiraniLet indeksiraniLet){
        return uMinute(indeksiraniLet.getLet().getVreme());
    }

    //konverzija vremena u minute
    private static int uMinute(LocalTime vreme){
        if(vreme == null){
            throw new IllegalArgumentException(
                    "Nevalidno vreme leta"
            );
        }

        return vreme.getHour() * 60 + vreme.getMinute();
    }

    //pomocna klasa dodele ID letovima
    private static class IndeksiraniLet {
        private final Let let;
        private final int id;

        private IndeksiraniLet(Let let, int id) {
            this.let = let;
            this.id = id;
        }

        private Let getLet() {
            return let;
        }

        private int getId() {
            return id;
        }
    }
}