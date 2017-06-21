package ru.svetozarov.Home;

import ru.svetozarov.ConsumerForHome;
import ru.svetozarov.Exception.ExceptionCountFloor;
import ru.svetozarov.Lift.ILift;
import ru.svetozarov.Passenger;
import ru.svetozarov.ProducerForHome;
import ru.svetozarov.ProjectConstants;

import java.util.ArrayList;

/**
 * Created by Evgenij on 21.06.2017.
 */
public class Home implements ProjectConstants {
    private ProducerForHome producerForHome = new ProducerForHome();
    private ConsumerForHome consumerPassenger = new ConsumerForHome();
    private ArrayList<Passenger> arrayPassengerForSmallLift;
    private ArrayList<Passenger> arrayPassengerForBigLift;
    private ILift passengerLift;
    private ILift freightLift;
    private int countFloor;
    private int countPassenger;


    public Home(int countFloor, int countPassenger) throws ExceptionCountFloor {
        if(countFloor < MIN_COUNT_FLOOR)
            throw new ExceptionCountFloor("Количество этажей в доме не должно быть меньше "+MIN_COUNT_FLOOR);
        this.countFloor = countFloor;
        this.countPassenger = countPassenger;
    }

    private void createPassenger(){
        arrayPassengerForSmallLift = producerForHome.createPassenger(countPassenger, countFloor, passengerLift);
        arrayPassengerForBigLift = producerForHome.createPassenger(countPassenger, countFloor, freightLift);
    }

    private void createLift(){
        passengerLift = producerForHome.createLift(PASSENGER_LIFT, countFloor);
        freightLift = producerForHome.createLift(FREIGHT_LIFT, countFloor);
    }
    private void startPassenger(){
        consumerPassenger.startPassenger(arrayPassengerForSmallLift);
        //consumerPassenger.startPassenger(arrayPassengerForBigLift);
    }

    private void startLift(){
        consumerPassenger.startLift(passengerLift, freightLift);
    }
    public void startWork(){
        createLift();
        createPassenger();
        startLift();
        startPassenger();
    }
}
