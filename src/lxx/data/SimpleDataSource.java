package lxx.data;

import lxx.model.BattleModel;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * User: Aleksey Zhidkov
 * Date: 09.07.12
 */
public class SimpleDataSource implements DataSource<BattleModel, GuessFactor, SimpleDataSource.Entry> {

    private final LinkedList<DataEntry> data = new LinkedList<DataEntry>();

    private final LocationFactory locationFactory;

    public SimpleDataSource(LocationFactory locationFactory) {
        this.locationFactory = locationFactory;
    }

    @Override
    public void add(BattleModel location, GuessFactor gf) {
        final double[] loc = locationFactory.getLocation(location);
        data.add(new DataEntry(loc, gf));
    }

    @Override
    public List<SimpleDataSource.Entry> get(BattleModel key) {
        final double[] loc = locationFactory.getLocation(key);
        final List<SimpleDataSource.Entry> res = new ArrayList<SimpleDataSource.Entry>(data.size());

        for (DataEntry e : data) {
            res.add(new Entry(dist(loc, e.location), e.gf.guessFactor));
        }

        return res;
    }

    private static double dist(double[] l1, double[] l2) {
        double res = 0;

        for (int i = 0; i < l1.length; i++) {
            final double dif = l1[i] - l2[i];
            res += dif * dif;
        }

        return res;
    }

    private class DataEntry {

        public final double[] location;
        public final GuessFactor gf;

        private DataEntry(double[] location, GuessFactor gf) {
            this.location = location;
            this.gf = gf;
        }
    }

    public class Entry {

        public final double dist;
        public final double gf;

        public Entry(double dist, double gf) {
            this.dist = dist;
            this.gf = gf;
        }
    }

}
