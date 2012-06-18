package lxx.strategy;

/**
 * User: Aleksey Zhidkov
 * Date: 18.06.12
 */
public class TurnDecision {

    public final double desiredVelocity;
    public final double turnRate;

    public final double gunTurnRate;
    public final double firePower;

    public final double radarTurnRate;

    public TurnDecision(double desiredVelocity, double turnRate, double gunTurnRate, double firePower, double radarTurnRate) {
        this.desiredVelocity = desiredVelocity;
        this.turnRate = turnRate;
        this.gunTurnRate = gunTurnRate;
        this.firePower = firePower;
        this.radarTurnRate = radarTurnRate;
    }
}
