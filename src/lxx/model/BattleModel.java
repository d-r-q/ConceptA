package lxx.model;

import lxx.ConceptA;
import lxx.events.WavePassedEvent;
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

    public Map<String, CaRobot> enemies = new HashMap<String, CaRobot>();
    public Map<String, CaRobot> robots = new HashMap<String, CaRobot>();
    private LinkedList<Wave> waves = new LinkedList<Wave>();

    public CaRobot me;
    private CaRobot duelOpponent;
    public long time;

    public BattleModel() {
    }

    private BattleModel(BattleModel original) {
        this.me = original.me;
        this.enemies = new HashMap<String, CaRobot>(original.enemies);
        this.duelOpponent = original.duelOpponent;
        this.time = original.time;
    }

    public void update(TurnEvents turnEvents) {
        final BattleModel snapshot = new BattleModel(this);

        time = ConceptA.currentTime;
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
            robots.put(enemy.getName(), enemy);
        }

        robots.put(me.getName(), me);

        for (CaRobot robot : robots.values()) {
            if (robot.getFiredBulletSpeed() > 0) {
                waves.add(new Wave(snapshot, robot.getFiredBulletSpeed(), robot));
            }
        }

        final LinkedList<WavePassedEvent> wpEvents = new LinkedList<WavePassedEvent>();
        for (Iterator<Wave> wavesIter = waves.iterator(); wavesIter.hasNext();) {
            final Wave w = wavesIter.next();
            wpEvents.addAll(w.check(this));

            if (!w.hasRemainingTargets()) {
                wavesIter.remove();
            }
        }

        if (wpEvents.size() > 0) {
            //for ()
        }

        if (enemies.size() == 1) {
            duelOpponent = enemies.values().iterator().next();
        }
    }

    public CaRobot duelOpponent() {
        return duelOpponent;
    }

    public CaRobot[] getRobots() {
        return robots.values().toArray(new CaRobot[robots.size()]);
    }

    public CaRobot getRobot(String robotName) {
        return robots.get(robotName);
    }
}
