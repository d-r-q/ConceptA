package lxx.movement;

/**
 * User: Aleksey Zhidkov
 * Date: 18.06.12
 */
public class MovementDecision {

    public final double desiredVelocity;
    public final double turnRate;

    public MovementDecision(double desiredVelocity, double desiredHeading) {
        this.desiredVelocity = desiredVelocity;
        this.turnRate = desiredHeading;
    }
}
