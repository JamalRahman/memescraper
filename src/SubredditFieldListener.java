import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

/**
 * Created by jamal on 19/07/2017.
 */
class SubredditFieldListener extends PanelControlListener implements ChangeListener<String> {

    private ListView<Job> listJobQueue;
    private final JobWrapper jobWrapper;

    public SubredditFieldListener(TextField parent, ListView<Job> listJobQueue, JobWrapper jobWrapper) {
        super(parent);
        this.listJobQueue = listJobQueue;
        this.jobWrapper = jobWrapper;
    }

    @Override
    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        if (newValue.trim().equals("")) {
            setHighlighted(true);
        } else {
            setHighlighted(false);
        }
        try {
            jobWrapper.getSelectedJob().setSubreddit(newValue.trim());
            listJobQueue.refresh();
        } catch (NullPointerException ignored) {
        }
    }
}
