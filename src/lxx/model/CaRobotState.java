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
    protected final Double radarHeading;
    protected final Double gunHeading;

    protected final double speed;

    public CaRobotState(String name, CaPoint position, double velocity, double heading, double energy, long time,
                        Double radarHeading, Double gunHeading) {
        this.name = name;
        this.position = position;
        this.velocity = velocity;
        this.heading = heading;
        this.energy = energy;
        this.time = time;
        this.radarHeading = radarHeading;
        this.gunHeading = gunHeading;

        this.speed = abs(velocity);
    }

    public CaRobotState(String name, CaPoint position, double velocity, double heading, double energy, long time) {
        // nulls for fast detecting of using object, which represents an enemy instead of ConceptA
        this(name, position, velocity, heading, energy, time, null, null);
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

    public CaPoint project(double alpha, double distance) {
        return position.project(alpha, distance);
    }

    public double getSpeed() {
        return speed;
    }

    public Double getRadarHeading() {
        return radarHeading;
    }

    public double getGunHeading() {
        return gunHeading;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CaRobotState that = (CaRobotState) o;

        if (time != that.time) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (int) (time ^ (time >>> 32));
        return result;
    }

}
