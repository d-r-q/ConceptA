package lxx.model;

import lxx.ConceptA;
import lxx.events.WavePassedEvent;
import lxx.services.WavesService;
import robocode.ScannedRobotEvent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

/**
 * User: Aleksey Zhidkov
 * Date: 18.06.12
 */
public class BattleModel {

    public final BattleModel prevState;

    public final Map<String, CaRobot> enemies;

    public final CaRobot me;
    public final CaRobot duelOpponent;
    public final long time;

    public BattleModel() {
        prevState = null;
        enemies = new HashMap<String, CaRobot>();
        me = null;
        duelOpponent = null;
        time = ConceptA.currentTime;
    }

    public BattleModel(BattleModel prevState, CaRobot me, Map<String, CaRobot> enemies) {
        this.prevState = prevState;
        this.enemies = enemies;
        this.me = me;
        duelOpponent = enemies.size() == 1
                ? enemies.values().iterator().next()
                : null;
        time = ConceptA.currentTime;
    }

    public BattleModel update(TurnEvents turnEvents) {
        final CaRobotState nextState = CaRobotStateFactory.createState(turnEvents.statusEvent);
        final CaRobot me = (this.me == null)
                ? new CaRobot(nextState)
                : new CaRobot(this.me, nextState);

        final Map<String, CaRobot> enemies = new HashMap<String, CaRobot>(this.enemies);
        for (ScannedRobotEvent sre : turnEvents.scannedRobotEvents) {
            CaRobot enemy = enemies.get(sre.getName());
            final CaRobotState enemyNextState = CaRobotStateFactory.createState(turnEvents.statusEvent, sre);
            enemy = (enemy == null)
                    ? new CaRobot(enemyNextState)
                    : new CaRobot(enemy, enemyNextState);

            enemies.put(enemy.getName(), enemy);
        }

        return new BattleModel(this, me, enemies);
    }

    public CaRobot getRobot(String name) {
        if (me.getName().equals(name)) {
            return me;
        }
        return enemies.get(name);
    }

    public boolean hasDuelOpponent() {
        return duelOpponent != null;
    }
}
