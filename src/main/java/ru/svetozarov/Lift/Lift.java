package ru.svetozarov.Lift;
import org.apache.log4j.Logger;
import ru.svetozarov.Lift.Call.Call;
import ru.svetozarov.Lift.ElectricMotor.IElectricMotor;
import ru.svetozarov.Other.ProjectConstants;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Evgenij on 19.06.2017.
 * Класс Lift описывает работу лифта
 */
public class Lift implements ILift,  ProjectConstants {

    private final int COUNT_FLOOR;
    private IElectricMotor electricMotor;
    private volatile int state = STATE_STOP;
    private volatile int floor = 1;
    private volatile int finalFloor;
    private volatile float idFirstPassenger;
    private volatile boolean ready = false;
    private volatile boolean routeCallLift = ROUTE_UP;//Направление вызова лифта
    private final String typeLift;
    private Logger logger;
    private CopyOnWriteArrayList<Call> queueCalls = new CopyOnWriteArrayList<Call>();//Очердь вызовов
    //количество ожидающих на n-ом этаже, которые вызвали лифт вверх  и вниз
    private ConcurrentHashMap<Integer, Integer> numberWaitingOnTheFloorRU = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Integer, Integer> numberWaitingOnTheFloorRD = new ConcurrentHashMap<>();
    private Object lock = new Object();

