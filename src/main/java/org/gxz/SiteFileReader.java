package org.gxz;

import org.gxz.data.WaveformData;
import org.gxz.decode.WaveformDataDecoder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.IntConsumer;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public class SiteFileReader implements Iterable<WaveformData> {

    private static final int DEFAULT_CAPACITY = 1024 * 1024; // aka 1MB

    private int capacity;

    private final File siteFile;

    private final List<SiteFileListener> listeners = new ArrayList<>();

    private boolean autoExpandBuffer = true;

    private boolean capacityChanged = false;

    public SiteFileReader(int capacity, File siteFile) {
        //  少于14字节根本读不到长度，甚至连扩容的机会都没有
        if (capacity <= 14) {
            capacity = DEFAULT_CAPACITY;
        }
        this.capacity = capacity;
        this.siteFile = siteFile;
    }

    public SiteFileReader(File siteFile) {
        this(DEFAULT_CAPACITY, siteFile);
    }

    public void disableAutoExpandBuffer() {
        autoExpandBuffer = false;
    }

    public void addListener(SiteFileListener listener) {
        listeners.add(listener);
    }

    public void read() {
        try (FileInputStream fileInputStream = new FileInputStream(siteFile)) {
            byte[] readBytes = new byte[capacity];

            int startOffset = 0;
            int expectLength = capacity - startOffset;
            int realRead;
            WaveformDataDecoder decoder = new WaveformDataDecoder();
            while ((realRead = fileInputStream.read(readBytes, startOffset, expectLength)) != -1) {
                ByteBuffer buffer = ByteBuffer.wrap(readBytes, 0, startOffset + realRead);
                startOffset = decodeAndPublishEvent(buffer, decoder);
                //  如果期待的字节数和实际读到的字节数不一样，说明读完了
                if (expectLength != realRead) {
                    break;
                }
                //  更新下一次期待读取的长度
                expectLength = capacity - startOffset;
                if (capacityChanged) {
                    byte[] newReadBytes = new byte[capacity];
                    System.arraycopy(readBytes, buffer.position(), newReadBytes, 0, startOffset);
                    readBytes = newReadBytes;
                    capacityChanged = false;
                    continue;
                }
                System.arraycopy(readBytes, buffer.position(), readBytes, 0, startOffset);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * @return 返回半包数据的字节数，也就是整个buffer 剩下的字节数
     **/
    private int decodeAndPublishEvent(ByteBuffer buffer, WaveformDataDecoder decoder) {
        while (!halfPackage(buffer, this::expand)) {
            WaveformData data = decoder.decode(buffer);
            listeners.forEach(listener -> listener.onWaveformData(data));
        }
        return buffer.remaining();
    }

    private void expand(int newCapacity) {
        this.capacity = newCapacity;
        this.capacityChanged = true;
    }

    private boolean halfPackage(ByteBuffer buffer, IntConsumer expandAction) {
        //  如果没有14字节，说明一定是半包
        if (buffer.remaining() < 14) {
            return true;
        }
        buffer.getShort();
        buffer.getShort();
        buffer.getShort();
        int length = buffer.getInt();
        int newCapacity = checkCapacityAndExpand(length, buffer.capacity());
        if (newCapacity != buffer.capacity()) {
            //  如果需要扩容 一定是半包
            buffer.position(buffer.position() - 10);
            expandAction.accept(newCapacity);
            return true;
        }
        //  如果剩下的字节数不够一个完整的数据包，说明是半包，直接返回，但是要把之前读出来的补上
        if (buffer.remaining() < length - 10) {
            buffer.position(buffer.position() - 10);
            return true;
        }
        // 够一个完整的数据包，把读出来的offset补回去
        buffer.position(buffer.position() - 10);
        return false;
    }


    /**
     * 返回新的capacity
     **/
    private int checkCapacityAndExpand(int dataLength, int currentCapacity) {
        if (dataLength <= currentCapacity) {
            return currentCapacity;
        }
        if (!autoExpandBuffer) {
            throw new BufferTooShortExexption(currentCapacity, dataLength);
        }
        //  copy from HashMap
        int n = -1 >>> Integer.numberOfLeadingZeros(dataLength - 1);
        return n + 1;

    }

    @Override
    public Iterator<WaveformData> iterator() {
        try {
            return new Itr();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    class Itr implements Iterator<WaveformData> {

        private final WaveformDataDecoder decoder = new WaveformDataDecoder();

        private byte[] readBytes = new byte[capacity];

        private ByteBuffer buffer;

        private final FileInputStream fileInputStream = new FileInputStream(siteFile);

        private boolean completed;

        private WaveformData nextData;

        Itr() throws IOException {
            int length = fileInputStream.read(readBytes);
            if (length != capacity) {
                completed = true;
            }
            buffer = ByteBuffer.wrap(readBytes, 0, length);
        }

        /**
         * 如果读出了数据，返回true，否则返回false
         **/
        private boolean doRead() {
            if (completed) {
                return false;
            }
            System.arraycopy(buffer.array(), buffer.position(), readBytes, 0, buffer.remaining());
            try {
                int expectLength = readBytes.length - buffer.remaining();
                int readLength = fileInputStream.read(readBytes, buffer.remaining(), expectLength);
                if (readLength != expectLength) {
                    completed = true;
                }
                if (readLength == -1) {
                    return false;
                }
                buffer = ByteBuffer.wrap(readBytes, 0, buffer.remaining() + readLength);
                return true;
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                if (completed) {
                    try {
                        fileInputStream.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }


        private void expand(int newCapacity) {
            readBytes = new byte[newCapacity];
        }

        @Override
        public boolean hasNext() {
            if (nextData != null) {
                return true;
            }
            //  不是半包，解码保存
            if (!halfPackage(buffer, this::expand)) {
                nextData = decoder.decode(buffer);
                return true;
            }
            if (!doRead()) {
                return false;
            }
            return hasNext();
        }

        @Override
        public WaveformData next() {
            if (nextData == null) {
                throw new NoSuchElementException("no more data");
            }
            WaveformData data = nextData;
            nextData = null;
            return data;
        }
    }


}
