package lxx.strategy;

import lxx.ConceptA;
import lxx.gun.GuessFactorGun;
import lxx.model.BattleModel;
import lxx.model.CaRobot;
import lxx.movement.MovementDecision;
import lxx.movement.RandomMovement;
import lxx.movement.WaveSurfingMovement;
import lxx.radar.DuelRadar;
import lxx.services.BulletsService;
import lxx.services.Context;
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
    private final WaveSurfingMovement waveSurfingMovement;
    private final BulletsService bulletsService;

    public DuelStrategy(ConceptA me, Context context, GuessFactorGun gun) {
        this.me = me;
        this.gun = gun;
        randomMovement = new RandomMovement();
        me.addTickListener(randomMovement);
        bulletsService = context.getBulletsService();
        waveSurfingMovement = new WaveSurfingMovement(context.getMovementDataManager(), bulletsService);
    }

    @Override
    public boolean applicable(BattleModel model) {
        return model.aliveEnemies.size() == 1;
    }

    @Override
    public TurnDecision getTurnDecision(BattleModel model) {
        me.setAdjustGunForRobotTurn(true);
        me.setAdjustRadarForGunTurn(true);
        me.setAdjustRadarForRobotTurn(true);

        final CaRobot opponent = model.duelOpponent;
        final double angleToEnemy = model.me.angleTo(opponent);

        final MovementDecision md = waveSurfingMovement.applicable(model)
                ? waveSurfingMovement.getMovementDecision(model)
                : randomMovement.getMovementDecision(model);

        final double bo = gun.aim(model, Rules.getBulletSpeed(3));
        final double gunHeading = angleToEnemy + bo;

        return new TurnDecision(
                md.desiredVelocity, md.turnRate,
                Utils.normalRelativeAngle(gunHeading - me.getGunHeadingRadians()), 3,
                DuelRadar.getRadarTurnAngleRadians(model.me, model.duelOpponent));
    }

}
