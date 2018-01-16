/**
 * Created by jamal on 07/07/2017.
 */
class Job {

    private String subreddit;
    private int num;
    private Category cat;
    private Period period;

    public Job(String subreddit, int num, Category cat, Period period) {
        this.subreddit = subreddit;
        this.num = num;
        this.cat = cat;
        this.period = period;
    }

    public void setTask(String subreddit, int num, Category cat, Period period) {
        this.subreddit = subreddit;
        this.num = num;
        this.cat = cat;
        this.period = period;
    }


    // GET METHODS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public String getSubreddit() {
        return subreddit;
    }

    public int getNum() {
        return num;
    }

    public Category getCat() {
        return cat;
    }

    public Period getPeriod() {
        return period;
    }

    // SET METHODS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public void setSubreddit(String subreddit) {
        this.subreddit = subreddit;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public void setCat(Category cat) {
        this.cat = cat;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    // OTHER METHODS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public String toString() {
        return "/r/" + subreddit + ", " + String.valueOf(num) + ", " + String.valueOf(cat) + ", " + String.valueOf(period);
    }
}
