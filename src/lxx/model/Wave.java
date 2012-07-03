package lxx.model;

import lxx.BattleConstants;
import lxx.ConceptA;
import lxx.events.WavePassedEvent;
import lxx.util.CaPoint;
import lxx.util.CaUtils;
import lxx.util.IntervalDouble;
import robocode.util.Utils;

import java.util.*;

import static java.lang.Math.min;
import static java.lang.StrictMath.max;

/**
 * User: Aleksey Zhidkov
 * Date: 18.06.12
 */
public class Wave {

    private static final double NO_HIT_INTERVAL = 999;

    private final Map<String, CaRobot> remainingTargets = new HashMap<String, CaRobot>();
    private final Map<String, IntervalDouble> hitIntervals = new HashMap<String, IntervalDouble>();

    public final BattleModel fireTimeState;
    public final double speed;
    public final CaRobotState owner;
    public final CaPoint startPos;
    public final long launchTime;

    public Wave(BattleModel fireTimeState, CaRobotState owner, double speed, CaRobot... targets) {
        this.fireTimeState = fireTimeState;
        this.speed = speed;
        this.owner = owner;
        this.startPos = owner.getPosition();
        this.launchTime = fireTimeState.time;

        for (CaRobot robot : targets) {
            hitIntervals.put(robot.getName(), new IntervalDouble(NO_HIT_INTERVAL, NO_HIT_INTERVAL));
            remainingTargets.put(robot.getName(), robot);
        }
    }

    public List<WavePassedEvent> check(BattleModel model) {
        final List<WavePassedEvent> events = new LinkedList<WavePassedEvent>();
        final double travelledDistance = getTravelledDistance();
        for (String remainingTargetName : remainingTargets.keySet()) {
            final CaRobot robot = model.getRobot(remainingTargetName);
            // todo: check robot is alive
            final double alpha = startPos.angleTo(robot.getPosition());
            final CaPoint bltPnt = startPos.project(alpha, travelledDistance);
            if (BattleConstants.isRobotContains(robot.getPosition(), bltPnt)) {
                final double halfRobotWidthRadians = CaUtils.getRobotWidthInRadians(startPos, robot.getPosition()) / 2;

                final IntervalDouble hitInterval = hitIntervals.get(robot.getName());

                final double noBearingOffset = startPos.angleTo(remainingTargets.get(remainingTargetName).getPosition());
                final double currentBearingOffset = Utils.normalRelativeAngle(alpha - noBearingOffset);
                if (hitInterval.center() == NO_HIT_INTERVAL) {
                    hitInterval.a = currentBearingOffset;
                    hitInterval.b = currentBearingOffset;
                }
                final double ccwEdge = currentBearingOffset - halfRobotWidthRadians;
                final double cwEdge = currentBearingOffset + halfRobotWidthRadians;
                final double minBo = min(ccwEdge, cwEdge);
                final double maxBo = max(ccwEdge, cwEdge);

                hitInterval.extend(minBo);
                hitInterval.extend(maxBo);

            } else if (hitIntervals.get(robot.getName()).center() != NO_HIT_INTERVAL) {
                events.add(new WavePassedEvent(this, robot, hitIntervals.remove(robot.getName())));
                remainingTargets.remove(robot.getName());
            }
        }

        return events;
    }

    public double getTravelledDistance() {
        return (ConceptA.currentTime - launchTime) * speed;
    }

    public boolean hasRemainingTargets() {
        return hitIntervals.size() > 0;
    }
}
