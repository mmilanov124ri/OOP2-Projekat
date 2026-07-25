package Simulacija;

import Modeli.Let;


public class SimulacijaLeta {

    private final Let let;

    private final int polazakMinuti;

    public SimulacijaLeta(Let let, int polazakMinuti) {
        if(let == null){
            throw new IllegalArgumentException(
                    "Nevalidan let"
            );
        }
        if(polazakMinuti < 0){
            throw new IllegalArgumentException(
                    "Nevalidno vreme polaska"
            );
        }

        this.let = let;
        this.polazakMinuti = polazakMinuti;

    }

    public Let getLet() {
        return let;
    }

    public int getPolazakMinuti() {
        return polazakMinuti;
    }

    public int getKrajLetaMinuti() {
        return polazakMinuti + let.getTrajanje();
    }

    public boolean jeULetu(double trenutnoVreme){
        return trenutnoVreme >= polazakMinuti && trenutnoVreme < getKrajLetaMinuti();
    }

    public boolean jeZavrsen(double trenutnoVreme){
        return trenutnoVreme >= getKrajLetaMinuti();
    }

    public double getNapredak(double trenutnoVreme){
        if(trenutnoVreme <= polazakMinuti){
            return 0.0;
        }
        if(trenutnoVreme >= getKrajLetaMinuti()){
            return 1.0;
        }
        return (trenutnoVreme - polazakMinuti) / (double) let.getTrajanje();
    }

}
