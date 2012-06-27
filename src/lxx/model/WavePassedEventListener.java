package lxx.model;

import lxx.events.WavePassedEvent;

import java.util.List;

/**
 * User: Aleksey Zhidkov
 * Date: 18.06.12
 */
public interface WavePassedEventListener {

    String getOwner();

    void processEvents(List<WavePassedEvent> events);

}
