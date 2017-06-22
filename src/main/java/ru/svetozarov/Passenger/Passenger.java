package ru.svetozarov.Passenger;

import ru.svetozarov.Lift.ILift;
import ru.svetozarov.Other.ProjectConstants;

import java.util.Random;

/**
 * Created by Evgenij on 19.06.2017.
 */
public class Passenger extends Thread implements ProjectConstants {

    private int startFloor;
    private int finalFloor;
    private int id;
    private ILift lift;

    public Passenger(int startFloor, int finalFloor, ILift lift) {
        this.startFloor = startFloor;
        this.finalFloor = finalFloor;
        this.lift = lift;
    }

    public int getStartFloor() {
        return startFloor;
    }

    public int getFinalFloor() {
        return finalFloor;
    }


    public void callLift(){
        boolean route;
        if(startFloor>finalFloor){
            route = ROUTE_DOWN;
        }else{
            route = ROUTE_UP;
        }
        lift.callOuter(startFloor,route, id);
        while(lift.getFloor()!=startFloor || lift.getState()!= STATE_IN_OUT_PASSENGER
                 || lift.getRouteCallLift() != route ) {
            Thread.yield();
        }
        lift.callInner(finalFloor, route, id);
    }
    private void delay(){
        try {
            Thread.sleep(new Random().nextInt(16000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        this.id =(int) Thread.currentThread().getId();
       // delay();
        callLift();
    }
}
