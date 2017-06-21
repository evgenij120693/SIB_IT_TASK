import org.apache.log4j.Logger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Evgenij on 19.06.2017.
 */
public class Lift implements ILift, Runnable, ProjectConstants {

    private static Logger logger = Logger.getLogger(Lift.class);
    private final int COUNT_FLOOR;
    private IElectricMotor electricMotor;
    private volatile int state = STATE_STOP;
    private volatile int floor = 1;
    private volatile int finalFloor;
    private volatile float idFirstPassenger;
    private volatile boolean ready = false;
    private volatile boolean routeCallLift = ROUTE_UP;
    private String typeLift;
    private CopyOnWriteArrayList<Call> queueCalls = new CopyOnWriteArrayList<Call>();
    private ConcurrentHashMap<Integer, Integer> numberWaitingOnTheFloorRU = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Integer, Integer> numberWaitingOnTheFloorRD = new ConcurrentHashMap<>();
    private Object lock = new Object();

    public Lift(IElectricMotor electricMotor, int COUNT_FLOOR, String typeLift) {
        this.electricMotor = electricMotor;
        this.typeLift = typeLift;
        this.COUNT_FLOOR = COUNT_FLOOR;
        for (int i = 1; i <= this.COUNT_FLOOR; i++) {
            numberWaitingOnTheFloorRU.put(i, 0);
            numberWaitingOnTheFloorRD.put(i, 0);
        }
    }

    public boolean getRouteCallLift() {
        return routeCallLift;
    }


    public int getFloor() {
        return floor;
    }

    public int getState() {
        return state;
    }

    public boolean getReady() {
        return ready;
    }

    public String getTypeLift() {
        return typeLift;
    }

    public void callOuter(int floor, boolean route, float idPassenger) {
        synchronized (lock) {
            incrementWaitingOnTheFloor(floor, route);
            if (state == STATE_STOP && floor != this.floor) {
                if (floor > this.floor)
                    state = STATE_MOVE_UP_EMPTY;
                else if (floor < this.floor)
                    state = STATE_MOVE_DOWN_EMPTY;
                routeCallLift = route;
                finalFloor = floor;
                logger.trace("Лифт поехал за пассажиром №" + idPassenger + " на " + finalFloor + " этаж");
                ready = true;
               // stateDoor = DOOR_CLOSE;
                idFirstPassenger = idPassenger;
                //decrementWaitingOnTheFloor(floor, route);
            } else if ((state == STATE_MOVE_UP && floor < this.finalFloor && floor > this.floor)
                    || (state == STATE_MOVE_DOWN && floor > this.finalFloor && floor < this.floor)) {
                addCall(floor, route, CALL_OUTER, idPassenger);
                logger.trace("Добавили вызов пассажира №" + idPassenger + " на " + floor + " этаже.");
            } else if (state == STATE_IN_OUT_PASSENGER
                    && floor == this.floor && this.routeCallLift == route) {
                logger.trace("Пассажир №" + idPassenger + " заходит.");
                //stateDoor = DOOR_OPEN;
            } else if (state == STATE_STOP && floor == this.floor) {
                routeCallLift = route;
                //this.finalFloor = finalFloor;
                state = STATE_IN_OUT_PASSENGER;
                logger.trace("Пассажир №" + idPassenger + " заходит." + this.floor + " этаж. tut");
                //stop();
            } else if (this.routeCallLift == route && this.finalFloor == floor) {
                // incrementWaitingOnTheFloor(floor, route);
                logger.trace("Пассажир №" + idPassenger + " ожидает лифт на " + this.finalFloor + " этаже.");
            } else {
                String temp = "ВВЕРХ";
                if(route == ROUTE_DOWN)
                     temp = "ВНИЗ";
                addCall(floor, route, CALL_OUTER, idPassenger);
                logger.trace("Добавили вызов пассажира №" + idPassenger + " в очередь. Этаж "
                        + floor + ", напрваление "+temp);
            }
            // incrementWaitingOnTheFloor(floor, route);
        }
    }


