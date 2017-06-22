package ru.svetozarov.Lift;

import org.junit.Test;
import ru.svetozarov.Lift.ElectricMotor.ElectricMotor;
import ru.svetozarov.Lift.ElectricMotor.IElectricMotor;
import ru.svetozarov.Other.ProjectConstants;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.*;

/**
 * Created by Evgenij on 22.06.2017.
 */
public class LiftTest implements ProjectConstants {

    public Lift initLift(int countFloor, String typeLift,
                         int timeUp, int timeDown, int timeStop) {
        Lift lift = new Lift(new ElectricMotor(timeUp, timeDown, timeStop), countFloor, typeLift);
        return lift;
    }

    public int numberWaiting(int floor, boolean route, Lift lift) {
        Class classField = Lift.class;
        ConcurrentHashMap<Integer, Integer> numberWaitingOnTheFloor;
        String nameField = "numberWaitingOnTheFloorRU";
        if(route == ROUTE_DOWN)
            nameField = "numberWaitingOnTheFloorRD";
        try {
            Field numberWaitingOnTheFloorField = classField.getDeclaredField(nameField);
            numberWaitingOnTheFloorField.setAccessible(true);
            numberWaitingOnTheFloor = (ConcurrentHashMap<Integer, Integer>) numberWaitingOnTheFloorField.get(lift);
            return numberWaitingOnTheFloor.get(floor);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Test
    public void getRouteCallLift() {
        Lift lift = initLift(5, "test", 0, 0, 0);
        assertTrue(lift.getRouteCallLift() == ROUTE_UP);
    }


    @Test
    public void getFloor() {
        Lift lift = initLift(5, "test", 0, 0, 0);
        assertTrue(lift.getFloor() == 1);
    }

    @Test
    public void getState() {
        Lift lift = initLift(5, "test", 0, 0, 0);
        assertTrue(lift.getState() == STATE_STOP);
    }

    @Test
    public void callOuterConditionFirst() {
        Lift lift = initLift(5, "test", 0, 0, 0);
        int floor = 1;
        boolean route = ROUTE_UP;
        int idPassenger = 999;
        Class classLift = Lift.class;
        try {
            Field floorLift = classLift.getDeclaredField("floor");
            floorLift.setAccessible(true);
            floorLift.set(lift, 2);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        assertTrue(numberWaiting(floor,route,lift) == 0);
        assertTrue(lift.getFloor() == 2);
        assertTrue(lift.getFloor() > floor);
        assertTrue(lift.getState() == STATE_STOP);
        lift.callOuter(floor, route, idPassenger);
        assertTrue(numberWaiting(floor,route,lift) == 1);
        assertTrue(lift.getReady() == true);
        assertTrue(lift.getIdFirstPassenger() == idPassenger);
        assertTrue(lift.getState() == STATE_MOVE_DOWN_EMPTY);
        assertTrue(lift.getFinalFloor() == floor);
        assertTrue(lift.getRouteCallLift() == route);
    }

    @Test
    public void callOuterConditionSecond() {
        Lift lift = initLift(5, "test", 0, 0, 0);
        int floor = 3;
        boolean route = ROUTE_UP;
        int idPassenger = 999;
        assertTrue(numberWaiting(floor,route,lift) == 0);
        assertTrue(lift.getFloor() == 1);
        assertTrue(lift.getFloor() < floor);
        assertTrue(lift.getState() == STATE_STOP);
        lift.callOuter(floor, route, idPassenger);
        assertTrue(numberWaiting(floor,route,lift) == 1);
        assertTrue(lift.getReady() == true);
        assertTrue(lift.getIdFirstPassenger() == idPassenger);
        assertTrue(lift.getState() == STATE_MOVE_UP_EMPTY);
        assertTrue(lift.getFinalFloor() == floor);
        assertTrue(lift.getRouteCallLift() == route);
    }

    @Test
    public void callOuterConditionThird() {
        Lift lift = initLift(5, "test", 0, 0, 0);
        int floor = 3;
        boolean route = ROUTE_UP;
        int idPassenger = 999;
        Class classLift = Lift.class;
        try {
            Field floorLift = classLift.getDeclaredField("floor");
            floorLift.setAccessible(true);
            floorLift.set(lift, floor);
            Field stateLift = classLift.getDeclaredField("state");
            stateLift.setAccessible(true);
            stateLift.set(lift, STATE_IN_OUT_PASSENGER);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        assertTrue(numberWaiting(floor,route,lift) == 0);
        assertTrue(lift.getFloor() == floor);
        assertTrue(lift.getRouteCallLift() == route);
        assertTrue(lift.getState() == STATE_IN_OUT_PASSENGER);
        lift.callOuter(floor, route, idPassenger);
        assertTrue(numberWaiting(floor,route,lift) == 1);
        assertTrue(lift.getFloor() == floor);
        assertTrue(lift.getRouteCallLift() == route);
        assertTrue(lift.getState() == STATE_IN_OUT_PASSENGER);
    }

    @Test
    public void callInner() throws Exception {

    }

    @Test
    public void run() throws Exception {

    }

}