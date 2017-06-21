/**
 * Created by Evgenij on 20.06.2017.
 * Класс Call описывает вызов лифта
 * floor - номер этажа, на который вызван лифт
 * routeCall - направление вызова (up=true/down=false)
 * modeCall - режим вызова лифта(внешний = outer или внутренний = inner)
 */
public class Call {
    private int floor;
    private boolean routeCall;
    private boolean modeCall;
    private float idPassenger;

    public int getFloor() {
        return floor;
    }

    public boolean getRouteCall() {
        return routeCall;
    }

    public boolean getModeCall() {
        return modeCall;
    }

    public float getIdPassenger() {
        return idPassenger;
    }

    public Call(int floor, boolean routeCall, boolean modeCall, float idPassenger) {
        this.floor = floor;
        this.routeCall = routeCall;
        this.modeCall = modeCall;
        this.idPassenger = idPassenger;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Boolean.valueOf(routeCall).hashCode();
        result = prime * result + floor;
        result = prime * result + Boolean.valueOf(modeCall).hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        Call call = (Call) obj;
        return floor == call.floor
                && (routeCall == call.routeCall)
                && (modeCall == call.modeCall);
    }
}
