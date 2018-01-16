/**
 * Created by jamal on 07/07/2017.
 */
public enum Period implements ChoiceEnum {
    HOUR, DAY, WEEK, MONTH, YEAR, ALL;

    @Override
    public String toString() {
        String s = super.toString();
        return s.substring(0, 1) + s.substring(1).toLowerCase();

    }
}
