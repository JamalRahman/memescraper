/**
 * Created by jamal on 20/07/2017.
 */
class JobWrapper {
    private Job selectedJob;

    public JobWrapper() {
        this.selectedJob = null;
    }

    public Job getSelectedJob() {
        return selectedJob;
    }

    public void setSelectedJob(Job selectedJob) {
        this.selectedJob = selectedJob;
    }
}
