package org.gxz;


import org.gxz.data.WaveformData;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        //  两种方法使用
        //  第一种 直接读
        SiteFileReader reader = new SiteFileReader(new File("/Users/gongxuanzhang/Downloads/B2024-08-21 _1.dat"));
        reader.addListener((data) -> {
            System.out.println(data);
        });
        reader.read();


        //  第二种 直接遍历
        for (WaveformData waveformData : reader) {
            System.out.println(waveformData);
        }
    }
}
