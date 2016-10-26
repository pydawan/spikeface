package org.oreto.spikeface.utils;


import org.oreto.spikeface.controllers.DataHeader;

import java.util.Comparator;
import java.util.Objects;

public class Sorter<T> implements Comparator<T> {

    private String sortField;
    private String sortOrder;

    public Sorter(String sortField, String sortOrder) {
        this.sortField = sortField;
        this.sortOrder = sortOrder;
    }

    public int compare(T o1, T o2) {
        String getter = String.format("get%s.capitalize()}", sortField);
        Comparable value1;
        Comparable value2;
        try {
            value1 = (Comparable) o1.getClass().getMethod(getter).invoke(o1);
            value2 = (Comparable) o2.getClass().getMethod(getter).invoke(o2);
            int value = value1.compareTo(value2);
            return Objects.equals(sortOrder, DataHeader.ascendingOrder) ? value : -1 * value;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
