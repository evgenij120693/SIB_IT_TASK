import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Evgenij on 21.06.2017.
 */
public class ProducerPassenger {
    public ArrayList<Passenger> createPassenger(int countPassenger, int countFloor, ILift lift){
        ArrayList<Passenger> arrayPassenger = new ArrayList<>(countPassenger);
        Random random = new Random();
        int startFloor;
        int finalFloor;
        while (countPassenger != 0){
            startFloor = random.nextInt(countFloor-1)+1;
            do{
                finalFloor = random.nextInt(countFloor-1)+1;
            }while (finalFloor == startFloor);
            arrayPassenger.add(new Passenger(startFloor, finalFloor, lift));
            countPassenger--;
        }
        return arrayPassenger;
    }
}
