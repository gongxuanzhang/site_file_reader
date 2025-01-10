package org.gxz.decode;

import java.nio.ByteBuffer;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public interface Decoder<D> {
    
    /**
     * 解码一条数据，此参数的buffer保证至少拥有一条完整数据 
     **/
    D decode(ByteBuffer byteBuffer);
}
