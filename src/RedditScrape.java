import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * This task accesses reddit's json API, decodes the json and downloads an image for any post
 * that points to an image link.
 * <p>
 * For use with the ScraperController subclass of Service.
 */

class RedditScrape extends Task<Void> {
    private static final String ua = "Memescraper.net | Windows:memescraper:v1.0.0 | /u/MightyLemur";
    private final Double saveTitles;
    private URLConnection imageRequest;
    private String type;
    private final ObservableList<Job> jobQueue;
    private final String saveLocation;
    private int totalDownloads;
    private int currentlyDownloaded;

    /**
     * Constructs a Scrape object with a list of Jobs and a location to save the downloaded images.
     *
     * @param jobQueue     The queue of Job items, which each describe a subreddit to crawl
     * @param saveLocation The location in which to save images
     * @param saveTitles
     */
    public RedditScrape(ObservableList<Job> jobQueue, String saveLocation, Double saveTitles) {
        this.jobQueue = jobQueue;
        this.saveLocation = saveLocation;
        this.saveTitles = saveTitles;
        totalDownloads = 0;
        for (Job job :
                jobQueue) {
            totalDownloads += job.getNum();
        }
    }

    @Override
    protected Void call() throws Exception {
        currentlyDownloaded = 0;
        updateProgress(0, totalDownloads);
        updateMessage("Sending reddit API request");
        for (Job job : jobQueue) {
            if (isCancelled()) break;
            executeScrape(job);
        }
        return null;
    }

    /**
     * Makes one query to Reddit's json api, scraping a particular selection of posts and downloading
     * all images within the selection of posts.
     *
     * @param job The individual job description on which this method downloads
     *            all images from the specified post criteria.
     * @throws IOException A connection is opened up to a URL, which can throw an exception.
     */
    private void executeScrape(Job job) throws IOException {
        String sUrl = constructURL(job);
        URL url = new URL(sUrl);

        HttpURLConnection request = (HttpURLConnection) url.openConnection();
        request.setRequestProperty("User-Agent", ua);
        request.connect();
        int num = job.getNum();

        JsonParser jp = new JsonParser();
        JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
        JsonObject fullJson = root.getAsJsonObject();
        // Check if Json array contains less than num
        int jsonChildNumber = fullJson.get("data").getAsJsonObject().get("children").getAsJsonArray().size();
        if (jsonChildNumber < num) {
            int differential = num - jsonChildNumber;
            totalDownloads -= differential;
            num = jsonChildNumber;
            // LOG json array < requested num
        }

        // Iterates through every post within the large JSON object.
        for (int i = 0; i < num; i++) {
            if (isCancelled()) break;
            JsonObject currentPost = fullJson.get("data").getAsJsonObject()
                    .get("children").getAsJsonArray()
                    .get(i).getAsJsonObject()
                    .get("data").getAsJsonObject();

            String currentImgUrl = currentPost.get("url").getAsString();

            // .gifv URLS are hosted as web-videos, turn into .gifs (where possible)

            if (currentImgUrl.endsWith(".gifv")) {
                currentImgUrl = currentImgUrl.substring(0, currentImgUrl.length() - 1);
            }

            URL imageUrl = new URL(currentImgUrl);

            //If the reddit post's link is to an image then save it

            if (isImg(currentPost, imageUrl)) {
                String fileType = type.substring(6);
                String subreddit = job.getSubreddit();
                String savePath = saveLocation + subreddit + "/";
                String fileName;
                updateMessage("Downloading "+fileType+": "+currentlyDownloaded+"/"+totalDownloads);
                if (saveTitles == 1.0) {
                    fileName = String.valueOf(i + 1) + "_" + currentPost.get("title").getAsString();
                    fileName = fileName.replaceAll("[^a-zA-Z0-9_\\\\-\\\\.]","_");
                } else {
                    fileName = String.valueOf(i + 1);
                }

                if (Files.notExists(Paths.get(savePath))) {
                    Files.createDirectories(Paths.get(savePath));
                }

                InputStream in = imageRequest.getInputStream();

                Files.copy(in, Paths.get(savePath + fileName + "." + fileType), StandardCopyOption.REPLACE_EXISTING);
                in.close();

            } else {
                //TODO: Log text posts
            }
            currentlyDownloaded++;
            updateProgress(currentlyDownloaded, totalDownloads);
        }
    }

    /**
     * Turns a Job object into a URL which perfectly queries reddit's API for a json representation of the Job's
     * requirements.
     *
     * @param job The job object that is having a reddit api query constructed for it.
     * @return The full reddit json query
     */
    private String constructURL(Job job) {
        String url = "https://reddit.com/r/";
        url = url.concat(job.getSubreddit() + "/" + String.valueOf(job.getCat()) + "/.json?sort=" + String.valueOf(job.getCat()) +
                "&t=" + String.valueOf(job.getPeriod()) +
                "&limit=" + String.valueOf(job.getNum()));
        return url.toLowerCase();
    }

    /**
     * When given a reddit post in JSON form, and it's link URL, finds if the link is to an image or something else.
     *
     * @param currentPost The JSON object that represents a single reddit post's data
     * @param imageUrl    The URL that the currentPost links to
     * @return True if the URL points to an image, False if otherwise.
     * @throws IOException Thrown by contentTypeIsImage()
     */
    private Boolean isImg(JsonObject currentPost, URL imageUrl) throws IOException {
        return !currentPost.get("is_self").getAsBoolean() && contentTypeIsImage(imageUrl);
    }

    /**
     * Helper method for isImg. Reads HTML Headers for "Content-Type" attribute.
     *
     * @param imageUrl The URL that the currentPost links to
     * @return True if the Content-Type is image. False otherwise
     * @throws IOException If an I/O Exception occur in URL.openConnection()
     */
    private boolean contentTypeIsImage(URL imageUrl) throws IOException {
        imageRequest = imageUrl.openConnection();
        imageRequest.setRequestProperty("User-Agent", ua);
        type = imageRequest.getHeaderField("Content-Type");
        return type.contains("image");

    }
}


