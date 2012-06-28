package lxx.movement;

import lxx.ConceptA;
import lxx.TickListener;
import lxx.model.BattleModel;

/**
 * User: Aleksey Zhidkov
 * Date: 18.06.12
 */
public class RandomMovement implements TickListener {

    private final OrbitalMovement orbitMovement = new OrbitalMovement(500);

    private OrbitDirection orbitDirection = OrbitDirection.CLOCKWISE;
    private long timeToChangeDir = 0;

    public MovementDecision getMovementDecision(BattleModel model) {
        return orbitMovement.makeDecision(model, model.duelOpponent.getPosition(), orbitDirection);
    }

    private static OrbitDirection reverse(OrbitDirection dir) {
        switch (dir) {
            case CLOCKWISE:
                return OrbitDirection.COUNTER_CLOCKWISE;
            case COUNTER_CLOCKWISE:
                return OrbitDirection.CLOCKWISE;
            case STOP:
                return OrbitDirection.STOP;
            default:
                throw new IllegalArgumentException("Unknown direction: " + dir);
        }
    }

    @Override
    public void tick() {
        if (ConceptA.currentTime >= timeToChangeDir) {
            orbitDirection = OrbitDirection.values()[(int) (Math.random() * OrbitDirection.values().length)];
            timeToChangeDir = (long) (ConceptA.currentTime + 20 * Math.random());
        }
    }
}
