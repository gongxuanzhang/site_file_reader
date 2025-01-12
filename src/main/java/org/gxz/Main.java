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
        reader.addListener((data) -> {
            System.out.println(data);
            readList.add(data);
        });
        reader.read();


        //  第二种 直接遍历
        for (WaveformData waveformData : reader) {
            System.out.println(waveformData);
            foreachList.add(waveformData);
        }
        
        //  验证正确性
        ByteBuffer buffer = ByteBuffer.wrap(Files.readAllBytes(file.toPath()));
        WaveformDataDecoder decoder = new WaveformDataDecoder();
        List<WaveformData> correctness = new ArrayList<>();
        while (buffer.hasRemaining()) {
            WaveformData data = decoder.decode(buffer);
            correctness.add(data);
        }
        if (!correctness.equals(readList) || !correctness.equals(foreachList)) {
            throw new IllegalArgumentException("解析错误");
        }
    }
}
