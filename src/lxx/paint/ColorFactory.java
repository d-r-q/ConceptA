package lxx.paint;

import java.awt.*;
import java.awt.color.ColorSpace;

/**
 * User: Aleksey Zhidkov
 * Date: 09.07.12
 */
public class ColorFactory {

    private final double minValue;
    private final double maxValue;

    private final float[] minHsbVals;
    private final float[] maxHsbVals;

    public ColorFactory(double minValue, double maxValue, Color minColor, Color maxColor) {
        this.minValue = minValue;
        this.maxValue = maxValue;

        minHsbVals = Color.RGBtoHSB(minColor.getRed(), minColor.getGreen(), minColor.getBlue(), null);
        maxHsbVals = Color.RGBtoHSB(maxColor.getRed(), maxColor.getGreen(), maxColor.getBlue(), null);
    }

    public Color getColor(double value) {
        final double k = (value - minValue) / (maxValue - minValue);
        final double h = minHsbVals[0] + (maxHsbVals[0] - minHsbVals[0]) * k;
        final double s = minHsbVals[1] + (maxHsbVals[1] - minHsbVals[1]) * k;
        final double b = minHsbVals[2] + (maxHsbVals[2] - minHsbVals[2]) * k;

        return new Color(Color.HSBtoRGB((float)h, (float)s, (float)b));
    }

}
