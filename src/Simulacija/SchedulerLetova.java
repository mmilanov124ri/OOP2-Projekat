package Simulacija;

import Modeli.Let;

import java.time.LocalTime;
import java.util.*;

public class SchedulerLetova {
    private static final int termin = 10;

    private SchedulerLetova() {}

    public static List<SimulacijaLeta> napraviRaspored(List<Let> letovi) {
        if (letovi.isEmpty())
            throw new IllegalArgumentException(
                    "Lista letova je prazna"
            );

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

        sortiraniLetovi.sort(Comparator.comparingInt(SchedulerLetova::planiraniMinut).thenComparingInt(IndeksiraniLet::getId));


        Map<String, Integer> sledeciSlobodanTermin = new HashMap<>();

        List<SimulacijaLeta> rezultat = new ArrayList<>();

        for (IndeksiraniLet indeksiraniLet : sortiraniLetovi){
            Let let  = indeksiraniLet.getLet();

            int planiranoVreme = uMinute(let.getVreme());

            int najbliziTermin = zaokruziTermin(planiranoVreme);

            String kodAerodroma = let.getPoletanje().getKod();

            int slobodnoOd = sledeciSlobodanTermin.getOrDefault(kodAerodroma, 0);

            int realniPolazak = Math.max(najbliziTermin, slobodnoOd);

            rezultat.add(new SimulacijaLeta(let, realniPolazak));

            sledeciSlobodanTermin.put(kodAerodroma,realniPolazak + termin);

            rezultat.sort(Comparator.comparingInt(SimulacijaLeta::getPolazakMinuti));

        }

        return rezultat;

    }

    private static int planiraniMinut(IndeksiraniLet indeksiraniLet){
        return uMinute(indeksiraniLet.getLet().getVreme());
    }

    private static int uMinute(LocalTime vreme){
        if(vreme == null){
            throw new IllegalArgumentException(
                    "Nevalidno vreme leta"
            );
        }

        return vreme.getHour() * 60 + vreme.getMinute();
    }

    private static int zaokruziTermin(int minut){
        return ((minut + termin - 1)/termin * termin);
    }


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