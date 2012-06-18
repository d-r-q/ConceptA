package lxx;

import lxx.model.BattleField;
import lxx.model.BattleModel;
import lxx.model.TurnEvents;
import lxx.paint.CaGraphics;
import lxx.paint.Canvas;
import lxx.strategy.StrategySelector;
import lxx.strategy.TurnDecision;
import lxx.util.Log;
import robocode.AdvancedRobot;
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

    private final BattleModel model = new BattleModel();
    private final List<TickListener> tickListeners = new LinkedList<TickListener>();

    private boolean isAlive = true;

    @Override
    public void run() {
        BattleConstants.myName = getName();
        BattleConstants.initialGunHeat = getGunHeat();
        BattleConstants.gunCoolingRate = getGunCoolingRate();

        int robotWidth = (int) getWidth();
        int robotHeight = (int) getHeight();

        BattleField.init(robotWidth / 2, robotHeight / 2, (int)getBattleFieldWidth() - robotWidth, (int)getBattleFieldHeight() - robotHeight);

        currentRound = getRoundNum();

        setBodyColor(new Color(151, 187, 200));
        setGunColor(new Color(245, 250, 255));
        setRadarColor(Color.WHITE);

        final StrategySelector strategySelector = new StrategySelector(model, this);

        // start search for enemy until state is unknown
        setTurnRightRadians(Double.POSITIVE_INFINITY);
        setTurnGunRightRadians(Double.POSITIVE_INFINITY);
        setTurnRadarRightRadians(Double.POSITIVE_INFINITY);

        while (isAlive) {
            // model updated in onStatus
            for (TickListener tl : tickListeners) {
                tl.tick();
            }
            final TurnDecision turnDecision = strategySelector.selectStrategy().getTurnDecision(model);
            move(turnDecision);
            handleGun(turnDecision);
            setTurnRadarRight(turnDecision.radarTurnRate);

            setTurnRadarRightRadians(turnDecision.radarTurnRate);

            Config.isPaintEnabled = false;
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
            setFire(tr.firePower);
        }
    }

    private TurnEvents getTurnEvents(StatusEvent e) {
        final TurnEvents turnEvents = new TurnEvents(getScannedRobotEvents(), e);
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
        model.update(getTurnEvents(e));
    }

    @Override
    public void onPaint(Graphics2D g) {
        Config.isPaintEnabled = true;
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
            case KeyEvent.VK_F11:
                Log.decreaseLogLevel();
            case KeyEvent.VK_F12:
                Log.increaseLogLevel();
        }
    }

    @Override
    public void onDeath(DeathEvent event) {
        isAlive = false;
    }
}
