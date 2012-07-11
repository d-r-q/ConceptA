package lxx.data;

import ags.utils.KdTree;
import com.sun.jndi.toolkit.url.GenericURLContext;
import lxx.BattleConstants;
import lxx.TickListener;
import lxx.model.BattleModel;
import lxx.model.Wave;
import lxx.paint.Canvas;
import lxx.paint.Circle;
import lxx.paint.ColorFactory;
import lxx.services.BulletsServiceListener;
import lxx.util.CaPoint;
import lxx.util.CaUtils;
import robocode.Bullet;
import robocode.util.Utils;

import java.awt.*;
import java.util.*;
import java.util.List;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.signum;

/**
 * User: Aleksey Zhidkov
 * Date: 03.07.12
 */
public class MovementDataManager implements BulletsServiceListener, TickListener {

    private static final Map<String, SimpleDataSource> hitLogs = new HashMap<String, SimpleDataSource>();

    private final Map<Wave, LinkedList<BearingOffsetDanger>> bulletsCache = new HashMap<Wave, LinkedList<BearingOffsetDanger>>();

    @Override
    public void bulletIntercepted(Wave w, Bullet hittedBullet) {
        registerHit(w, hittedBullet);
    }

    @Override
    public void bulletHit(Wave w, Bullet hitBullet) {
        registerHit(w, hitBullet);
    }

    private void registerHit(Wave w, Bullet b) {
        if (!w.aimTimeState.hasDuelOpponent()) {
            return;
        }
        SimpleDataSource dataSource = getDataSource(w);

        final double aimTimeLatDir = CaUtils.getNonZeroLateralDirection(w.aimTimeState.duelOpponent.getPosition(), w.aimTimeState.me);
        final double aimTimeAlpha = w.aimTimeState.duelOpponent.angleTo(w.aimTimeState.me);
        dataSource.add(w.aimTimeState, new GuessFactor(Utils.normalRelativeAngle(b.getHeadingRadians() - aimTimeAlpha) / CaUtils.getMaxEscapeAngle(w.speed) * aimTimeLatDir));
    }

    private SimpleDataSource getDataSource(Wave w) {
        SimpleDataSource dataSource = hitLogs.get(w.owner.getName());

        if (dataSource == null) {
            dataSource = new SimpleDataSource(new SimpleEnemyMovement(BattleConstants.myName, w.owner.getName()));
            hitLogs.put(w.owner.getName(), dataSource);
        }
        return dataSource;
    }

    @Override
    public void bulletFired(Wave w) {
    }

    @Override
    public void bulletMissed(Wave w) {
    }

    public List<BearingOffsetDanger> getBullets(Wave w) {
        LinkedList<BearingOffsetDanger> bullets = bulletsCache.get(w);

        if (bullets == null) {
            final SimpleDataSource ds = getDataSource(w);
            final BattleModel aimTimeState = w.aimTimeState;
            final List<SimpleDataSource.Entry> entries = ds.get(aimTimeState);

            bullets = new LinkedList<BearingOffsetDanger>();

            double maxDist = 0;
            for (SimpleDataSource.Entry e : entries) {
                maxDist = max(maxDist, e.dist);
            }

            for (SimpleDataSource.Entry e : entries) {
                final double bo = e.gf * CaUtils.getMaxEscapeAngle(w.speed) * CaUtils.getNonZeroLateralDirection(aimTimeState.duelOpponent.getPosition(), aimTimeState.me);
                bullets.add(new BearingOffsetDanger(bo, 1 - e.dist / maxDist));
            }

            Collections.sort(bullets, new Comparator<BearingOffsetDanger>() {
                @Override
                public int compare(BearingOffsetDanger o1, BearingOffsetDanger o2) {
                    return (int) signum(o1.bo - o2.bo);
                }
            });

            if (bullets.size() == 0) {
                bullets.add(new BearingOffsetDanger(0, 1));
            }

            bulletsCache.put(w, bullets);
        }

        return bullets;
    }

    @Override
    public void tick() {
        if (Canvas.WAVES.enabled()) {
            for (Wave w : bulletsCache.keySet()) {
                if (!w.aimTimeState.hasDuelOpponent()) {
                    return;
                }
                final double baseAlpha = w.aimTimeState.duelOpponent.angleTo(w.aimTimeState.me);
                final List<BearingOffsetDanger> bullets = bulletsCache.get(w);
                double minDanger = Integer.MAX_VALUE;
                double maxDanger = Integer.MIN_VALUE;
                for (BearingOffsetDanger bod : bullets) {
                    minDanger = min(minDanger, bod.danger);
                    maxDanger = max(maxDanger, bod.danger);
                }
                final ColorFactory cf = new ColorFactory(minDanger, maxDanger, Color.BLUE, Color.RED);
                for (BearingOffsetDanger bod : bullets) {
                    final CaPoint bulletPos = w.startPos.project(Utils.normalAbsoluteAngle(baseAlpha + bod.bo), w.getTravelledDistance());
                    Canvas.WAVES.draw(new Circle(bulletPos, 2), cf.getColor(bod.danger));
                }
            }
        }
    }
}
