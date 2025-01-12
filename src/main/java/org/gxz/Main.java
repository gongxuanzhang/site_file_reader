package org.gxz;


import org.gxz.data.WaveformData;
import org.gxz.decode.WaveformDataDecoder;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        List<WaveformData> readList = new ArrayList<>();
        List<WaveformData> foreachList = new ArrayList<>();
        //  两种方法使用
        //  第一种 直接读
        File file = new File("/Users/gongxuanzhang/Downloads/data.dat");
        SiteFileReader reader = new SiteFileReader(file);
        reader.addListener(readList::add);
        reader.read();


        //  第二种 直接遍历
        for (WaveformData waveformData : reader) {
            foreachList.add(waveformData);
        }
        
        //  创建只有64字节的buffer，看能不能读出来
        SiteFileReader smallReader = new SiteFileReader(64, file);
        List<WaveformData> smallReadList = new ArrayList<>();
        List<WaveformData> smallForeachList = new ArrayList<>();
        smallReader.addListener(smallReadList::add);
        smallReader.read();
        
        for (WaveformData waveformData : smallReader) {
            smallForeachList.add(waveformData);
        }

        //  验证正确性 就不写测试了，直接在main里面写了
        ByteBuffer buffer = ByteBuffer.wrap(Files.readAllBytes(file.toPath()));
        WaveformDataDecoder decoder = new WaveformDataDecoder();
        List<WaveformData> correctness = new ArrayList<>();
        while (buffer.hasRemaining()) {
            WaveformData data = decoder.decode(buffer);
            correctness.add(data);
        }
        if (!correctness.equals(readList)) {
            throw new IllegalArgumentException("read 解析错误");
        }
        if (!correctness.equals(foreachList)) {
            throw new IllegalArgumentException("foreach 解析错误");
        }
        if (!correctness.equals(smallReadList)) {
            throw new IllegalArgumentException("smallRead 解析错误");
        }
        if (!correctness.equals(smallForeachList)) {
            throw new IllegalArgumentException("smallForeach 解析错误");
        }
    }
}
