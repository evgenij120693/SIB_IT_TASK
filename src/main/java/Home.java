import ru.svetozarov.Exception.ExceptionCountFloor;

import java.util.ArrayList;

/**
 * Created by Evgenij on 21.06.2017.
 */
public class Home implements ProjectConstants {
    private ProducerPassenger producerPassenger = new ProducerPassenger();
    private ConsumerPassenger consumerPassenger = new ConsumerPassenger();
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
        arrayPassengerForSmallLift = producerPassenger.createPassenger(countPassenger, countFloor, passengerLift);
        arrayPassengerForBigLift = producerPassenger.createPassenger(countPassenger, countFloor, freightLift);
    }

    private void createLift(){

    }
    public void startWork(){
        createPassenger();

    }




}
