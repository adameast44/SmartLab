package com.me.adameastham.smartlab;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;

//Written by Adam Eastham

public class CircularBuffer {

    private int[] buffer;

    private int tail;

    private int head;

    private int size;

    public CircularBuffer(int n) {
        size = n;
        buffer = new int[size];
        tail = 0;
        head = 0;
    }

    //add a an element to buffer
    public void add(int toAdd) {
        if (head != (tail - 1)) {
            buffer[head++] = toAdd;
        } else {
            throw new BufferOverflowException();
        }
        head = head % buffer.length;
    }

    //remove a element from the front of the buffer
    public int get() {
        int t;
        int adjTail = tail > head ? tail - buffer.length : tail;
        if (adjTail < head) {
            t = buffer[tail++];
            tail = tail % buffer.length;
        } else {
            throw new BufferUnderflowException();
        }
        return t;
    }

    //empty the buffer
    public void empty(){
        buffer = new int[0];
    }

    //return a element from a specified position in the buffer
    public int peak(int x){
        return buffer[x];
    }

    //return the size of the buffer
    public int size(){
        return size;
    }

    public int getAverage(){
        int total = 0;
        for (int i=0; i<size; i++){
            total+=buffer[i];
        }
        return total/size;
    }
}
