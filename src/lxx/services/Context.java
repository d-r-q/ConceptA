package lxx.services;

import lxx.ConceptA;

/**
 * User: Aleksey Zhidkov
 * Date: 28.06.12
 */
public class Context {

    private final WavesService wavesService;

    public Context(ConceptA conceptA) {
        wavesService = new WavesService();
        conceptA.addBattleModelListener(wavesService);
    }

    public WavesService getWavesService() {
        return wavesService;
    }
}
