package system.dao.api;

public interface Dao<K, E> {
    void create(E entity);
    void remove(E entity);
    void update(E entity);
    E findById(K id);
}
