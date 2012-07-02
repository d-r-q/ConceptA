package lxx;

import lxx.events.FireEvent;
import lxx.model.*;
import lxx.paint.CaGraphics;
import lxx.paint.Canvas;
import lxx.services.Context;
import lxx.strategy.StrategySelector;
import lxx.strategy.TurnDecision;
import lxx.util.Log;
import robocode.AdvancedRobot;
import robocode.Bullet;
import robocode.DeathEvent;
import robocode.StatusEvent;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.List;

import static java.lang.Math.abs;
import static java.lang.Math.signum;

/**
 * User: Aleksey Zhidkov
 * Date: 18.06.12
 */
public class ConceptA extends AdvancedRobot {

    public static long currentTime;
    public static long currentRound;

    private final List<TickListener> tickListeners = new LinkedList<TickListener>();
    private final List<BattleModelListener> bmListeners = new LinkedList<BattleModelListener>();

    private BattleModel model = new BattleModel();

    private boolean isAlive = true;
    private Bullet firedBullet;

    @Override
    public void run() {
        final Context context = new Context(this);
        BattleConstants.myName = getName();
        BattleConstants.initialGunHeat = getGunHeat();
        BattleConstants.gunCoolingRate = getGunCoolingRate();
        BattleConstants.setRobotBounds(getWidth(), getHeight());
        BattleConstants.totalEnemies = getOthers();

        int robotWidth = (int) getWidth();
        int robotHeight = (int) getHeight();

        BattleField.init(robotWidth / 2, robotHeight / 2, (int)getBattleFieldWidth() - robotWidth, (int)getBattleFieldHeight() - robotHeight);

        currentRound = getRoundNum();

        setBodyColor(new Color(151, 187, 200));
        setGunColor(new Color(245, 250, 255));
        setRadarColor(Color.WHITE);

        final StrategySelector strategySelector = new StrategySelector(this, context);

        // start search for enemy until state is unknown
        setTurnRightRadians(Double.POSITIVE_INFINITY);
        setTurnGunRightRadians(Double.POSITIVE_INFINITY);
        setTurnRadarRightRadians(Double.POSITIVE_INFINITY);

        while (isAlive) {
            // model updated in onStatus
            for (TickListener tl : tickListeners) {
                tl.tick();
            }
            final TurnDecision turnDecision = strategySelector.selectStrategy(model).getTurnDecision(model);
            move(turnDecision);
            handleGun(turnDecision);
            setTurnRadarRight(turnDecision.radarTurnRate);

            setTurnRadarRightRadians(turnDecision.radarTurnRate);

            paint(getGraphics());
            execute();
        }
    }

    private void move(TurnDecision tr) {
        setTurnRightRadians(tr.turnRate);
        setMaxVelocity(abs(tr.desiredVelocity));
        setAhead(100 * signum(tr.desiredVelocity));
    }

    private void handleGun(TurnDecision tr) {
        setTurnGunRightRadians(tr.gunTurnRate);
        if (tr.firePower > 0) {
            firedBullet = setFireBullet(tr.firePower);
        } else {
            firedBullet = null;
        }
    }

    private TurnEvents getTurnEvents(StatusEvent e) {
        final FireEvent fireEvent = firedBullet == null
                ? null
                : new FireEvent(firedBullet);
        final TurnEvents turnEvents = new TurnEvents(getScannedRobotEvents(), e, fireEvent);
        getScannedRobotEvents().clear();
        getStatusEvents().clear();

        return turnEvents;
    }

    public void addTickListener(TickListener tl) {
        tickListeners.add(tl);
    }

    @Override
    public void onStatus(StatusEvent e) {
        currentTime = getTime();
        final BattleModel newState = model.update(getTurnEvents(e));

        for (BattleModelListener listener : bmListeners) {
            listener.battleModelUpdated(newState);
        }

        model = newState;
        Config.isPaintEnabled = false;
    }

    @Override
    public void onPaint(Graphics2D g) {
        Config.isPaintEnabled = true;
    }

    private void paint(Graphics2D g) {
        final CaGraphics cg = new CaGraphics(g);

        for (Canvas c : Canvas.values()) {
            c.exec(cg);
        }
    }

    @Override
    public void onKeyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_F1:
                Canvas.RANDOM_MOVEMENT.switchEnabled();
                break;
            case KeyEvent.VK_F2:
                Canvas.WAVES.switchEnabled();
                break;
            case KeyEvent.VK_F11:
                Log.decreaseLogLevel();
                break;
            case KeyEvent.VK_F12:
                Log.increaseLogLevel();
                break;
            case KeyEvent.VK_P:
                for (Canvas c : Canvas.values()) {
                    c.switchEnabled();
                }
                break;
        }
    }

    @Override
    public void onDeath(DeathEvent event) {
        isAlive = false;
    }

    public void addBattleModelListener(BattleModelListener listener) {
        bmListeners.add(listener);
    }
}
