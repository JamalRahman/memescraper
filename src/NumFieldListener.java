import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

/**
 * Created by jamal on 19/07/2017.
 */
class NumFieldListener extends PanelControlListener implements ChangeListener<String> {
    private ListView<Job> listJobQueue;
    private final JobWrapper jobWrapper;

    public NumFieldListener(TextField parent, ListView<Job> listJobQueue, JobWrapper jobWrapper) {
        super(parent);
        this.listJobQueue = listJobQueue;
        this.jobWrapper = jobWrapper;
    }

    @Override
    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        try {
            Integer.parseInt(newValue.trim());
            setHighlighted(false);
            try {
                jobWrapper.getSelectedJob().setNum(Integer.parseInt(newValue.trim()));
                listJobQueue.refresh();
            } catch (NullPointerException ignored) {
            }
        } catch (NumberFormatException e) {
            setHighlighted(true);
            try {
                jobWrapper.getSelectedJob().setNum(0);
                listJobQueue.refresh();
            } catch (NullPointerException ignored) {
            }
        }
    }
}
