package lxx.util;

import lxx.model.CaRobot;
import wiki.FastMath;

import java.awt.geom.Point2D;

/**
 * User: Aleksey Zhidkov
 * Date: 18.06.12
 */
public class CaPoint extends Point2D {

    public double x;
    public double y;

    public CaPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public void setLocation(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public CaPoint project(double alpha, double distance) {
        return new CaPoint(x + FastMath.sin(alpha) * distance, y + FastMath.cos(alpha) * distance);
    }

    public double angleTo(CaPoint another) {
        return CaUtils.angle(this.x, this.y, another.x, another.y);
    }

}
