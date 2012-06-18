package lxx.model;

import robocode.ScannedRobotEvent;
import robocode.StatusEvent;

import java.util.List;

/**
 * User: Aleksey Zhidkov
 * Date: 18.06.12
 */
public class TurnEvents {

    public final List<ScannedRobotEvent> scannedRobotEvents;

    public final StatusEvent statusEvent;

    public TurnEvents(List<ScannedRobotEvent> scannedRobotEvents, StatusEvent statusEvent) {
        this.scannedRobotEvents = scannedRobotEvents;
        this.statusEvent = statusEvent;
    }

}
