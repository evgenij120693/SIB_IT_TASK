package ru.svetozarov;

import ru.svetozarov.Lift.ILift;
import ru.svetozarov.Lift.Lift;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Evgenij on 21.06.2017.
 */
public class ProducerForHome implements ProjectConstants {
    public ArrayList<Passenger> createPassenger(int countPassenger, int countFloor, ILift lift) {
        ArrayList<Passenger> arrayPassenger = new ArrayList<>(countPassenger);
        Random random = new Random();
        int startFloor;
        int finalFloor;
        while (countPassenger != 0) {
            startFloor = random.nextInt(countFloor - 1) + 1;
            do {
                finalFloor = random.nextInt(countFloor - 1) + 1;
            } while (finalFloor == startFloor);
            System.out.println("start floor "+startFloor+", final floor "+finalFloor);
            arrayPassenger.add(new Passenger(startFloor, finalFloor, lift));
            countPassenger--;
        }
        return arrayPassenger;
    }

    public ILift createLift(String typeLift, int countFloor) {
        if (typeLift == PASSENGER_LIFT)
            return new Lift(new ElectricMotor(TIME_UP_FOR_PASSENGER_LIFT, TIME_DOWN_FOR_PASSENGER_LIFT,
                    TIME_STOP_FOR_PASSENGER_LIFT), countFloor, typeLift);
        else
            return new Lift(new ElectricMotor(TIME_UP_FOR_FREIGHT_LIFT, TIME_DOWN_FOR_FREIGHT_LIFT,
                    TIME_STOP_FOR_FREIGHT_LIFT), countFloor, typeLift);
    }
}
