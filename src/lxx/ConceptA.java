package lxx;

import lxx.events.FireEvent;
import lxx.model.BattleField;
import lxx.model.BattleModel;
import lxx.model.BattleModelListener;
import lxx.paint.CaGraphics;
import lxx.paint.Canvas;
import lxx.services.Context;
import lxx.strategy.StrategySelector;
import lxx.strategy.TurnDecision;
import lxx.util.Log;
import robocode.*;
import robocode.Event;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Math.abs;
import static java.lang.Math.signum;

/**
 * User: Aleksey Zhidkov
 * Date: 18.06.12
 */
public class ConceptA extends TeamRobot {

    public static long currentTime;
    public static long currentRound;

    private final List<TickListener> tickListeners = new LinkedList<TickListener>();
    private final List<BattleModelListener> bmListeners = new LinkedList<BattleModelListener>();

    private BattleModel model = new BattleModel();

    private boolean isAlive = true;
    private Bullet firedBullet;
    private Context context;

    @Override
    public void run() {
        context = new Context(this);

        int robotWidth = (int) getWidth();
        int robotHeight = (int) getHeight();

        BattleField.init(robotWidth / 2, robotHeight / 2, (int) getBattleFieldWidth() - robotWidth, (int) getBattleFieldHeight() - robotHeight);

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
            try {
                for (TickListener tl : tickListeners) {
                    tl.tick();
                }

                final TurnDecision turnDecision = strategySelector.selectStrategy(model).getTurnDecision(model);
                move(turnDecision);
                handleGun(turnDecision);
                setTurnRadarRight(turnDecision.radarTurnRate);
                setTurnRadarRightRadians(turnDecision.radarTurnRate);

                paint(getGraphics());

            } catch (Exception e) {
                if (Log.isErrorEnabled()) {
                    Log.printStackTrace(e);
                }
            }
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

    private Map<String, List<Event>> getRobotsEvents(StatusEvent statusEvent) {
        final Map<String, List<Event>> res = new HashMap<String, List<Event>>();
        for (Event e : getAllEvents()) {
            if (e instanceof ScannedRobotEvent) {
                final String name = ((ScannedRobotEvent) e).getName();
                addEvent(res, e, name);
            } else if (e instanceof RobotDeathEvent) {
                addEvent(res, e, ((RobotDeathEvent) e).getName());
            } else if (e instanceof DeathEvent) {
                addEvent(res, e, getName());
            } else if (e instanceof HitByBulletEvent) {
                addEvent(res, e, ((HitByBulletEvent) e).getName());
                context.getBulletsService().onHitByBullet((HitByBulletEvent) e);
            } else if (e instanceof BulletHitEvent) {
                addEvent(res, e, ((BulletHitEvent) e).getName());
            } else if (e instanceof BulletHitBulletEvent) {
                context.getBulletsService().onBulletHitBullet((BulletHitBulletEvent) e);
            }
        }
        addEvent(res, statusEvent, getName());
        if (firedBullet != null) {
            addEvent(res, new FireEvent(currentTime, firedBullet), getName());
        }

        return res;
    }

    private void addEvent(Map<String, List<Event>> res, Event e, String name) {
        List<Event> events = res.get(name);
        if (events == null) {
            events = new LinkedList<Event>();
            res.put(name, events);
        }
        events.add(e);
    }

    public void addTickListener(TickListener tl) {
        tickListeners.add(tl);
    }

    @Override
    public void onStatus(StatusEvent e) {
        if (BattleConstants.myName == null) {
            printControls();
            initBattleConstants();
        }

        currentTime = getTime();
        final BattleModel newState = model.update(getRobotsEvents(e));

        for (BattleModelListener listener : bmListeners) {
            listener.battleModelUpdated(newState);
        }

        model = newState;
        Config.isPaintEnabled = false;
    }

    private void initBattleConstants() {
        BattleConstants.myName = getName();
        BattleConstants.initialGunHeat = getGunHeat();
        BattleConstants.gunCoolingRate = getGunCoolingRate();
        BattleConstants.setRobotBounds(getWidth(), getHeight());
        BattleConstants.totalEnemies = getOthers();
        BattleConstants.teammates = getTeammates() != null ? getTeammates().length : 0;
        BattleConstants.teammatesNames = getTeammates();
        final Matcher matcher = Pattern.compile(".*\\((\\d*)\\)").matcher(getName());
        if (matcher.find()) {
            BattleConstants.myIndex = new Integer(matcher.group(1));
        }
    }

    private void printControls() {
        System.out.println("Controls: ");
        System.out.println(" F1: Random movement");
        System.out.println(" F2: Waves");
        System.out.println(" F3: Bullet collisions");
        System.out.println(" F4: Wave surfing");
        System.out.println(" F5: Radar");
        System.out.println("F11: Decrease log level");
        System.out.println("F12: Increase log level");
        System.out.println("  p: Switch all canvases state");
        System.out.println();
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
            case KeyEvent.VK_F3:
                Canvas.BULLET_HITS.switchEnabled();
                break;
            case KeyEvent.VK_F4:
                Canvas.WAVE_SURFING.switchEnabled();
                break;
            case KeyEvent.VK_F5:
                Canvas.RADAR.switchEnabled();
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
