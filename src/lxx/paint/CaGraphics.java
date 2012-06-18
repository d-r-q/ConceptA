package lxx.paint;

import lxx.util.CaPoint;

import java.awt.*;

import static java.lang.Math.round;

/**
 * User: Aleksey Zhidkov
 * Date: 18.06.12
 */
public class CaGraphics {

    private final Graphics2D g;

    private Color color;

    public CaGraphics(Graphics2D g) {
        this.g = g;
    }

    public void drawLine(CaPoint from, CaPoint to) {
        g.drawLine((int) round(from.x), (int) round(from.y), (int) round(to.x), (int) round(to.y));
    }

    public void drawLine(CaPoint center, double angle, double length) {
        drawLine(center.project(angle, length / 2), center.project(robocode.util.Utils.normalAbsoluteAngle(angle - Math.PI), length / 2));
    }

    public void setColor(Color c) {
        if (c != null && !c.equals(color)) {
            g.setColor(c);
            color = c;
        }
    }
}
