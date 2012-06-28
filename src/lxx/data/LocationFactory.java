package lxx.data;

import lxx.model.BattleModel;

/**
 * User: Aleksey Zhidkov
 * Date: 28.06.12
 */
public interface LocationFactory {

    int getDimensions();

    double[] getLocation(BattleModel battleModel);

}
