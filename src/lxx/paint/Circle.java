package lxx.paint;

import lxx.util.CaPoint;

/**
 * User: Aleksey Zhidkov
 * Date: 28.06.12
 */
public class Circle implements Drawable {

    private final CaPoint center;
    private final double radius;
    private final boolean fill;

    public Circle(CaPoint center, double radius, boolean fill) {
        this.center = center;
        this.radius = radius;
        this.fill = fill;
    }

    public Circle(CaPoint center, double radius) {
        this(center, radius, false);
    }

    @Override
    public void draw(CaGraphics g) {
        if (fill) {
            g.fillCircle(center, radius);
        } else {
            g.drawCircle(center, radius);
        }
    }
}
