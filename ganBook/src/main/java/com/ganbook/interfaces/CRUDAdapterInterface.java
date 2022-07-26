package com.ganbook.interfaces;

import java.util.List;

/**
 * Created by dmytro_vodnik on 4/3/16.
 * working on WaiterApp project
 */
public interface CRUDAdapterInterface<T> {

    T getItem(int position);
    void addItem(T item);
    void addItem(T item, int index);
    void removeItem(T item);
    void addItems(List<T> items);
    void clearList();
    void updateItem(int pos, T item);
    void showItem(T item, int itemPosition);
    List<T> getItems();

    void removeItem(int currentItemPosition);
}
