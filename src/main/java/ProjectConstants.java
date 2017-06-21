/**
 * Created by Evgenij on 20.06.2017.
 */
public interface ProjectConstants {
    int MIN_COUNT_FLOOR = 2;
    boolean CALL_OUTER = false;
    boolean CALL_INNER = true;
    boolean ROUTE_UP = true;
    boolean ROUTE_DOWN = false;
    boolean DOOR_CLOSE = false;
    boolean DOOR_OPEN = true;
    int STATE_MOVE_UP_EMPTY = 4;
    int STATE_MOVE_DOWN_EMPTY = 3;
    int STATE_MOVE_UP = 2;//движение ввверх с пассажиром(ами)
    int STATE_MOVE_DOWN = 1;//движение вниз с пассажиром(ами)
    int STATE_STOP = 0;// конечная остановка, после которой лифт свободен
    int STATE_IN_OUT_PASSENGER = 5;// промежуточные остановки

}
