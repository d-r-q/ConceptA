package lxx.events;

import robocode.Bullet;
import robocode.Event;

/**
 * User: Aleksey Zhidkov
 * Date: 18.06.12
 */
public class FireEvent extends Event {

    private final long time;

    public final Bullet bullet;

    public FireEvent(long time, Bullet bullet) {
        this.time = time;
        this.bullet = bullet;
    }

    public long getTime() {
        return time;
    }
}
