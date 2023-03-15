import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Tauler implements Serializable {
    public Map<String,Integer> map_jugadors;
    public int resultat = 3, acabats;
    private int numPlayers;
    private boolean finJoc;

    public Tauler() {
        map_jugadors = new HashMap<>();
        acabats = 0;
        finJoc = false;
    }
    public int getNumPlayers() {
        return numPlayers;
    }

    public void addNUmPlayers() {
        this.numPlayers++;
    }

    public boolean isFinJoc() {
        return finJoc;
    }

    public void setFinJoc(boolean finJoc) {
        this.finJoc = finJoc;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        //sb.append("Intents\n");
        map_jugadors.forEach((k,v) -> sb.append(k + " - " + v + "\n"));
        if (finJoc) {
            sb.append("Joc finalitzat. El número era: " + resultat + "\n");
        }
        return sb.toString();
    }
}

class Jugada implements Serializable {
    String Nom;
    int num;
}