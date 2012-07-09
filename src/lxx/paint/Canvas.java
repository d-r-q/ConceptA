package lxx.paint;

import lxx.Config;

import java.awt.*;
import java.util.LinkedList;

/**
 * User: Aleksey Zhidkov
 * Date: 18.06.12
 */
public enum Canvas {

    RANDOM_MOVEMENT(true),
    MISK(true),
    WAVES(true),
    BULLET_HITS(false),
    WAVE_SURFING(true);

    private LinkedList<DrawCommand> drawables = new LinkedList<DrawCommand>();

    private final boolean autoReset;

    private boolean enabled = false;

    Canvas(boolean autoReset) {
        this.autoReset = autoReset;
    }

    public void switchEnabled() {
        enabled = !enabled;
    }

    public boolean enabled() {
        return enabled && Config.isPaintEnabled;
    }

    public void reset() {
        drawables.clear();
    }

    public void draw(Drawable d, Color c) {
        drawables.add(new DrawCommand(d, c));
    }

    public void exec(CaGraphics g) {
        if (drawables.size() == 0) {
            return;
        }

        for (DrawCommand dc : drawables) {
            g.setColor(dc.c);
            dc.d.draw(g);
        }

        if (autoReset) {
            reset();
        }
    }

    private class DrawCommand {

        private final Drawable d;
        private final Color c;

        private DrawCommand(Drawable d, Color c) {
            this.d = d;
            this.c = c;
        }
    }

}
