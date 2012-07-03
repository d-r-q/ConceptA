package lxx.model;

import lxx.ConceptA;
import lxx.events.FireEvent;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;
import robocode.StatusEvent;

import java.util.List;
import java.util.Vector;

/**
 * User: Aleksey Zhidkov
 * Date: 18.06.12
 */
public class TurnEvents {

    public final List<ScannedRobotEvent> scannedRobotEvents;
    public final List<RobotDeathEvent> robotDeathEvents;

    public final ConceptA conceptA;
    public final StatusEvent statusEvent;
    public final FireEvent fireEvent;

    public TurnEvents(ConceptA conceptA, List<RobotDeathEvent> robotDeathEvents, List<ScannedRobotEvent> scannedRobotEvents, StatusEvent statusEvent, FireEvent fireEvent) {
        this.conceptA = conceptA;
        this.robotDeathEvents = robotDeathEvents;
        this.scannedRobotEvents = scannedRobotEvents;
        this.statusEvent = statusEvent;
        this.fireEvent = fireEvent;
    }

}
