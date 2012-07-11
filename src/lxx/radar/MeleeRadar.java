package lxx.radar;

import lxx.model.BattleModel;
import lxx.model.CaRobot;
import lxx.paint.Canvas;
import lxx.paint.ColorFactory;
import lxx.paint.Line;
import lxx.util.CaPoint;
import lxx.util.Log;
import robocode.Rules;
import robocode.util.Utils;

import java.awt.*;
import java.util.LinkedList;

import static java.lang.StrictMath.signum;
import static java.lang.StrictMath.toDegrees;

/**
 * User: Aleksey Zhidkov
 * Date: 09.07.12
 */
public class MeleeRadar {

    private final LinkedList<Double> scanHistory = new LinkedList<Double>();

    private CaRobot enemyToScan;
    private double radarTurnDirection;

    public double getRadarTurnRate(BattleModel model) {
        drawScanHistory(model);

        if (enemyToScan == null || enemyToScan.getLastScanTime() < model.getRobot(enemyToScan.getName()).getLastScanTime() ||
                !model.getRobot(enemyToScan.getName()).isAlive()) {
            enemyToScan = selectEnemyToScan(model);
            radarTurnDirection = selectScanDirection(model);
        }

        return radarTurnDirection == 1
                ? Rules.RADAR_TURN_RATE_RADIANS
                : -Rules.RADAR_TURN_RATE_RADIANS;
    }

    private void drawScanHistory(BattleModel model) {
        scanHistory.add(model.me.getRadarHeading());
        if (scanHistory.size() > 8) {
            scanHistory.removeFirst();
        }

        if (Canvas.RADAR.enabled()) {
            final ColorFactory cf = new ColorFactory(0, scanHistory.size() - 1, new Color(0, 255, 0, 50), new Color(0, 255, 0, 225));

            int idx = 0;
            for (Double radarHeading : scanHistory) {
                final Color color = cf.getColor(idx);
                final CaPoint to = model.me.getPosition().project(radarHeading, Rules.RADAR_SCAN_RADIUS);
                Canvas.RADAR.draw(new Line(model.me.getPosition(), to), color);
                idx++;
            }
        }
    }

    private CaRobot selectEnemyToScan(BattleModel model) {
        CaRobot enemyToScan = null;

        for (CaRobot robot : model.aliveEnemies) {
            if (enemyToScan == null || enemyToScan.getLastScanTime() > robot.getLastScanTime()) {
                enemyToScan = robot;
            }
        }

        if (Log.isDebugEnabled()) {
            Log.debug("Selected enemy to scan: " + enemyToScan.getName());
        }

        return enemyToScan;
    }

    private double selectScanDirection(BattleModel model) {
        int cwEnemies = 0;
        int ccwEnemies = 0;

        for (CaRobot robot : model.aliveEnemies) {
            final double alphaToEnemy = model.me.angleTo(robot);
            final double alpha = Utils.normalRelativeAngle(alphaToEnemy - model.me.getRadarHeading());
            if (alpha >= 0) {
                cwEnemies++;
            } else {
                ccwEnemies++;
            }
        }

        final double cwAngle = toDegrees(Utils.normalAbsoluteAngle(model.me.angleTo(enemyToScan) - model.me.getRadarHeading()));
        final double ccwAngle = toDegrees(Utils.normalAbsoluteAngle(model.me.getRadarHeading() - model.me.angleTo(enemyToScan)));
        if (Log.isDebugEnabled()) {
            Log.debug("Radar heading: " + Math.toDegrees(model.me.getRadarHeading()) +
                    "\ncwEnemies: " + cwEnemies + ", ccwEnemies: " + ccwEnemies +
                    "\ncwAngle = " + cwAngle + ", ccwAngle = " + ccwAngle);
        }

        if (cwEnemies / cwAngle >= ccwEnemies / ccwAngle) {
            return 1;
        } else {
            return -1;
        }
    }

}
