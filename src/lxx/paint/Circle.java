package lxx.paint;

import lxx.util.CaPoint;

/**
 * User: Aleksey Zhidkov
 * Date: 28.06.12
 */
public class Circle implements Drawable {

    private final CaPoint center;
    private final double radius;

    public Circle(CaPoint center, double radius) {
        this.center = center;
        this.radius = radius;
    }

    @Override
    public void draw(CaGraphics g) {
        g.drawCircle(center, radius);
    }
}
