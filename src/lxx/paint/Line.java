package lxx.paint;

import lxx.util.CaPoint;

/**
 * User: Aleksey Zhidkov
 * Date: 04.07.12
 */
public class Line implements Drawable {

    private final CaPoint from;
    private final CaPoint to;

    public Line(CaPoint from, CaPoint to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public void draw(CaGraphics g) {
        g.drawLine(from, to);
    }
}
