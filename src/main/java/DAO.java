public interface DAO<T>
{
	void put(T item);
	T get(long id);
	void update(long id, T new_item);
	void delete(long id);
}
