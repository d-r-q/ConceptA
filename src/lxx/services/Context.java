package lxx.services;

import lxx.ConceptA;
import lxx.data.MovementDataManager;

/**
 * User: Aleksey Zhidkov
 * Date: 28.06.12
 */
public class Context {

    private final WavesService wavesService;
    private final BulletsService bulletsService;
    private final MovementDataManager movementDataManager;

    public Context(ConceptA conceptA) {
        wavesService = new WavesService();
        conceptA.addBattleModelListener(wavesService);

        bulletsService = new BulletsService(wavesService);
        conceptA.addBattleModelListener(bulletsService);

        movementDataManager = new MovementDataManager();
        bulletsService.addListener(movementDataManager);
        conceptA.addTickListener(movementDataManager);
    }

    public WavesService getWavesService() {
        return wavesService;
    }

    public BulletsService getBulletsService() {
        return bulletsService;
    }

    public MovementDataManager getMovementDataManager() {
        return movementDataManager;
    }
}
