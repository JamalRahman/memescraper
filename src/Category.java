/**
 * Created by jamal on 07/07/2017.
 */

public enum Category implements ChoiceEnum {
    TOP, HOT, RISING, CONTROVERSIAL, NEW;

    @Override
    public String toString() {
        String s = super.toString();
        return s.substring(0, 1) + s.substring(1).toLowerCase();
    }
}