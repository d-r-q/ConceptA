package lxx.model;

import robocode.ScannedRobotEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * User: Aleksey Zhidkov
 * Date: 18.06.12
 */
public class BattleModel {

    public CaRobot me;
    public Map<String, CaRobot> enemies = new HashMap<String, CaRobot>();
    private CaRobot duelOpponent;

    public void update(TurnEvents turnEvents) {
        final CaRobotState nextState = CaRobotStateFactory.createState(turnEvents.statusEvent);
        me = (me == null)
                ? new CaRobot(nextState)
                : new CaRobot(me, nextState);

        for (ScannedRobotEvent sre : turnEvents.scannedRobotEvents) {
            CaRobot enemy = enemies.get(sre.getName());
            final CaRobotState enemyNextState = CaRobotStateFactory.createState(turnEvents.statusEvent, sre);
            enemy = (enemy == null)
                    ? new CaRobot(enemyNextState)
                    : new CaRobot(enemy, enemyNextState);

            enemies.put(enemy.getName(), enemy);
        }

        if (enemies.size() == 1) {
            duelOpponent = enemies.values().iterator().next();
        }
    }

    public CaRobot duelOpponent() {
        return duelOpponent;
    }

}
