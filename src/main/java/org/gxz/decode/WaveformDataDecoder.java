package org.gxz.decode;

import org.gxz.data.WaveformData;

import java.nio.ByteBuffer;

public class WaveformDataDecoder implements Decoder<WaveformData> {

    public WaveformData decode(ByteBuffer buffer) {
        WaveformData data = new WaveformData();
        data.setFrameHeader1(buffer.getShort() & 0xFFFF);
        data.setFrameHeader2(buffer.getShort() & 0xFFFF);
        data.setDataType(buffer.getShort() & 0xFFFF);
        data.setDataLength(buffer.getInt() & 0xFFFFFFFFL);
        data.setStationId(buffer.getInt() & 0xFFFFFFFFL);
        data.setFrameCountHigh(buffer.getShort() & 0xFFFF);
        data.setFrameCountLow(buffer.getShort() & 0xFFFF);
        data.setYear(buffer.getShort() & 0xFFFF);
        data.setMonth(buffer.getShort() & 0xFFFF);
        data.setDay(buffer.getShort() & 0xFFFF);
        data.setHour(buffer.getShort() & 0xFFFF);
        data.setMinute(buffer.getShort() & 0xFFFF);
        data.setSecond(buffer.getShort() & 0xFFFF);
        data.setNanosecondHigh(buffer.getShort() & 0xFFFF);
        data.setNanosecondLow(buffer.getShort() & 0xFFFF);
        data.setProcessQualityCode1(buffer.getShort() & 0xFFFF);
        data.setProcessQualityCode2(buffer.getShort() & 0xFFFF);
        data.setExternalQualityCode(buffer.getShort() & 0xFFFF);
        int waveformLength = ((int) data.getDataLength()) - 23 * 2;
        byte[] waveformData = new byte[waveformLength];
        buffer.get(waveformData);
        data.setWaveformData(waveformData);

        data.setWaveformType(buffer.getShort() & 0xFFFF);
        data.setFrameTail1(buffer.getShort() & 0xFFFF);
        data.setFrameTail2(buffer.getShort() & 0xFFFF);
        return data;
    }

}
