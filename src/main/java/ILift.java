/**
 * Created by Evgenij on 19.06.2017.
 */
public interface ILift {
    public void callOuter(int floor, boolean route, float idPassenger);
    public void callInner(int floor, boolean route, float idPassenger);
    public int getFloor();
    public int getState();
    public boolean getReady();
    public void stop();
    public boolean getRouteCallLift();
    public boolean getStateDoor();
}
