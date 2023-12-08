package boulahri.app.dal;

import java.util.Vector;

public interface IDao <T>{
    public void delete(T thing);
    public void add(T thing);
    public Vector<T> getAll(); 
    public void update(T thing, T newTing);
}
