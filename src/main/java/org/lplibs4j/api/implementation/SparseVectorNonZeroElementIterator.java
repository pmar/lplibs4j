package org.lplibs4j.api.implementation;

import org.lplibs4j.api.util.NonZeroElementIterator;

public class SparseVectorNonZeroElementIterator implements
        NonZeroElementIterator {

    /**
     * @param actualrow
     */
    public SparseVectorNonZeroElementIterator(SparseVector actualrow) {
        super();
        this.actualrow = actualrow;
    }

    SparseVector actualrow;
    int i;
    int index;

    public int getActuali() {
        if (!this.actualrow.linevector) return 0;
        return i;
    }

    public int getActualj() {
        if (this.actualrow.linevector) return 0;
        return i;
    }

    public boolean hasNext() {
        return (index < actualrow.used);
    }

    public Double next() {
        this.i = actualrow.index[this.index];
        return actualrow.data[this.index++];
    }

    public void remove() {
        // TODO Auto-generated method stub

    }

}
