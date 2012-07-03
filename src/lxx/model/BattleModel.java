package lxx.model;

import lxx.BattleConstants;
import lxx.ConceptA;
import lxx.events.WavePassedEvent;
import lxx.services.WavesService;
import lxx.util.CaPoint;
import robocode.Event;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;

import java.util.*;

/**
 * User: Aleksey Zhidkov
 * Date: 18.06.12
 */
public class BattleModel {

    public final BattleModel prevState;

    public final Map<String, CaRobot> enemies;
    public final Map<String, CaRobot> teammates;

    public final CaRobot me;
    public final CaRobot duelOpponent;
    public final long time;

    public LinkedList<CaRobot> aliveEnemies;
    public List<CaRobot> aliveTeammates;

    public BattleModel() {
        prevState = null;
        enemies = new HashMap<String, CaRobot>();
        teammates = new HashMap<String, CaRobot>();
        me = null;
        duelOpponent = null;
        time = ConceptA.currentTime;
        aliveEnemies = null;
        aliveTeammates = null;
    }

    public BattleModel(BattleModel prevState, CaRobot me, Map<String, CaRobot> enemies, Map<String, CaRobot> teammates) {
        this.prevState = prevState;
        this.enemies = enemies;
        this.teammates = teammates;
        this.me = me;

        time = ConceptA.currentTime;

        aliveEnemies = getAliveEnemies();
        aliveTeammates = getAliveTeammates();

        duelOpponent = aliveEnemies.size() == 1
                ? aliveEnemies.getFirst()
                : null;
    }

    public BattleModel update(Map<String, List<Event>> events) {
        final CaRobotState nextState = CaRobotStateFactory.getMyState(events.remove(BattleConstants.myName));
        final CaRobot me = (this.me == null)
                ? new CaRobot(nextState)
                : new CaRobot(this.me, nextState);

        final Map<String, CaRobot> enemies = new HashMap<String, CaRobot>(this.enemies);
        final Map<String, CaRobot> teammates = new HashMap<String, CaRobot>(this.teammates);
        for (Map.Entry<String, List<Event>> e : events.entrySet()) {
            CaRobot robot = enemies.get(e.getKey());
            final CaRobotState enemyNextState = CaRobotStateFactory.getAnotherRobotState(me, robot != null ? robot
                    : new CaRobot(new CaRobotState(e.getKey(), new CaPoint(), 0, 0, 0, 0, false)), e.getValue());
            robot = (robot == null)
                    ? new CaRobot(enemyNextState)
                    : new CaRobot(robot, enemyNextState);

            if (BattleConstants.isTeammate(robot.getName())) {
                teammates.put(robot.getName(), robot);
            } else {
                enemies.put(robot.getName(), robot);
            }
        }

        return new BattleModel(this, me, enemies, teammates);
    }

    public CaRobot getRobot(String name) {
        if (me.getName().equals(name)) {
            return me;
        }
        final CaRobot enemy = enemies.get(name);
        if (enemy != null) {
            return enemy;
        }
        return teammates.get(name);
    }

    public boolean hasDuelOpponent() {
        return duelOpponent != null;
    }

    public List<CaRobot> getAliveTeammates() {
        final LinkedList<CaRobot> aliveTeammates = new LinkedList<CaRobot>();

        for (CaRobot teammate : teammates.values()) {
            if (teammate.isAlive()) {
                aliveTeammates.add(teammate);
            }
        }

        return aliveTeammates;
    }

    public LinkedList<CaRobot> getAliveEnemies() {
        final LinkedList<CaRobot> aliveEnemies = new LinkedList<CaRobot>();

        for (CaRobot enemy : enemies.values()) {
            if (enemy.isAlive()) {
                aliveEnemies.add(enemy);
            }
        }

        return aliveEnemies;
    }

    public List<CaRobot> getTeamRobots() {
        final LinkedList<CaRobot> teamRobots = new LinkedList<CaRobot>(aliveTeammates);
        teamRobots.add(me);
        return teamRobots;
    }
}