    public void callInner(int floor, boolean route, float idPassenger) {
        synchronized (lock) {
            //  System.out.println(" call inner");
            logger.trace("Зашел пассажир №" + idPassenger + ". Добавил остановку на " + floor + " этаже ");
            if (route == ROUTE_DOWN) {
                if (numberWaitingOnTheFloorRD.get(this.floor) == 1) {
                   // stateDoor = DOOR_CLOSE;
                    state = STATE_MOVE_DOWN;
                    ready = true;
                }
                // System.out.println("number waitong rd "+numberWaitingOnTheFloorRD.get(this.floor));
                if (this.finalFloor < this.floor) {
                    System.out.println(finalFloor + " " + floor);
                    if (this.finalFloor > floor) {
                        addCall(this.finalFloor, route, CALL_INNER, idFirstPassenger);
                        this.finalFloor = floor;
                        idFirstPassenger = idPassenger;
                    } else
                        addCall(floor, route, CALL_INNER, idPassenger);
                } else {
                    //System.out.println("gou " + state);
                    idFirstPassenger = idPassenger;
                    finalFloor = floor;
                    ready = true;
                }
            } else if (route == ROUTE_UP) {
                if (numberWaitingOnTheFloorRU.get(this.floor) == 1) {
                   // stateDoor = DOOR_CLOSE;
                    ready = true;
                    state = STATE_MOVE_UP;
                }
                //System.out.println("number waitong ru "+numberWaitingOnTheFloorRU.get(this.floor));

                if (this.finalFloor > this.floor) {
                    //System.out.println(finalFloor + " " + floor);
                    if (this.finalFloor < floor) {
                        addCall(this.finalFloor, route, CALL_INNER, idFirstPassenger);
                        this.finalFloor = floor;
                        idFirstPassenger = idPassenger;
                    } else
                        addCall(floor, route, CALL_INNER, idPassenger);
                    //logger.trace("Зашел пассажир №" + idPassenger + ". Добавил остановку на " + floor + " этаже ");
                } else {
                    //System.out.println("gou " + state);
                    idFirstPassenger = idPassenger;
                    this.finalFloor = floor;
                    ready = true;
                }
            }
            decrementWaitingOnTheFloor(this.floor, route);
          /*  System.out.println(" tut "+numberWaitingOnTheFloorRU.get(this.floor) + " state "+this.state);
            System.out.println(" tut "+numberWaitingOnTheFloorRD.get(this.floor));*/

        }
    }

    private void down(boolean emptyLift) {
        //stateDoor = DOOR_CLOSE;
        if (!emptyLift) {
            logger.trace("Лифт поехал на " + finalFloor + " этаж. Едем вниз.");
            while (this.floor != finalFloor) {
                electricMotor.downLift();
                this.floor--;
                logger.trace(this.floor + " этаж...Едем вниз. Конечный этаж " + finalFloor);
                boolean flag = false;
                for (Call call :
                        queueCalls) {
                    if (this.floor == call.getFloor() && call.getRouteCall() == ROUTE_DOWN) {
                        logger.trace("Лифт остановился");
                        if (call.getModeCall() == CALL_OUTER) {
                            logger.trace("Вошел пассажир №" + call.getIdPassenger());
                        } else {
                            logger.trace("Вышел  пассажир №" + call.getIdPassenger());
                        }
                        flag = true;
                        queueCalls.remove(call);
                    }
                }
                if (flag) {
                    state = STATE_IN_OUT_PASSENGER;
                    //state = STATE_STOP;
                    this.stop();
                }
            }
            state = STATE_STOP;
            this.stop();
            logger.trace("Вышел пассажир №"+idFirstPassenger);
        } else {
            while (this.floor != finalFloor) {
                electricMotor.downLift();
                this.floor--;
                logger.trace(this.floor + " этаж...Едим вниз. Этаж, на котором ожидают " + finalFloor);
            }
            ready = false;
            state = STATE_IN_OUT_PASSENGER;
            //state = STATE_STOP;
            this.stop();
            //logger.trace("Пассажир №"+idFirstPassenger+" заходит.");
            //while (numberWaitingOnTheFloorRD.get(floor) != 0)
            // Thread.yield();
        }
    }

