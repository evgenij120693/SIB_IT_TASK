import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Evgenij on 19.06.2017.
 */
public class ElectricMotor implements IElectricMotor {
    private int timeUp;
    private int timeDown;
    private int timeStop;

    public ElectricMotor(int timeUp, int timeDown, int timeStop) {
        this.timeUp = timeUp;
        this.timeDown = timeDown;
        this.timeStop = timeStop;
    }

    public void downLift() {
        try {
            Thread.sleep(timeDown);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void upLift() {
        try {
            Thread.sleep(timeUp);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void stopLift() {
        try {
            Thread.sleep(timeStop);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }




}
