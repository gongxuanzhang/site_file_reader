package org.gxz.data;

import lombok.Data;

import java.util.Arrays;


/**
 * 定位仪波形数据实体类
 */
@Data
public class WaveformData {
    private int frameHeader1; // 帧头标识，固定值0x55AA
    private int frameHeader2; // 帧头标识，固定值0x55AA
    private int dataType; // 数据种类，表示波形数据类型
    private long dataLength; // 波形数据长度
    private long stationId; // 区站号
    private int frameCountHigh; // 当日帧计数高位
    private int frameCountLow; // 当日帧计数低位
    private int year; // 过阈值时间的年
    private int month; // 过阈值时间的月
    private int day; // 过阈值时间的日
    private int hour; // 过阈值时间的小时
    private int minute; // 过阈值时间的分钟
    private int second; // 过阈值时间的秒
    private int nanosecondHigh; // 过阈值时间的纳秒高位
    private int nanosecondLow; // 过阈值时间的纳秒低位
    private int processQualityCode1; // 一类过程质控码
    private int processQualityCode2; // 二类过程质控码
    private int externalQualityCode; // 外部质控码
    private byte[] waveformData; // 波形数据内容 动态
    private int waveformType; // 波形类型
    private int frameTail1; // 帧尾标识，固定值0x5A5A
    private int frameTail2; // 帧尾标识，固定值0x5A5A


    /**
     * 动态数据没打印  
     **/
    @Override
    public String toString() {
        return "WaveformData{" +
                "frameHeader1=" + frameHeader1 +
                ", frameHeader2=" + frameHeader2 +
                ", dataType=" + dataType +
                ", dataLength=" + dataLength +
                ", stationId=" + stationId +
                ", frameCountHigh=" + frameCountHigh +
                ", frameCountLow=" + frameCountLow +
                ", year=" + year +
                ", month=" + month +
                ", day=" + day +
                ", hour=" + hour +
                ", minute=" + minute +
                ", second=" + second +
                ", nanosecondHigh=" + nanosecondHigh +
                ", nanosecondLow=" + nanosecondLow +
                ", processQualityCode1=" + processQualityCode1 +
                ", processQualityCode2=" + processQualityCode2 +
                ", externalQualityCode=" + externalQualityCode +
                ", waveformType=" + waveformType +
                ", frameTail1=" + frameTail1 +
                ", frameTail2=" + frameTail2 +
                '}';
    }
}


