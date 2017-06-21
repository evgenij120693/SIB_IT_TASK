/**
 * Created by Evgenij on 19.06.2017.
 */
public class Passenger implements ProjectConstants{

    private int startFloor;
    private int finalFloor;
    private float id = Thread.currentThread().getId();
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

    public float getName() {
        return id;
    }
    public void callLift(){
        boolean route;
        if(startFloor>finalFloor){
            route = ROUTE_DOWN;
        }else{
            route = ROUTE_UP;
        }
        lift.callOuter(startFloor,route, id);
        while(lift.getFloor()!=startFloor || lift.getStateDoor()!=DOOR_OPEN
                 || lift.getRouteCallLift() != route ) {

            Thread.yield();
        }
        // System.out.println(lift.getState() + " pessenger");
        //System.out.println("Переключение");
        lift.callInner(finalFloor, route, id);

    }
}
