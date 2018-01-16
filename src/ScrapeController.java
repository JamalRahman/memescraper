import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

/**
 * Created by jamal on 09/07/2017.
 */
class ScrapeController extends Service<Void> {
    private final ObservableList<Job> jobQueue;
    private String saveLocation;
    private Preferences preferences;

    public ScrapeController(ObservableList<Job> jobQueue, Preferences preferences) {
        this.jobQueue = jobQueue;
        this.preferences = preferences;
    }

    public void setSaveLocation(String saveLocation) {
        this.saveLocation = saveLocation;
    }

    @Override
    protected void succeeded() {
        super.succeeded();
        reset();

    }

    @Override
    protected void cancelled() {
        super.cancelled();
        reset();
    }

    @Override
    protected void failed() {
        super.failed();
        reset();
    }

    public Task<Void> createTask() {
        final String _saveLocation = saveLocation;
        final Double saveTitles = preferences.get(PrefEnum.saveTitles);
        return new RedditScrape(jobQueue, _saveLocation, saveTitles);
    }
}
