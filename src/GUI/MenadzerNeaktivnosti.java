package GUI;

import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class MenadzerNeaktivnosti {
    private final Frame roditelj;

    private final long limitMilisekundi;
    private final int sekundeUpozorenja;

    private final ScheduledExecutorService izvrsilac;
    private final AtomicBoolean gasenjePokrenuto;

    private volatile long poslednjaAktivnost;
    private volatile boolean pauziran;
    private volatile boolean upozorenjePrikazano;

    private Dialog dijalogUpozorenja;
    private Label tekstOdbrojavanja;

    private final AWTEventListener osluskivanjeAktivnosti;

    //kreiranje tajmera neaktivnosti korisnika
    public MenadzerNeaktivnosti(Frame roditelj, int limitMilisekundi, int sekundeUpozorenja) {
        if(roditelj == null){
            throw new IllegalArgumentException(
                    "Nevalidan roditeljski prozor"
            );
        }

        if(limitMilisekundi < 0){
            throw new IllegalArgumentException(
                    "Limit neaktivnosti je manji od 0"
            );
        }

        if(sekundeUpozorenja <= 0 || sekundeUpozorenja * 1000L >= limitMilisekundi){
            throw new IllegalArgumentException(
                    "Vreme upozorenja nevalidno"
            );
        }

        this.roditelj = roditelj;
        this.limitMilisekundi = limitMilisekundi;
        this.sekundeUpozorenja = sekundeUpozorenja;

        poslednjaAktivnost = System.currentTimeMillis();

        izvrsilac = Executors.newSingleThreadScheduledExecutor(
                zadatak -> {
                    Thread nit = new Thread(zadatak, "Provera neaktivnosti");

                    nit.setDaemon(true);
                    return nit;
                }
        );

        gasenjePokrenuto = new AtomicBoolean(false);

        osluskivanjeAktivnosti = dogadjaj -> {
            if(!pauziran && !upozorenjePrikazano) {
                resetuj();
            }
        };

    }

    //pokretanje tajmera
    public void pokreni(){
        long maskaDogadjaja = AWTEvent.KEY_EVENT_MASK
                | AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK
                | AWTEvent.MOUSE_WHEEL_EVENT_MASK | AWTEvent.ACTION_EVENT_MASK
                | AWTEvent.ITEM_EVENT_MASK | AWTEvent.TEXT_EVENT_MASK;

        Toolkit.getDefaultToolkit().addAWTEventListener(osluskivanjeAktivnosti, maskaDogadjaja);

        izvrsilac.scheduleAtFixedRate(this::proveriNeaktivnost,0,1, TimeUnit.SECONDS);

    }

    //provera tajmera i eventualno resetovanje ili gasenje programa
    private void proveriNeaktivnost(){
        if(pauziran || gasenjePokrenuto.get()){
            return;
        }

        long proteklo = System.currentTimeMillis() - poslednjaAktivnost;

        long preostaloMilisekundi = limitMilisekundi - proteklo;

        long preostaloSekundi = (preostaloMilisekundi + 999) / 1000;

        if(preostaloMilisekundi <= 0){
            pokreniGasenje();
            return;
        }

        if(preostaloSekundi <= sekundeUpozorenja){
            if(!upozorenjePrikazano) {
                upozorenjePrikazano = true;

                EventQueue.invokeLater(() ->
                    prikaziUpozorenje((int) preostaloSekundi));
                };
            }else{
                EventQueue.invokeLater(() -> osveziOdbrojavanje((int) preostaloSekundi));

            }
    }

    //prikaz upozorenja pred gasenje programa
    private void prikaziUpozorenje(int preostaloSekundi){
        if(!roditelj.isDisplayable()){
           return;
        }
        dijalogUpozorenja = new Dialog(roditelj, "Upozorenje", true);

        dijalogUpozorenja.setLayout(new BorderLayout(10 ,10));

        tekstOdbrojavanja = new Label(napraviTekst(preostaloSekundi), Label.CENTER);

        Button buttonNastavi = new Button ("Nastavi rad");

        Panel panelDugmeta = new Panel(new FlowLayout(FlowLayout.CENTER));

        panelDugmeta.add(buttonNastavi);

        dijalogUpozorenja.add(tekstOdbrojavanja,BorderLayout.CENTER);

        dijalogUpozorenja.add(panelDugmeta,BorderLayout.SOUTH);

        buttonNastavi.addActionListener(e -> nastaviRad());

        dijalogUpozorenja.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e){
                nastaviRad();
            }
        });


        dijalogUpozorenja.setSize(480, 150);
        dijalogUpozorenja.setResizable(false);
        dijalogUpozorenja.setLocationRelativeTo(roditelj);
        dijalogUpozorenja.setVisible(true);

    }

    //refresh tajmera
    private void osveziOdbrojavanje(int preostaloSekundi){
        if(tekstOdbrojavanja != null){
            tekstOdbrojavanja.setText(napraviTekst(preostaloSekundi));
        }
    }

    //kreiranje teksta upozorenja
    private String napraviTekst(int preostaloSekundi){
        return "Program ce se zatvoriti za " + preostaloSekundi + " sekundi.";
    }

    //resetovanje tajmera i nastavak rada
    private void nastaviRad(){
        resetuj();
        upozorenjePrikazano = false;

        if(dijalogUpozorenja != null){
            dijalogUpozorenja.dispose();
            dijalogUpozorenja = null;
            tekstOdbrojavanja = null;
        }
    }

    //vracanje tajmera na pocetnu duzinu
    public void resetuj(){
        poslednjaAktivnost = System.currentTimeMillis();
    }

    //pauziranje tajmera neaktivnosti
    public void pauziraj(){
        pauziran = true;

        if(dijalogUpozorenja != null){
            dijalogUpozorenja.dispose();
            dijalogUpozorenja = null;
            tekstOdbrojavanja = null;
        }
        upozorenjePrikazano = false;
    }

    //ponovno pustanje tajmera
    public void nastavi(){
        pauziran = false;
        resetuj();
    }

    //gasenje programa
    private void pokreniGasenje(){
        if(!gasenjePokrenuto.compareAndSet(false, true)){
            return;
        }
        EventQueue.invokeLater(() -> {
            if (dijalogUpozorenja != null) {
                dijalogUpozorenja.dispose();
            }
            zaustavi();
            roditelj.dispose();
            System.exit(0);
        });
    }

    //gasenje tajmera neaktivnosti
    public void zaustavi(){
        Toolkit.getDefaultToolkit().removeAWTEventListener(osluskivanjeAktivnosti);

        izvrsilac.shutdown();

    }


}
