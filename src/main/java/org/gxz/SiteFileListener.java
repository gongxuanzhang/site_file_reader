package org.gxz;

import org.gxz.data.WaveformData;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
@FunctionalInterface
public interface SiteFileListener {


    void onWaveformData(WaveformData waveformData);

}
