package com.pplive.liveplatform.core.record;



public class Cycle<A> {

    private A[] elems;
    private int read_index;
    private int write_index;

    @SuppressWarnings("unchecked")
    public Cycle(int cap) {
        elems = (A[]) new Object[cap + 1];
        read_index = write_index = 0;
    }

    public boolean full() {
        return write_index + 1 == read_index || (write_index + 1 == elems.length && read_index == 0);
    }

    public boolean empty() {
        return write_index == read_index;
    }

    public boolean push(A a) {
        if (full()) {
            //System.out.println("cycle full. write_index = " + write_index);
            return false;
        }
        elems[write_index] = a;
        int idx = write_index + 1;
        if (idx == elems.length) {
            idx = 0;
        }
        write_index = idx;
        return true;
    }

    public A pop() {
        A a = elems[read_index];
        if (empty()) {
            //System.out.println("cycle empty. write_index = " + write_index);
            return a;
        }
        elems[read_index] = null;
        int idx = read_index + 1;
        if (idx == elems.length) {
            idx = 0;
        }
        read_index = idx;
        return a;
    }
}
