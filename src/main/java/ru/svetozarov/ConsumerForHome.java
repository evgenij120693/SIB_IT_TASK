package ru.svetozarov;

import org.apache.log4j.Logger;
import ru.svetozarov.Lift.ILift;
import ru.svetozarov.Lift.Lift;

import java.util.ArrayList;

/**
 * Created by Evgenij on 21.06.2017.
 */
public class ConsumerForHome {
    private static Logger logger = Logger.getLogger(ConsumerForHome.class);
    public void startLift(ILift firstLift, ILift secondLift){
        Thread thread1 = new Thread(firstLift);
        Thread thread2 = new Thread(secondLift);
        thread1.start();
        thread2.start();
        logger.trace("Лифты включены.");
    }

    public void startPassenger(ArrayList<Passenger> arrayPassenger){
        for (Passenger passenger :
                arrayPassenger ) {

            passenger.start();
        }
    }
}