    public Lift(IElectricMotor electricMotor, int COUNT_FLOOR, String typeLift) {
        this.electricMotor = electricMotor;
        this.typeLift = typeLift;
        this.logger = Logger.getLogger(typeLift);
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

    public int getFinalFloor() {
        return finalFloor;
    }

    public float getIdFirstPassenger() {
        return idFirstPassenger;
    }

    /**
     * Функция обработки внешних вызовов, т.е. которые были сделаны вне лифта.
     * @param floor - этаж, с которого был сделан вызов
     * @param route - направление вызова (кнопка вверх(ROUTE_UP)/вниз(ROUTE_DOWN))
     * @param idPassenger - id пассажира
     */
    public void callOuter(int floor, boolean route, int idPassenger) {
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
                idFirstPassenger = idPassenger;
            } else if (state == STATE_IN_OUT_PASSENGER
                    && floor == this.floor && this.routeCallLift == route) {
                logger.trace("Пассажир №" + idPassenger + " заходит.");
            } else if (state == STATE_STOP && floor == this.floor) {
                routeCallLift = route;
                state = STATE_IN_OUT_PASSENGER;
                logger.trace("Пассажир №" + idPassenger + " заходит." + this.floor + " этаж. tut");
                ready = false;
            } else if (this.routeCallLift == route && this.finalFloor == floor) {
                logger.trace("Пассажир №" + idPassenger + " ожидает лифт на " + this.finalFloor + " этаже.");
            } else {
                String temp = "ВВЕРХ";
                if(route == ROUTE_DOWN)
                    temp = "ВНИЗ";
                addCall(floor, route, CALL_OUTER, idPassenger);
                logger.trace("Добавили вызов пассажира №" + idPassenger + " в очередь. Этаж "
                        + floor + ", напрваление "+temp);
            }
        }
    }

    /**
     * Функция обрабатки вызовов, совершенные из кабины лифта.
     * Сначала устанавливается конечный этаж или в очередь добавляется промежуточная остановка,
     * после чего идет проверка все ли пассажиры-попутчики зашли checkNumberWaitingAndChangeState(),
     * и в случае отсутвия таковых меняется статус лифта.
     * @param floor - конечный этаж
     * @param route - направление вызова(кнопка вверх или вниз)
     * @param idPassenger - id пассажира
     */
    public void callInner(int floor, boolean route, int idPassenger) {
        synchronized (lock) {
            logger.trace("Зашел пассажир №" + idPassenger + ". Добавил остановку на " + floor + " этаже ");
            if (route == ROUTE_DOWN) {
                if (this.finalFloor < this.floor) {
                    if (this.finalFloor > floor) {
                        //addCall(this.finalFloor, route, CALL_INNER, idFirstPassenger);
                        this.finalFloor = floor;
                        idFirstPassenger = idPassenger;
                    } /*else if(this.finalFloor < floor)
                        addCall(floor, route, CALL_INNER, idPassenger);*/
                } else {
                    //addCall(floor, route, CALL_INNER, idPassenger);
                    idFirstPassenger = idPassenger;
                    finalFloor = floor;
                }
            } else if (route == ROUTE_UP) {
                if (this.finalFloor > this.floor) {
                    if (this.finalFloor < floor) {
                        //addCall(this.finalFloor, route, CALL_INNER, idFirstPassenger);
                        this.finalFloor = floor;
                        idFirstPassenger = idPassenger;
                    } /*else if(this.finalFloor > floor)
                        addCall(floor, route, CALL_INNER, idPassenger);*/
                } else {
                   // addCall(floor, route, CALL_INNER, idPassenger);
                    idFirstPassenger = idPassenger;
                    this.finalFloor = floor;
                }
            }
            addCall(floor, route, CALL_INNER, idPassenger);
            decrementWaitingOnTheFloor(this.floor, route);//удаляем пассажира из очереди ожидающих
            queueCalls.remove(new Call(this.floor,route, CALL_OUTER, idPassenger));//удаляем вызов из очереди, если он есть
            checkNumberWaitingAndChangeState(route);//проверяем есть ли еще пассажиры-попутчики на этом этаже
            /*System.out.println("RD"+numberWaitingOnTheFloorRD.get(this.floor)+"state "+this.state);
            System.out.println("RU"+numberWaitingOnTheFloorRU.get(this.floor)+"state "+this.state);
            System.out.println("Ready "+ready);*/
        }
    }

    /**
     * Функция обработки движения лифта вверх.
     * Лифт осуществляет движение вверх, пока не будет достигнут конечный этаж - finalFloor
     * Если лифт уже везет пассажира(ов), то на каждом этаже происходит проверка наличия попутчиков
     * а также наличия промежуточных остановок для уже находящихся в лифте.
     * @param emptyLift - параметр определяющий пустой лифт или нет, т.е.
     *                  он уже везет пассажира или поехал за ним
     */
    private void down(boolean emptyLift) {
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
                        //logger.trace("Лифт остановился");
                        if (call.getModeCall() == CALL_INNER) {
                            logger.trace("Вышел  пассажир №" + call.getIdPassenger());
                        }else
                            ready = false;

                        flag = true;
                        queueCalls.remove(call);
                    }
                }
                if (flag) {
                    state = STATE_IN_OUT_PASSENGER;
                    this.stop();
                }
            }
            logger.trace("Лифт пустой. Проверка наличия вызова в очереди.");
            state = STATE_STOP;
            this.stop();
        } else {
            while (this.floor != finalFloor) {
                electricMotor.downLift();
                this.floor--;
                logger.trace(this.floor + " этаж...Едим вниз. Этаж, на котором ожидают: " + finalFloor);
            }
            ready = false;
            state = STATE_IN_OUT_PASSENGER;
            this.stop();
        }
    }

    /**
     * Функция обработки движения лифта вниз.
     * Лифт осуществляет движение вверх, пока не будет достигнут конечный этаж - finalFloor
     * Если лифт уже везет пассажира(ов), то на каждом этаже происходит проверка наличия попутчиков
     * а также наличия промежуточных остановок для уже находящихся в лифте.
     * @param emptyLift - параметр определяющий пустой лифт или нет, т.е.
     *                  он везет пассажира или поехал за ним
     */
    private void up(boolean emptyLift) {
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
                        //logger.trace("Лифт остановился");
                        if (call.getModeCall() == CALL_INNER) {
                            logger.trace("Вышел пассажир №" + call.getIdPassenger());
                        }else
                            ready = false;

                        flag = true;
                        queueCalls.remove(call);
                    }
                }
                if (flag) {
                    state = STATE_IN_OUT_PASSENGER;
                    this.stop();
                }
            }
            //logger.trace("Лифт на месте. Пассажир №"+idFirstPassenger+" выходит.");
            logger.trace("Лифт пустой. Проверка наличия вызова в очереди.");
            state = STATE_STOP;
            this.stop();
        } else {
            while (this.floor != finalFloor) {
                electricMotor.upLift();
                this.floor++;
                logger.trace(this.floor + " этаж...Едем вверх. Этаж, на котором ожидают:  " + finalFloor);
            }
            ready = false;
            state = STATE_IN_OUT_PASSENGER;
            this.stop();
        }
    }

    /**
     * Функция остановки лифта
     */
    private void stop() {
        electricMotor.stopLift();
        while(!ready){
            Thread.yield();
        }
    }

    /**
     * Функция добавления вызова в очередь вызовов
     * @param floor - этаж, с которого поступил вызов
     * @param route - напрваление вызова(кнопка вверх(ROUTE_UP)/вниз(ROUTE_DOWN))
     * @param modeCall - вид вызова (снаружи(CALL_OUTER)/из кабины(CALL_INNER))
     * @param idPassenger - id пассажира, совершившего вызов
     */
    private void addCall(int floor, boolean route, boolean modeCall, int idPassenger) {
        queueCalls.add(new Call(floor, route, modeCall, idPassenger));
    }

    private void checkNumberWaitingAndChangeState(boolean route){
        if(route == ROUTE_UP){
            if (numberWaitingOnTheFloorRU.get(this.floor) == 0) {
                ready = true;
                state = STATE_MOVE_UP;
            }
        }else{
            if (numberWaitingOnTheFloorRD.get(this.floor) == 0) {
                ready = true;
                state = STATE_MOVE_DOWN;
            }
        }
    }
    private void incrementWaitingOnTheFloor(int floor, boolean route) {
        if (route == ROUTE_UP)
            numberWaitingOnTheFloorRU.put(floor, numberWaitingOnTheFloorRU.get(floor) + 1);
        else
            numberWaitingOnTheFloorRD.put(floor, numberWaitingOnTheFloorRD.get(floor) + 1);
    }

    private void decrementWaitingOnTheFloor(int floor, boolean route) {
        if (route == ROUTE_UP)
            numberWaitingOnTheFloorRU.put(floor, numberWaitingOnTheFloorRU.get(floor) - 1);
        else
            numberWaitingOnTheFloorRD.put(floor, numberWaitingOnTheFloorRD.get(floor) - 1);
    }

    /**
     * Функция проверки количества ожидающих пассажиров на указанном этаже.
     * Пока все не зайдут в лифт, он будет ждать.
     */
    private void checkNumberOnTheFloor() {
        if (this.routeCallLift == ROUTE_UP) {
            while (numberWaitingOnTheFloorRU.get(this.floor) != 0)
                Thread.yield();
        } else {
            while (numberWaitingOnTheFloorRD.get(this.floor) != 0)
                Thread.yield();
        }
    }

    /**
     * Функция, описывающая работу лифта. Каждому состоянию соответсвует свое действие.
     */
    public void run() {
        logger.trace("Лифт запущен. 1 этаж");
        work:while (true) {
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
                        //while (queueCalls.contains(temp))
                            queueCalls.remove(temp);
                        decrementWaitingOnTheFloor(temp.getFloor(), temp.getRouteCall());
                        callOuter(temp.getFloor(), temp.getRouteCall(), temp.getIdPassenger());
                    }else{
                        ready = true;
                        logger.trace("Лифт свободен, очередь пуста.");
                        break work;

                    }
                    break;
            }
        }
    }
}


