package lxx.model;

import junit.framework.TestCase;

/**
 * User: jdev
 * Date: 08.07.12
 */
public class CaRobotStateFactoryTest extends TestCase {

    public void testGetNewVelocity() throws Exception {
        assertEquals(1.0, CaRobotStateFactory.getNewVelocity(0, 1));
        assertEquals(-0.5, CaRobotStateFactory.getNewVelocity(1, -1));
        assertEquals(0.75, CaRobotStateFactory.getNewVelocity(-0.5, 1));
        assertEquals(-0.625, CaRobotStateFactory.getNewVelocity(0.75, -1));
        assertEquals(0.6875, CaRobotStateFactory.getNewVelocity(-0.625, 1));

        assertEquals(8.0, CaRobotStateFactory.getNewVelocity(8, 10));
        assertEquals(8.0, CaRobotStateFactory.getNewVelocity(8, 8));
        assertEquals(7.0, CaRobotStateFactory.getNewVelocity(8, 7));
        assertEquals(6.0, CaRobotStateFactory.getNewVelocity(8, 0));
        assertEquals(6.0, CaRobotStateFactory.getNewVelocity(8, -8));

        assertEquals(4.0, CaRobotStateFactory.getNewVelocity(6, 0));
        assertEquals(4.0, CaRobotStateFactory.getNewVelocity(6, -8));

        assertEquals(0.0, CaRobotStateFactory.getNewVelocity(2, 0));
        assertEquals(0.0, CaRobotStateFactory.getNewVelocity(2, -8));

        assertEquals(-8.0, CaRobotStateFactory.getNewVelocity(-8, -10));
        assertEquals(-8.0, CaRobotStateFactory.getNewVelocity(-8, -8));
        assertEquals(-7.0, CaRobotStateFactory.getNewVelocity(-8, -7));
        assertEquals(-6.0, CaRobotStateFactory.getNewVelocity(-8, 0));
        assertEquals(-6.0, CaRobotStateFactory.getNewVelocity(-8, 8));

        assertEquals(-4.0, CaRobotStateFactory.getNewVelocity(-6, 0));
        assertEquals(-4.0, CaRobotStateFactory.getNewVelocity(-6, 8));

        assertEquals(0.0, CaRobotStateFactory.getNewVelocity(-2, 0));
        assertEquals(0.0, CaRobotStateFactory.getNewVelocity(-2, 8));
    }
}
