package lxx.model;

import lxx.util.CaPoint;

import static java.lang.Math.abs;

/**
 * User: Aleksey Zhidkov
 * Date: 18.06.12
 */
public class CaRobotState {

    protected final String name;
    protected final CaPoint position;
    protected final double velocity;
    protected final double heading;
    protected final double energy;
    protected final long time;
    private double speed;

    public CaRobotState(String name, CaPoint position,
                        double velocity, double heading, double energy, long time) {
        this.name = name;
        this.position = position;
        this.velocity = velocity;
        this.heading = heading;
        this.energy = energy;
        this.time = time;
        this.speed = abs(velocity);
    }

    public String getName() {
        return name;
    }

    public CaPoint getPosition() {
        return position;
    }

    public double getVelocity() {
        return velocity;
    }

    public double getHeading() {
        return heading;
    }

    public double getEnergy() {
        return energy;
    }

    public long getTime() {
        return time;
    }

    public double angleTo(CaRobot caRobot) {
        return position.angleTo(caRobot.position);
    }

    public double angleTo(CaPoint pnt) {
        return position.angleTo(pnt);
    }

    public double getSpeed() {
        return speed;
    }
}
