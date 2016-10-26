package org.oreto.spikeface.utils

import org.oreto.spikeface.controllers.common.DataHeader

public class Sorter<T> implements Comparator<T> {

    String sortField
    String sortOrder

    Sorter(String sortField, String sortOrder) {
        this.sortField = sortField
        this.sortOrder = sortOrder
    }

    public int compare(T o1, T o2) {
        String getter = "get${sortField.capitalize()}"
        Object value1 = o1.class.getMethod(getter).invoke(o1)
        Object value2 = o2.class.getMethod(getter).invoke(o2)

        int value = ((Comparable) value1) <=> value2
        sortOrder == DataHeader.ascendingOrder ? value : -1 * value
    }
}
