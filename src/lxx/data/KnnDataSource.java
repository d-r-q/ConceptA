package lxx.data;

import ags.utils.KdTree;
import lxx.model.BattleModel;

import java.util.List;

import static java.lang.Math.sqrt;

/**
 * User: Aleksey Zhidkov
 * Date: 28.06.12
 */
public class KnnDataSource<V> implements DataSource<BattleModel, V, KdTree.Entry<V>> {

    private final KdTree<V> tree;
    private final LocationFactory locationFactory;

    public KnnDataSource(LocationFactory locationFactory) {
        this.locationFactory = locationFactory;
        this.tree = new KdTree.Manhattan<V>(locationFactory.getDimensions(), 10000);
    }

    @Override
    public void add(BattleModel model, V value) {
        tree.addPoint(locationFactory.getLocation(model), value);
    }

    @Override
    public List<KdTree.Entry<V>> get(BattleModel model) {
        return tree.nearestNeighbor(locationFactory.getLocation(model), (int)sqrt(tree.size()), false);
    }
}
