import com.sun.javafx.collections.TrackableObservableList;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.*;
import java.util.List;

public class MainUIController implements Initializable {

    // FIELDS -----------------------------------------------

    private static Mode mode;
    private static ObservableList<Job> jobQueue = new TrackableObservableList<Job>() {
        @Override
        protected void onChanged(ListChangeListener.Change<Job> c) {
        }
    };
    private static JobWrapper jobWrapper;
    private static ScrapeController scrapeController;
    private static Boolean downloading = false;
    private Collection<Node> dodNodes = new HashSet<>();
    private Preferences preferences;

    // FXML FIELDS ------------------------------------------

    @FXML
    private Menu editMenu;
    @FXML
    private ToggleButton btnNew;
    @FXML
    private ToggleButton btnEdit;
    @FXML
    private ListView<Job> listJobQueue;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private VBox upperVbox;
    @FXML
    private VBox editPanel;
    @FXML
    private javafx.scene.control.TextField editPanelSubreddit;
    @FXML
    private javafx.scene.control.TextField editPanelNumber;
    @FXML
    private ChoiceBox editPanelCategory;
    @FXML
    private ChoiceBox editPanelPeriod;
    @FXML
    private Button submitJob;
    @FXML
    private Button btnDownload;
    @FXML
    private CheckMenuItem saveTitles;
    @FXML
    private Text downloadMessage;


    // CONSTRUCTOR AND INITIALIZER ---------------------------------------

    public MainUIController() {
        preferences = new Preferences();
        scrapeController = new ScrapeController(jobQueue, preferences);
        scrapeController.setOnRunning(event -> setDownloadingMode(true));
        scrapeController.setOnSucceeded(event -> setDownloadingMode(false));
        scrapeController.setOnCancelled(event -> setDownloadingMode(false));
        scrapeController.setOnFailed(event -> scrapeFailed());
        jobWrapper = new JobWrapper();
    }

    public void initialize(URL location, ResourceBundle resources) {
        setMode(Mode.CLEAR);
        listJobQueue.setItems(jobQueue);
        editPanelCategory.getItems().setAll(Category.values());
        editPanelPeriod.getItems().setAll(Period.values());
        progressBar.setPrefWidth(Double.MAX_VALUE);
        dodNodes.add(upperVbox);
        dodNodes.add(listJobQueue);
        progressBar.progressProperty().bind(scrapeController.progressProperty());
        downloadMessage.textProperty().bind(scrapeController.messageProperty());
        SubredditFieldListener subredditFieldListener = new SubredditFieldListener(editPanelSubreddit, listJobQueue, jobWrapper);
        NumFieldListener numFieldListener = new NumFieldListener(editPanelNumber, listJobQueue, jobWrapper);
        ChoiceBoxListener categoryListener = new ChoiceBoxListener(editPanelCategory, listJobQueue, jobWrapper);
        ChoiceBoxListener periodListener = new ChoiceBoxListener(editPanelPeriod, listJobQueue, jobWrapper);
        editPanelSubreddit.textProperty().addListener(subredditFieldListener);
        editPanelNumber.textProperty().addListener(numFieldListener);
        editPanelCategory.valueProperty().addListener(categoryListener);
        editPanelPeriod.valueProperty().addListener(periodListener);
    }

    // FXML Methods ------------------------------------------------------


    @FXML
    void deleteJobKey(ActionEvent event) {
        if (jobWrapper.getSelectedJob() != null) {
            jobQueue.remove(jobWrapper.getSelectedJob());
            getListSelected();
        }
    }

    @FXML
    void deleteJobButton(ActionEvent event) {
        setMode(Mode.CLEAR);
        if (jobWrapper.getSelectedJob() != null) {
            jobQueue.remove(jobWrapper.getSelectedJob());
        }
    }

    @FXML
    void menuPrefs(ActionEvent event) {
        switch (((MenuItem) event.getSource()).getId()) {
            case "saveTitles":
                if (saveTitles.isSelected()) {
                    preferences.set(PrefEnum.saveTitles, 1.0);
                } else {
                    preferences.set(PrefEnum.saveTitles, 0.0);
                }
                break;
        }
    }

    @FXML
    void buttonDownload(ActionEvent event) {
        if (!downloading) {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Save As");
            directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            File saveLoc = directoryChooser.showDialog(new Stage());
            try {
                scrapeController.setSaveLocation(saveLoc.getAbsolutePath() + "/");
                scrapeController.start();
            } catch (NullPointerException ignored) {
            } catch (Exception e) {
                errorMsg(e);
            }
        } else {
            scrapeController.cancel();
        }
    }


