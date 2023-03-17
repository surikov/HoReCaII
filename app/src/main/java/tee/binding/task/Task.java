package tee.binding.task;

/**
 *
 * @author User
 */
public abstract class Task {

    /**
     *
     */
    private boolean lock = false;

    public void start() {
        if (!lock) {
            lock = true;
            doTask();
            lock = false;
        }
    }

    /**
     *
     */
    public abstract void doTask();
}
