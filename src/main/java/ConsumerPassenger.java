import java.util.ArrayList;

/**
 * Created by Evgenij on 21.06.2017.
 */
public class ConsumerPassenger {
    public void startCall(ArrayList<Passenger> arrayPassenger){
        for (Passenger passenger :
                arrayPassenger ) {
            passenger.start();
        }
    }
}
