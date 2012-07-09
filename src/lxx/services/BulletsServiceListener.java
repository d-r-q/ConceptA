package lxx.services;

import lxx.model.CaRobot;
import lxx.model.Wave;
import robocode.Bullet;

/**
 * User: Aleksey Zhidkov
 * Date: 04.07.12
 */
public interface BulletsServiceListener {

    void bulletFired(Wave w);

    void bulletMissed(Wave w);

    void bulletIntercepted(Wave w, Bullet hittedBullet);

    void bulletHit(Wave w, Bullet hitBullet);

}
