package ru.svetozarov.Lift;

import org.junit.Test;
import ru.svetozarov.Lift.ElectricMotor.ElectricMotor;
import ru.svetozarov.Lift.ElectricMotor.IElectricMotor;
import ru.svetozarov.Other.ProjectConstants;

import static org.junit.Assert.*;

/**
 * Created by Evgenij on 22.06.2017.
 */
public class LiftTest implements ProjectConstants{
    public Lift initLift( int countFloor, String typeLift,
                          int timeUp, int timeDown, int timeStop){
        Lift lift = new Lift(new ElectricMotor(timeUp, timeDown, timeStop), countFloor, typeLift);
        return  lift;
    }
    @Test
    public void getRouteCallLift()  {
        Lift lift = initLift(5,"test",0,0,0);
        assertTrue(lift.getRouteCallLift() == ROUTE_UP);
    }



    @Test
    public void getFloor() {
        Lift lift = initLift(5,"test",0,0,0);
        assertTrue(lift.getFloor() == 1);
    }

    @Test
    public void getState() {
        Lift lift = initLift(5,"test",0,0,0);
        assertTrue(lift.getState() == STATE_STOP);
    }

    @Test
    public void callOuter()  {

    }

    @Test
    public void callInner() throws Exception {

    }

    @Test
    public void run() throws Exception {

    }

}