    private void up(boolean emptyLift) {
       // stateDoor = DOOR_CLOSE;
        if (!emptyLift) {
            logger.trace("Лифт поехал на " + finalFloor + " этаж. Едем вверх");
            while (this.floor != finalFloor) {
                electricMotor.upLift();
                this.floor++;
                logger.trace(this.floor + " этаж...Едем вверх. Конечный этаж "+this.finalFloor);
                boolean flag = false;
                for (Call call :
                        queueCalls) {
                    if (this.floor == call.getFloor() && call.getRouteCall() == ROUTE_UP) {
                        logger.trace("Лифт остановился");
                        if (call.getModeCall() == CALL_OUTER) {
                            logger.trace("Вошел пассажир №" + call.getIdPassenger());
                        } else {
                            logger.trace("Вышел пассажир №" + call.getIdPassenger());
                        }
                        flag = true;
                        queueCalls.remove(call);
                    }
                }
                if (flag) {
                    state = STATE_IN_OUT_PASSENGER;
                    //state = STATE_STOP;
                    this.stop();
                }
            }
            logger.trace("Лифт на месте. Пассажир №"+idFirstPassenger+" выходит.");
            //ready = true;
            state = STATE_STOP;
            this.stop();
        } else {
            while (this.floor != finalFloor) {
                electricMotor.upLift();
                this.floor++;
                logger.trace(this.floor + " этаж...Едем вверх. Этаж, на котором ожидают  " + finalFloor);
                //  System.out.println(state + " j");
            }
            ready = false;
            //logger.trace("Пассажир №"+idFirstPassenger+" заходит.");
            state = STATE_IN_OUT_PASSENGER;
            //state = STATE_STOP;
            this.stop();
        }
    }

    public void stop() {
       // stateDoor = DOOR_OPEN;
        electricMotor.stopLift();
        checkNumberOnTheFloor();

    }

    private void addCall(int floor, boolean route, boolean modeCall, float idPassenger) {
        queueCalls.add(new Call(floor, route, modeCall, idPassenger));
    }

    private void incrementWaitingOnTheFloor(int floor, boolean route) {
        // System.out.println("++");
        if (route == ROUTE_UP)
            numberWaitingOnTheFloorRU.put(floor, numberWaitingOnTheFloorRU.get(floor) + 1);
        else
            numberWaitingOnTheFloorRD.put(floor, numberWaitingOnTheFloorRD.get(floor) + 1);
        /*System.out.println("size waiting RU" + numberWaitingOnTheFloorRU.get(floor)+" to floor "+floor);
        System.out.println("size waiting RD" + numberWaitingOnTheFloorRD.get(floor) +" to floor "+floor);*/

    }

    private void decrementWaitingOnTheFloor(int floor, boolean route) {
        //System.out.println("--");
        if (route == ROUTE_UP)
            numberWaitingOnTheFloorRU.put(floor, numberWaitingOnTheFloorRU.get(floor) - 1);
        else
            numberWaitingOnTheFloorRD.put(floor, numberWaitingOnTheFloorRD.get(floor) - 1);
        /*System.out.println("size waiting RU" + numberWaitingOnTheFloorRU.get(floor)+" to floor "+floor);
        System.out.println("size waiting RD" + numberWaitingOnTheFloorRD.get(floor) +" to floor "+floor);*/
    }

    private void checkNumberOnTheFloor() {
        if (this.routeCallLift == ROUTE_UP) {
            while (numberWaitingOnTheFloorRU.get(this.floor) != 0)
                Thread.yield();
        } else {
            while (numberWaitingOnTheFloorRD.get(this.floor) != 0)
                Thread.yield();
        }
    }

    public void run() {
        while (true) {
            while (!ready)
                Thread.yield();
            switch (state) {
                case STATE_MOVE_UP_EMPTY:
                    up(true);
                    break;
                case STATE_MOVE_DOWN_EMPTY:
                    down(true);
                    break;
                case STATE_MOVE_UP:
                    up(false);
                    break;
                case STATE_MOVE_DOWN:
                    down(false);
                    break;
                case STATE_STOP:
                    ready = false;
                    if (queueCalls.size() != 0) {
                        Call temp = queueCalls.remove(0);
                        while (queueCalls.contains(temp))
                            queueCalls.remove(temp);
                        decrementWaitingOnTheFloor(temp.getFloor(), temp.getRouteCall());
                        callOuter(temp.getFloor(), temp.getRouteCall(), temp.getIdPassenger());
                    }else{
                        ready = false;
                        logger.trace("Лифт свободен, очередь пуста. Ожидается вызов");
                    }
                    break;
            }
        }
    }
}

