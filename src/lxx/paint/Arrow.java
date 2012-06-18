package lxx.paint;

import lxx.util.CaPoint;

/**
 * User: Aleksey Zhidkov
 * Date: 18.06.12
 */
public class Arrow implements Drawable {

    private final CaPoint from;
    private final CaPoint to;
    private final double pikeWidth;

    public Arrow(CaPoint from, double alpha, double length, double pikeWidth) {
        this.from = from;
        this.to = from.project(alpha, length);
        this.pikeWidth = pikeWidth;
    }

    @Override
    public void draw(CaGraphics g) {
        final double angle = from.angleTo(to);
        final double arrowLength = from.distance(to);
        final CaPoint peakBase = from.project(angle, arrowLength - pikeWidth);

        final CaPoint empennageBase = from.project(angle, (double) pikeWidth);

        g.drawLine(from, peakBase);
        g.drawLine(empennageBase, angle + Math.PI / 2, (double) pikeWidth);
        g.drawLine(peakBase, angle + Math.PI / 2, pikeWidth);

        final CaPoint peakPnt1 = peakBase.project(robocode.util.Utils.normalAbsoluteAngle(angle + Math.PI / 2), pikeWidth / 2);
        final CaPoint peakPnt2 = peakBase.project(robocode.util.Utils.normalAbsoluteAngle(angle - Math.PI / 2), pikeWidth / 2);

        g.drawLine(peakPnt1, to);
        g.drawLine(peakPnt2, to);
    }
}
