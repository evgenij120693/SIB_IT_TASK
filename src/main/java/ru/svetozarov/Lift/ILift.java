package ru.svetozarov.Lift;

/**
 * Created by Evgenij on 19.06.2017.
 */
public interface ILift extends Runnable{
    public void callOuter(int floor, boolean route, float idPassenger);
    public void callInner(int floor, boolean route, float idPassenger);
    public int getFloor();
    public int getState();
    public boolean getRouteCallLift();

    @Override
    default void run() {

    }
    //public boolean getStateDoor();
}