    @FXML
    void quit(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    void setModeAdd(ActionEvent event) {
        setMode(Mode.ADD);
    }

    @FXML
    void setModeEdit(ActionEvent event) {
        jobWrapper.setSelectedJob(listJobQueue.getFocusModel().getFocusedItem());
        setMode(Mode.EDIT);
    }

    @FXML
    void submitJob(ActionEvent event) {
        List<Exception> errors = new ArrayList<>();
        try {
            String inputSubreddit = editPanelSubreddit.getText().trim();
            String inputNum = editPanelNumber.getText().trim();
            Category inputCategory = (Category) editPanelCategory.getValue();
            Period inputPeriod = (Period) editPanelPeriod.getValue();

            if (inputSubreddit.equals("")) {
                errors.add(new BlankStringException());
            }
            if (!isInt(inputNum)) {
                errors.add(new NumberFormatException());
            }
            if (!errors.isEmpty()) {
                throw new RuntimeException();
            }
            int num = Integer.parseInt(inputNum);
            Job job = new Job(inputSubreddit, num, inputCategory, inputPeriod);
            jobQueue.add(job);
        } catch (RuntimeException e) {
            errorMsg(errors);
        }

    }

    @FXML
    void getListSelected() {
        if (listJobQueue.isFocused()) {
            jobWrapper.setSelectedJob(listJobQueue.getFocusModel().getFocusedItem());
            if (jobWrapper.getSelectedJob() != null) {
                setMode(Mode.EDIT);
            }
        }
    }

    // NON FXML Methods ------------------------------------------------

    private boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void setMode(Mode mode) {
        MainUIController.mode = mode;
        switch (mode) {
            case ADD:
                jobWrapper.setSelectedJob(null);
                btnEdit.setSelected(false);
                btnNew.setSelected(true);
                editPanel.setVisible(true);
                submitJob.setVisible(true);
                cleanEditPanel();
                break;
            case EDIT:
                if (jobWrapper.getSelectedJob() != null) {
                    btnEdit.setSelected(true);
                    btnNew.setSelected(false);
                    editPanel.setVisible(true);
                    submitJob.setVisible(false);
                    populateEditPanel();
                } else {
                    setMode(Mode.CLEAR);
                }
                break;
            case CLEAR:
                btnEdit.setSelected(false);
                btnNew.setSelected(false);
                editPanel.setVisible(false);
                cleanEditPanel();
        }
    }

    private void cleanEditPanel() {
        editPanelSubreddit.setText("");
        editPanelNumber.setText("");
        editPanelCategory.setValue(Category.TOP);
        editPanelPeriod.setValue(Period.DAY);
    }

    private void populateEditPanel() {
        editPanelSubreddit.setText(jobWrapper.getSelectedJob().getSubreddit());
        editPanelNumber.setText(String.valueOf(jobWrapper.getSelectedJob().getNum()));
        editPanelCategory.setValue(jobWrapper.getSelectedJob().getCat());
        editPanelPeriod.setValue(jobWrapper.getSelectedJob().getPeriod());
    }

    private void errorMsg(List<Exception> errors) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Oops!");
        alert.setHeaderText(null);
        String contentText = "Looks like something went wrong:\n\n";
        for (Exception e :
                errors) {
            if (e.getClass().equals(BlankStringException.class)) {
                contentText += "Enter a subreddit.\n";
            } else if (e.getClass().equals(NumberFormatException.class)) {
                contentText += "Enter a number of images to download.\n";
            }
        }
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    private void errorMsg(Exception e) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Oops!");
        alert.setHeaderText(null);
        String contentText = "Looks like something went wrong:\n\n";
        contentText += processError(e);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    private String processError(Exception e) {

        if (e.getClass().equals(BlankStringException.class)) {
            return "Enter a subreddit.\n";
        } else if (e.getClass().equals(NumberFormatException.class)) {
            return "Enter a number of images to download.\n";
        } else if (e.getClass().equals(UnknownHostException.class)) {
            return "Failed to connect to Reddit.\n";
        } else if (e.getClass().equals(IOException.class)) {
            return "Failed to save images. Try again or save to a new location.\n";
        } else return e.getMessage();
    }

    private void errorMsg() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Oops!");
        alert.setHeaderText(null);
        String contentText = "Looks like something went wrong:\n\n";
        alert.setContentText(contentText);
        alert.showAndWait();
    }


    private void setDownloadingMode(Boolean b) {
        if (b) {
            // Disable appropriate buttons
            progressBar.setVisible(true);
            downloadMessage.setVisible(true);
            downloading = true;
            editMenu.setDisable(true);
            for (Node node :
                    dodNodes) {
                node.setDisable(true);
            }
            jobWrapper.setSelectedJob(null);
            setMode(Mode.CLEAR);
            btnDownload.setText("Cancel");
        } else {
            // Enable appropriate buttons
            progressBar.setVisible(false);
            downloadMessage.setVisible(false);
            downloading = false;
            editMenu.setDisable(false);
            for (Node node :
                    dodNodes) {
                node.setDisable(false);
            }
            btnDownload.setText("Download");
        }
    }

    private void scrapeFailed() {
        setDownloadingMode(false);
        errorMsg((Exception) scrapeController.getException());
    }

}