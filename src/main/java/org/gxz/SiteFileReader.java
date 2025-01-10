package org.gxz;

import org.gxz.data.WaveformData;
import org.gxz.decode.WaveformDataDecoder;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public class SiteFileReader implements Iterable<WaveformData> {

    private static final int DEFAULT_CAPACITY = 1024 * 1024; // aka 1MB

    private final int capacity;

    private final File siteFile;

    private final List<SiteFileListener> listeners = new ArrayList<>();

    public SiteFileReader(int capacity, File siteFile) {
        this.capacity = capacity;
        this.siteFile = siteFile;
    }

    public SiteFileReader(File siteFile) {
        this(DEFAULT_CAPACITY, siteFile);
    }

    public void addListener(SiteFileListener listener) {
        listeners.add(listener);
    }

    public void read() {
        System.out.println("start read file");
        try (FileInputStream fileInputStream = new FileInputStream(siteFile)) {
            byte[] readBytes = new byte[capacity];

            int startOffset = 0;
            int shouldRead = capacity - startOffset;
            int realRead;
            WaveformDataDecoder decoder = new WaveformDataDecoder();
            while ((realRead = fileInputStream.read(readBytes, startOffset, shouldRead)) != -1) {
                ByteBuffer buffer = ByteBuffer.wrap(readBytes, 0, startOffset + realRead);
                startOffset = decodeAndPublishEvent(buffer, decoder);
                //  如果期待的字节数和实际读到的字节数不一样，说明读完了
                if (shouldRead != realRead) {
                    break;
                }
                //  如果没读完，把半包的字节移到最前面，继续循环
                System.arraycopy(readBytes, buffer.position(), readBytes, 0, startOffset);
                shouldRead = capacity - startOffset;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * @return 返回半包数据的字节数，也就是整个buffer 剩下的字节数
     **/
    private int decodeAndPublishEvent(ByteBuffer buffer, WaveformDataDecoder decoder) {
        while (true) {
            //  如果没有14字节，说明一定是半包，把整下的直接返回就完事
            if (buffer.remaining() < 14) {
                return buffer.remaining();
            }
            buffer.getShort();
            buffer.getShort();
            buffer.getShort();
            int length = buffer.getInt();

            //  如果剩下的字节数不够一个完整的数据包，说明是半包，直接返回，但是要把之前读出来的补上
            if (buffer.remaining() < length - 10) {
                buffer.position(buffer.position() - 10);
                return buffer.remaining();
            }
            //  把读出来的offset补回去
            buffer.position(buffer.position() - 10);
            WaveformData data = decoder.decode(buffer);
            listeners.forEach(listener -> listener.onWaveformData(data));
        }
    }

    @Override
    public Iterator<WaveformData> iterator() {
        return null;
    }
    
    
    
    class Itor implements Iterator<WaveformData>{

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public WaveformData next() {
            return null;
        }
    }
    
}
