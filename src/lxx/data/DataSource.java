package lxx.data;

import java.util.List;

/**
 * User: Aleksey Zhidkov
 * Date: 28.06.12
 */
public interface DataSource<K, V, R> {

    void add(K location, V value);

    List<R> get(K key);

}
