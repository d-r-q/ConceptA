package lxx.movement;

/**
 * User: Aleksey Zhidkov
 * Date: 18.06.12
 */
public enum OrbitDirection {

    CLOCKWISE(1),
    COUNTER_CLOCKWISE(-1),
    STOP(0);

    public final int direction;

    private OrbitDirection(int direction) {
        this.direction = direction;
    }
}
