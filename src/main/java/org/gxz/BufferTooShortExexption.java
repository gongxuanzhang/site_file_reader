package org.gxz;


public class BufferTooShortExexption extends RuntimeException {
    public BufferTooShortExexption(int capacity, int dataLength) {
        super("Buffer capacity is too short, need " + dataLength + " but only " + capacity);
    }
}
