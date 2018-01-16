import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;

/**
 * Created by jamal on 20/07/2017.
 */
class ChoiceBoxListener extends PanelControlListener implements ChangeListener<ChoiceEnum> {
    private ListView<Job> listJobView;
    private JobWrapper jobWrapper;
    private ChoiceBox<ChoiceEnum> parent;

    public ChoiceBoxListener(ChoiceBox parent, ListView<Job> listJobView, JobWrapper jobWrapper) {
        super(parent);
        this.parent = parent;
        this.listJobView = listJobView;
        this.jobWrapper = jobWrapper;
    }

    @Override
    public void changed(ObservableValue<? extends ChoiceEnum> observable, ChoiceEnum oldValue, ChoiceEnum newValue) {
        try {
            switch (parent.getId()) {
                case "editPanelCategory":
                    jobWrapper.getSelectedJob().setCat((Category) newValue);
                    break;
                case "editPanelPeriod":
                    jobWrapper.getSelectedJob().setPeriod((Period) newValue);
                    break;
            }
            listJobView.refresh();
        } catch (NullPointerException ignored) {
        }
    }
}
