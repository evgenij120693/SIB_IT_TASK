/**
 * Created by Evgenij on 19.06.2017.
 */
public class Passenger extends Thread implements ProjectConstants{

    private int startFloor;
    private int finalFloor;
    private float id;
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

    @Override
    public void run() {
        id = Thread.currentThread().getId();
        callLift();
    }
}
