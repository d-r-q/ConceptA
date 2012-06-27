package lxx.events;

import robocode.Bullet;

/**
 * User: Aleksey Zhidkov
 * Date: 18.06.12
 */
public class FireEvent {

    public final Bullet bullet;

    public FireEvent(Bullet bullet) {
        this.bullet = bullet;
    }
}
