import java.util.HashMap;

/**
 * Created by jamal on 11/07/2017.
 */
class Preferences {
    private HashMap<PrefEnum, Double> keyPairs;

    public Preferences(){
        keyPairs = new HashMap<>();
        keyPairs.put(PrefEnum.saveTitles,0.0);
    }

    public void set(PrefEnum key, Double value){
        keyPairs.replace(key, value);
    }

    public Double get(PrefEnum key){
        return keyPairs.get(key);
    }
}

enum PrefEnum{saveTitles}
