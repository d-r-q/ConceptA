package lxx.strategy;

import lxx.ConceptA;
import lxx.gun.GuessFactorGun;
import lxx.model.BattleModel;
import lxx.model.CaRobot;
import lxx.movement.MovementDecision;
import lxx.movement.RandomMovement;
import lxx.radar.DuelRadar;
import robocode.Rules;
import robocode.util.Utils;

/**
 * User: Aleksey Zhidkov
 * Date: 18.06.12
 */
public class DuelStrategy implements Strategy {

    private final ConceptA me;
    private final RandomMovement randomMovement;
    private final GuessFactorGun gun;

    public DuelStrategy(ConceptA me, GuessFactorGun gun) {
        this.me = me;
        this.gun = gun;
        randomMovement = new RandomMovement();
        me.addTickListener(randomMovement);
    }

    @Override
    public boolean applicable(BattleModel model) {
        return model.enemies.size() == 1;
    }

    @Override
    public TurnDecision getTurnDecision(BattleModel model) {
        me.setAdjustGunForRobotTurn(true);
        me.setAdjustRadarForGunTurn(true);
        me.setAdjustRadarForRobotTurn(true);

        final CaRobot opponent = model.duelOpponent;
        final double angleToEnemy = model.me.angleTo(opponent);

        final MovementDecision md = randomMovement.getMovementDecision(model);

        final double bo = gun.aim(model, Rules.getBulletSpeed(3));
        final double gunHeading = angleToEnemy + bo;

        return new TurnDecision(
                md.desiredVelocity, md.turnRate,
                Utils.normalRelativeAngle(gunHeading - me.getGunHeadingRadians()), 3,
                DuelRadar.getRadarTurnAngleRadians(model.me, model.duelOpponent));
    }

}
