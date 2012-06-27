package lxx.model;

import lxx.BattleConstants;
import lxx.ConceptA;
import lxx.events.WavePassedEvent;
import lxx.util.CaConstants;
import lxx.util.CaPoint;
import lxx.util.CaUtils;
import lxx.util.IntervalDouble;

import java.util.*;

import static java.lang.Math.min;
import static java.lang.StrictMath.max;

/**
 * User: Aleksey Zhidkov
 * Date: 18.06.12
 */
public class Wave {

    private static final double NO_HIT_INTERVAL = 999;

    private final Map<String, IntervalDouble> remainingTargets = new HashMap<String, IntervalDouble>();

    public final BattleModel stateAtLaunchTime;
    public final double speed;
    public final CaRobot owner;

    public Wave(BattleModel stateAtLaunchTime, double speed, CaRobot owner) {
        this.stateAtLaunchTime = stateAtLaunchTime;
        this.speed = speed;
        this.owner = owner;

        for (CaRobot robot : stateAtLaunchTime.getRobots()) {
            if (owner.equals(robot)) {
                continue;
            }
            remainingTargets.put(robot.getName(), new IntervalDouble(NO_HIT_INTERVAL, NO_HIT_INTERVAL));
        }
    }

    public List<WavePassedEvent> check(BattleModel model) {
        final List<WavePassedEvent> events = new LinkedList<WavePassedEvent>();
        final double travelledDistance = (ConceptA.currentTime - stateAtLaunchTime.time) * speed;
        for (String rt : remainingTargets.keySet()) {
            final CaRobot robot = model.getRobot(rt);
            // todo: check robot is alive
            final double alpha = owner.angleTo(robot);
            final CaPoint bltPnt = owner.project(alpha, travelledDistance);
            if (BattleConstants.isRobotContains(robot.getPosition(), bltPnt)) {
                final double halfRobotWidthRadians = CaUtils.getRobotWidthInRadians(owner.getPosition(), robot.getPosition()) / 2;

                final IntervalDouble hitInterval = remainingTargets.get(robot.getName());

                final double noBearingOffset = owner.angleTo(stateAtLaunchTime.getRobot(rt));
                final double currentBearingOffset = alpha - noBearingOffset;
                if (hitInterval.center() == NO_HIT_INTERVAL) {
                    hitInterval.a = alpha;
                    hitInterval.b = alpha;
                }
                final double minBo = min(currentBearingOffset - halfRobotWidthRadians, currentBearingOffset + halfRobotWidthRadians);
                final double maxBo = max(currentBearingOffset - halfRobotWidthRadians, currentBearingOffset + halfRobotWidthRadians);

                hitInterval.extend(minBo);
                hitInterval.extend(maxBo);
            } else if (remainingTargets.get(robot.getName()).center() == NO_HIT_INTERVAL) {
                events.add(new WavePassedEvent(this, robot, remainingTargets.remove(robot.getName())));
            }
        }

        return events;
    }

    public boolean hasRemainingTargets() {
        return remainingTargets.size() > 0;
    }
}
